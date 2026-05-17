package com.chorechampion.app.presentation.completion

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.ChoreCompletion
import com.chorechampion.app.domain.model.WeeklyAssignment
import com.chorechampion.app.domain.repository.WeeklyAssignmentRepository
import com.chorechampion.app.domain.usecase.CompleteChoreUseCase
import com.chorechampion.app.util.ScoringCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletionViewModel @Inject constructor(
    private val completeChoreUseCase: CompleteChoreUseCase,
    private val assignmentRepository: WeeklyAssignmentRepository
) : ViewModel() {

    private val _completionState = MutableStateFlow<CompletionState>(CompletionState.Initial)
    val completionState: StateFlow<CompletionState> = _completionState.asStateFlow()

    private val _completionPercentage = MutableStateFlow(100)
    val completionPercentage: StateFlow<Int> = _completionPercentage.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _calculatedPoints = MutableStateFlow(0f)
    val calculatedPoints: StateFlow<Float> = _calculatedPoints.asStateFlow()

    fun updateCompletionPercentage(percentage: Int) {
        _completionPercentage.value = percentage.coerceIn(0, 100)
        recalculatePoints()
    }

    fun updateNotes(notes: String) {
        _notes.value = notes
    }

    fun setPhotoUri(uri: Uri?) {
        _photoUri.value = uri
    }

    fun calculatePoints(weightage: Int) {
        _calculatedPoints.value = ScoringCalculator.calculateFinalPoints(
            completionPercentage.value,
            weightage
        )
    }

    private fun recalculatePoints() {
        // Points will be recalculated with assignment weightage when submitting
    }

    fun completeChore(assignment: WeeklyAssignment) {
        viewModelScope.launch {
            _completionState.value = CompletionState.Loading
            
            val result = completeChoreUseCase(
                assignmentId = assignment.id,
                completionPercentage = _completionPercentage.value,
                weightage = assignment.targetWeightage,
                photoUri = _photoUri.value?.toString(),
                notes = _notes.value.ifBlank { null }
            )

            result.onSuccess { completion ->
                _completionState.value = CompletionState.Success(completion)
            }.onFailure { error ->
                _completionState.value = CompletionState.Error(error.message ?: "Failed to complete chore")
            }
        }
    }

    fun getExistingCompletion(assignmentId: String) {
        viewModelScope.launch {
            try {
                val completion = assignmentRepository.getCompletionByAssignment(assignmentId)
                completion?.let {
                    _completionPercentage.value = it.completionPercentage
                    _photoUri.value = it.photoUri?.let { uri -> Uri.parse(uri) }
                    _notes.value = it.notes ?: ""
                    _calculatedPoints.value = it.pointsAwarded
                }
            } catch (e: Exception) {
                // Completion doesn't exist yet, that's fine
            }
        }
    }

    fun resetState() {
        _completionState.value = CompletionState.Initial
        _completionPercentage.value = 100
        _photoUri.value = null
        _notes.value = ""
        _calculatedPoints.value = 0f
    }
}

sealed class CompletionState {
    object Initial : CompletionState()
    object Loading : CompletionState()
    data class Success(val completion: ChoreCompletion) : CompletionState()
    data class Error(val message: String) : CompletionState()
}
