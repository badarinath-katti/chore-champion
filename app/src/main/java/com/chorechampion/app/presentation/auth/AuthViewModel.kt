package com.chorechampion.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.repository.AuthRepository
import com.chorechampion.app.domain.repository.ChoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val choreRepository: ChoreRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isAuthenticated: Boolean
        get() = authRepository.isAuthenticated

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signIn(email, password)
                .onSuccess { user ->
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign in failed")
                }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUp(email, password, name)
                .onSuccess { user ->
                    // Initialize default categories for new user
                    choreRepository.initializeDefaultCategories()
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign up failed")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Initial
        }
    }

    fun resetState() {
        _authState.value = AuthState.Initial
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
