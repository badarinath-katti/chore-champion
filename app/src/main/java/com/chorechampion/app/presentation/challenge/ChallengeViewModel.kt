package com.chorechampion.app.presentation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.Challenge
import com.chorechampion.app.domain.model.ChallengeStatus
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.repository.AuthRepository
import com.chorechampion.app.domain.repository.ChallengeRepository
import com.chorechampion.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _allChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val allChallenges: StateFlow<List<Challenge>> = _allChallenges.asStateFlow()

    private val _activeChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges.asStateFlow()

    private val _upcomingChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val upcomingChallenges: StateFlow<List<Challenge>> = _upcomingChallenges.asStateFlow()

    private val _completedChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val completedChallenges: StateFlow<List<Challenge>> = _completedChallenges.asStateFlow()

    private val _selectedChallenge = MutableStateFlow<Challenge?>(null)
    val selectedChallenge: StateFlow<Challenge?> = _selectedChallenge.asStateFlow()

    private val _availablePartners = MutableStateFlow<List<User>>(emptyList())
    val availablePartners: StateFlow<List<User>> = _availablePartners.asStateFlow()

    private val _selectedPartner = MutableStateFlow<User?>(null)
    val selectedPartner: StateFlow<User?> = _selectedPartner.asStateFlow()

    private val _createdChallenge = MutableStateFlow<Challenge?>(null)
    val createdChallenge: StateFlow<Challenge?> = _createdChallenge.asStateFlow()

    private val _joinChallengeError = MutableStateFlow<String?>(null)
    val joinChallengeError: StateFlow<String?> = _joinChallengeError.asStateFlow()

    private val _challengeCreator = MutableStateFlow<User?>(null)
    val challengeCreator: StateFlow<User?> = _challengeCreator.asStateFlow()

    private val _challengePartner = MutableStateFlow<User?>(null)
    val challengePartner: StateFlow<User?> = _challengePartner.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            // Get current authenticated user from Firebase
            val firebaseUser = authRepository.currentUser
            if (firebaseUser != null) {
                val user = userRepository.getUserByFirebaseUid(firebaseUser.uid)
                if (user != null) {
                    _currentUser.value = user
                    loadChallenges(user.id)
                    loadAvailablePartners(user.id)
                }
            }
        }
    }

    fun setCurrentUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId).first()?.let { user ->
                _currentUser.value = user
                loadChallenges(userId)
                loadAvailablePartners(userId)
            }
        }
    }

    private fun loadChallenges(userId: String) {
        viewModelScope.launch {
            challengeRepository.getChallengesByUser(userId).collect { challenges ->
                _allChallenges.value = challenges
            }
        }

        viewModelScope.launch {
            challengeRepository.getActiveChallenges(userId).collect { challenges ->
                _activeChallenges.value = challenges
            }
        }

        viewModelScope.launch {
            challengeRepository.getUpcomingChallenges(userId).collect { challenges ->
                _upcomingChallenges.value = challenges
            }
        }

        viewModelScope.launch {
            challengeRepository.getCompletedChallenges(userId).collect { challenges ->
                _completedChallenges.value = challenges
            }
        }
    }

    private fun loadAvailablePartners(currentUserId: String) {
        viewModelScope.launch {
            userRepository.getAllUsers().first().let { users ->
                _availablePartners.value = users.filter { it.id != currentUserId }
            }
        }
    }

    fun createChallenge(
        name: String,
        description: String?,
        partnerId: String?,
        startDate: Long,
        endDate: Long
    ) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            
            val status = if (System.currentTimeMillis() >= startDate) {
                ChallengeStatus.ACTIVE
            } else {
                ChallengeStatus.PENDING
            }

            val challenge = Challenge(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                creatorUserId = user.id,
                partnerUserId = partnerId,
                inviteCode = generateInviteCode(),
                startDate = startDate,
                endDate = endDate,
                status = status,
                createdAt = System.currentTimeMillis()
            )

            challengeRepository.createChallenge(challenge)
            _createdChallenge.value = challenge
        }
    }

    private fun generateInviteCode(): String {
        // Generate a 6-character alphanumeric code
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Removed similar characters
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }

    fun clearCreatedChallenge() {
        _createdChallenge.value = null
    }

    fun joinChallenge(inviteCode: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null) {
                _joinChallengeError.value = "Please sign in first"
                return@launch
            }

            val challenge = challengeRepository.getChallengeByInviteCode(inviteCode.uppercase())
            if (challenge == null) {
                _joinChallengeError.value = "Invalid invite code"
                return@launch
            }

            if (challenge.creatorUserId == user.id) {
                _joinChallengeError.value = "You cannot join your own challenge"
                return@launch
            }

            if (challenge.partnerUserId != null) {
                _joinChallengeError.value = "This challenge already has a partner"
                return@launch
            }

            challengeRepository.joinChallenge(challenge.id, user.id)
            _joinChallengeError.value = null
            // Reload challenges to show the newly joined one
            loadChallenges(user.id)
        }
    }

    fun clearJoinError() {
        _joinChallengeError.value = null
    }

    fun loadChallengeById(challengeId: String) {
        viewModelScope.launch {
            challengeRepository.getChallengeById(challengeId).collect { challenge ->
                _selectedChallenge.value = challenge
                // Load participants when challenge is loaded
                challenge?.let {
                    userRepository.getUserById(it.creatorUserId).first()?.let { user ->
                        _challengeCreator.value = user
                    }
                    if (it.partnerUserId != null) {
                        userRepository.getUserById(it.partnerUserId).first()?.let { user ->
                            _challengePartner.value = user
                        }
                    } else {
                        _challengePartner.value = null
                    }
                }
            }
        }
    }

    fun updateChallengeStatus(challengeId: String, status: ChallengeStatus) {
        viewModelScope.launch {
            challengeRepository.updateChallengeStatus(challengeId, status)
        }
    }

    fun deleteChallenge(challengeId: String) {
        viewModelScope.launch {
            challengeRepository.deleteChallenge(challengeId)
        }
    }

    fun endChallenge(challengeId: String) {
        updateChallengeStatus(challengeId, ChallengeStatus.COMPLETED)
    }

    fun cancelChallenge(challengeId: String) {
        updateChallengeStatus(challengeId, ChallengeStatus.CANCELLED)
    }

    fun getPartnerById(partnerId: String) {
        viewModelScope.launch {
            userRepository.getUserById(partnerId).first()?.let { user ->
                _selectedPartner.value = user
            }
        }
    }
}
