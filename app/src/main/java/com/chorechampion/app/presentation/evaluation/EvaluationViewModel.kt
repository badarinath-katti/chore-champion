package com.chorechampion.app.presentation.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.model.WeeklyEvaluation
import com.chorechampion.app.domain.repository.EvaluationRepository
import com.chorechampion.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val evaluationRepository: EvaluationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _evaluationState = MutableStateFlow<EvaluationState>(EvaluationState.Loading)
    val evaluationState: StateFlow<EvaluationState> = _evaluationState.asStateFlow()

    val recentEvaluations: StateFlow<List<WeeklyEvaluation>> = evaluationRepository
        .getRecentEvaluations(10)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadEvaluationForWeek(weekStartDate: Long) {
        viewModelScope.launch {
            _evaluationState.value = EvaluationState.Loading
            
            try {
                val evaluation = evaluationRepository.getEvaluationByWeek(weekStartDate)
                
                if (evaluation != null) {
                    // Load user details
                    val user1 = userRepository.getUserById(evaluation.user1Id).first()
                    val user2 = userRepository.getUserById(evaluation.user2Id).first()
                    
                    if (user1 != null && user2 != null) {
                        _evaluationState.value = EvaluationState.Success(
                            evaluation = evaluation,
                            user1 = user1,
                            user2 = user2
                        )
                    } else {
                        _evaluationState.value = EvaluationState.Error("Users not found")
                    }
                } else {
                    _evaluationState.value = EvaluationState.Error("No evaluation found for this week")
                }
            } catch (e: Exception) {
                _evaluationState.value = EvaluationState.Error(e.message ?: "Failed to load evaluation")
            }
        }
    }

    fun loadLatestEvaluation() {
        viewModelScope.launch {
            _evaluationState.value = EvaluationState.Loading
            
            try {
                val evaluations = evaluationRepository.getRecentEvaluations(1).first()
                val evaluation = evaluations.firstOrNull()
                
                if (evaluation != null) {
                    // Load user details
                    val user1 = userRepository.getUserById(evaluation.user1Id).first()
                    val user2 = userRepository.getUserById(evaluation.user2Id).first()
                    
                    if (user1 != null && user2 != null) {
                        _evaluationState.value = EvaluationState.Success(
                            evaluation = evaluation,
                            user1 = user1,
                            user2 = user2
                        )
                    } else {
                        _evaluationState.value = EvaluationState.Error("Users not found")
                    }
                } else {
                    _evaluationState.value = EvaluationState.Error("No evaluations yet")
                }
            } catch (e: Exception) {
                _evaluationState.value = EvaluationState.Error(e.message ?: "Failed to load evaluation")
            }
        }
    }
}

sealed class EvaluationState {
    object Loading : EvaluationState()
    data class Success(
        val evaluation: WeeklyEvaluation,
        val user1: User,
        val user2: User
    ) : EvaluationState()
    data class Error(val message: String) : EvaluationState()
}
