package com.chorechampion.app.presentation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.repository.AuthRepository
import com.chorechampion.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartnerSelectionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    val filteredUsers: StateFlow<List<User>> = combine(
        _allUsers,
        _searchQuery,
        _currentUser
    ) { users, query, currentUser ->
        if (query.isEmpty()) {
            users.filter { it.id != currentUser?.id }
        } else {
            users.filter { user ->
                user.id != currentUser?.id && (
                    user.name.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadUsers()
        observeFilteredUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            try {
                // Load all users first
                userRepository.getAllUsers().first().let { users ->
                    _allUsers.value = users
                    
                    // Load current user - use same logic as ChallengeViewModel
                    // Try auth repository first, fall back to first user
                    val currentUserId = authRepository.getCurrentUserId()
                    val currentUserSet = if (currentUserId != null) {
                        userRepository.getUserById(currentUserId).first()?.let { user ->
                            _currentUser.value = user
                            true
                        } ?: false
                    } else {
                        false
                    }
                    
                    if (!currentUserSet && users.isNotEmpty()) {
                        _currentUser.value = users.first()
                    }
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Failed to load users")
            }
        }
    }

    private fun observeFilteredUsers() {
        viewModelScope.launch {
            filteredUsers.collect { users ->
                _searchState.value = when {
                    users.isEmpty() -> SearchState.Empty
                    else -> SearchState.Success
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshUsers() {
        loadUsers()
    }
}

sealed class SearchState {
    object Loading : SearchState()
    object Empty : SearchState()
    object Success : SearchState()
    data class Error(val message: String) : SearchState()
}
