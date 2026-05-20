package com.chorechampion.app.presentation.assignments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chorechampion.app.data.local.entity.AssignmentStatus
import com.chorechampion.app.domain.model.Chore
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.model.WeeklyAssignment
import com.chorechampion.app.domain.repository.AuthRepository
import com.chorechampion.app.domain.repository.ChoreRepository
import com.chorechampion.app.domain.repository.UserRepository
import com.chorechampion.app.domain.repository.WeeklyAssignmentRepository
import com.chorechampion.app.util.WeekCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val assignmentRepository: WeeklyAssignmentRepository,
    private val choreRepository: ChoreRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _assignmentState = MutableStateFlow<AssignmentState>(AssignmentState.Loading)
    val assignmentState: StateFlow<AssignmentState> = _assignmentState.asStateFlow()

    private val currentUserId: String? = authRepository.getCurrentUserId()

    val currentWeekStart = MutableStateFlow(WeekCalculator.getWeekStart())

    val weeklyAssignments: StateFlow<List<WeeklyAssignment>> = currentWeekStart
        .flatMapLatest { weekStart ->
            assignmentRepository.getAssignmentsForWeek(weekStart)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val myAssignments: StateFlow<List<WeeklyAssignment>> = weeklyAssignments
        .map { assignments ->
            currentUserId?.let { userId ->
                assignments.filter { it.assignedToUserId == userId }
            } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val partnerAssignments: StateFlow<List<WeeklyAssignment>> = weeklyAssignments
        .map { assignments ->
            currentUserId?.let { userId ->
                assignments.filter { it.assignedToUserId != userId }
            } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _challengeAssignments = MutableStateFlow<List<WeeklyAssignment>>(emptyList())
    val challengeAssignments: StateFlow<List<WeeklyAssignment>> = _challengeAssignments.asStateFlow()

    fun loadAssignmentsForChallenge(challengeId: String) {
        viewModelScope.launch {
            assignmentRepository.getAssignmentsByChallenge(challengeId).collect { assignments ->
                _challengeAssignments.value = assignments
            }
        }
    }

    fun assignChoreToChallenge(
        choreId: String,
        userIds: List<String>,
        challengeId: String,
        startDate: Long,
        endDate: Long
    ) {
        viewModelScope.launch {
            try {
                val chore = choreRepository.getChoreById(choreId)
                val totalPoints = chore?.defaultWeightage ?: 10
                val pointsEach = totalPoints / userIds.size
                val remainder = totalPoints % userIds.size

                userIds.forEachIndexed { index, userId ->
                    val assignment = WeeklyAssignment(
                        id = UUID.randomUUID().toString(),
                        choreId = choreId,
                        assignedToUserId = userId,
                        weekStartDate = startDate,
                        weekEndDate = endDate,
                        // First user gets any remainder point(s) from integer division
                        targetWeightage = pointsEach + if (index == 0) remainder else 0,
                        status = AssignmentStatus.PENDING,
                        challengeId = challengeId,
                        createdAt = System.currentTimeMillis()
                    )
                    assignmentRepository.insertAssignment(assignment)
                }
                _assignmentState.value = AssignmentState.AssignmentCreated
            } catch (e: Exception) {
                _assignmentState.value = AssignmentState.Error(e.message ?: "Failed to assign chore to challenge")
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            try {
                val assignment = assignmentRepository.getAssignmentById(assignmentId)
                assignment?.let {
                    // You'll need to add a delete method to the repository
                    // For now, we can update status to SKIPPED as a workaround
                    assignmentRepository.updateAssignmentStatus(assignmentId, AssignmentStatus.SKIPPED)
                }
            } catch (e: Exception) {
                _assignmentState.value = AssignmentState.Error(e.message ?: "Failed to delete assignment")
            }
        }
    }

    fun createAssignment(
        choreId: String,
        assignedToUserId: String,
        weightage: Int
    ) {
        viewModelScope.launch {
            try {
                val weekStart = currentWeekStart.value
                val weekEnd = WeekCalculator.getWeekEnd()

                val assignment = WeeklyAssignment(
                    id = UUID.randomUUID().toString(),
                    choreId = choreId,
                    assignedToUserId = assignedToUserId,
                    weekStartDate = weekStart,
                    weekEndDate = weekEnd,
                    targetWeightage = weightage,
                    status = AssignmentStatus.PENDING,
                    createdAt = System.currentTimeMillis()
                )

                assignmentRepository.insertAssignment(assignment)
                _assignmentState.value = AssignmentState.AssignmentCreated
            } catch (e: Exception) {
                _assignmentState.value = AssignmentState.Error(e.message ?: "Failed to create assignment")
            }
        }
    }

    fun updateAssignmentStatus(assignmentId: String, status: AssignmentStatus) {
        viewModelScope.launch {
            try {
                assignmentRepository.updateAssignmentStatus(assignmentId, status)
            } catch (e: Exception) {
                _assignmentState.value = AssignmentState.Error(e.message ?: "Failed to update status")
            }
        }
    }

    fun getMyPoints(): StateFlow<Float> {
        return myAssignments
            .map { assignments ->
                assignments.sumOf { assignment ->
                    try {
                        val completion = assignmentRepository.getCompletionByAssignment(assignment.id)
                        completion?.pointsAwarded?.toDouble() ?: 0.0
                    } catch (e: Exception) {
                        0.0
                    }
                }.toFloat()
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0f)
    }

    fun getPartnerPoints(): StateFlow<Float> {
        return partnerAssignments
            .map { assignments ->
                assignments.sumOf { assignment ->
                    try {
                        val completion = assignmentRepository.getCompletionByAssignment(assignment.id)
                        completion?.pointsAwarded?.toDouble() ?: 0.0
                    } catch (e: Exception) {
                        0.0
                    }
                }.toFloat()
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0f)
    }
}

sealed class AssignmentState {
    object Loading : AssignmentState()
    object Success : AssignmentState()
    object AssignmentCreated : AssignmentState()
    data class Error(val message: String) : AssignmentState()
}
