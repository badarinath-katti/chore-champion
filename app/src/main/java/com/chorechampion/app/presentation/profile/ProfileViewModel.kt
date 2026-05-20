package com.chorechampion.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.repository.AuthRepository
import com.chorechampion.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _signOutSuccess = MutableStateFlow(false)
    val signOutSuccess: StateFlow<Boolean> = _signOutSuccess.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = authRepository.currentUser ?: return@launch
            val user = userRepository.getUserByFirebaseUid(firebaseUser.uid)
            _currentUser.value = user
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _signOutSuccess.value = true
        }
    }
}
