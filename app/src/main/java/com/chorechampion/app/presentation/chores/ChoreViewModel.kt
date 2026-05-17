package com.chorechampion.app.presentation.chores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.Chore
import com.chorechampion.app.domain.model.ChoreCategory
import com.chorechampion.app.domain.repository.AuthRepository
import com.chorechampion.app.domain.repository.ChoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChoreViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _choreState = MutableStateFlow<ChoreState>(ChoreState.Loading)
    val choreState: StateFlow<ChoreState> = _choreState.asStateFlow()

    val allChores: StateFlow<List<Chore>> = choreRepository.getAllChores()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allCategories: StateFlow<List<ChoreCategory>> = choreRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    val filteredChores: StateFlow<List<Chore>> = combine(
        allChores,
        _selectedCategoryFilter
    ) { chores, categoryFilter ->
        if (categoryFilter == null) {
            chores
        } else {
            chores.filter { it.categoryId == categoryFilter }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadChores()
    }

    fun loadChores() {
        viewModelScope.launch {
            _choreState.value = ChoreState.Loading
            try {
                _choreState.value = ChoreState.Success
            } catch (e: Exception) {
                _choreState.value = ChoreState.Error(e.message ?: "Failed to load chores")
            }
        }
    }

    fun filterByCategory(categoryId: String?) {
        _selectedCategoryFilter.value = categoryId
    }

    fun createChore(
        title: String,
        description: String?,
        categoryId: String,
        weightage: Int
    ) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch
                val chore = Chore(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    categoryId = categoryId,
                    defaultWeightage = weightage,
                    createdBy = userId,
                    createdAt = System.currentTimeMillis()
                )
                choreRepository.insertChore(chore)
                _choreState.value = ChoreState.ChoreCreated(chore)
            } catch (e: Exception) {
                _choreState.value = ChoreState.Error(e.message ?: "Failed to create chore")
            }
        }
    }

    fun updateChore(chore: Chore) {
        viewModelScope.launch {
            try {
                choreRepository.updateChore(chore)
                _choreState.value = ChoreState.ChoreUpdated
            } catch (e: Exception) {
                _choreState.value = ChoreState.Error(e.message ?: "Failed to update chore")
            }
        }
    }

    fun deleteChore(chore: Chore) {
        viewModelScope.launch {
            try {
                choreRepository.deleteChore(chore)
                _choreState.value = ChoreState.ChoreDeleted
            } catch (e: Exception) {
                _choreState.value = ChoreState.Error(e.message ?: "Failed to delete chore")
            }
        }
    }

    fun getChoreById(choreId: String): StateFlow<Chore?> {
        return choreRepository.getChoreByIdFlow(choreId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun resetState() {
        _choreState.value = ChoreState.Success
    }
}

sealed class ChoreState {
    object Loading : ChoreState()
    object Success : ChoreState()
    data class ChoreCreated(val chore: Chore) : ChoreState()
    object ChoreUpdated : ChoreState()
    object ChoreDeleted : ChoreState()
    data class Error(val message: String) : ChoreState()
}
