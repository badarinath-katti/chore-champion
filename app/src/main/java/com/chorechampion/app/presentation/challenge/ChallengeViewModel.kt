package com.chorechampion.app.presentation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.Challenge
import com.chorechampion.app.domain.model.ChallengeStatus
import com.chorechampion.app.domain.model.User
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
    private val userRepository: UserRepository
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

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            // TODO: Get current user ID from shared preferences or auth state
            // For now, we'll load the first user
            userRepository.getAllUsers().first().let { users ->
                if (users.isNotEmpty()) {
                    _currentUser.value = users.first()
                    loadChallenges(users.first().id)
                    loadAvailablePartners(users.first().id)
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
                startDate = startDate,
                endDate = endDate,
                status = status,
                createdAt = System.currentTimeMillis()
            )

            challengeRepository.createChallenge(challenge)
        }
    }

    fun loadChallengeById(challengeId: String) {
        viewModelScope.launch {
            challengeRepository.getChallengeById(challengeId).collect { challenge ->
                _selectedChallenge.value = challenge
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
