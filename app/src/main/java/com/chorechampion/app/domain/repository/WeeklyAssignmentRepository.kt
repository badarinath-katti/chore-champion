package com.chorechampion.app.domain.repository

import com.chorechampion.app.data.local.entity.AssignmentStatus
import com.chorechampion.app.domain.model.ChoreCompletion
import com.chorechampion.app.domain.model.WeeklyAssignment
import kotlinx.coroutines.flow.Flow

interface WeeklyAssignmentRepository {
    fun getAssignmentsForWeek(weekStart: Long): Flow<List<WeeklyAssignment>>
    fun getAssignmentsForUserAndWeek(userId: String, weekStart: Long): Flow<List<WeeklyAssignment>>
    fun getAssignmentsByChallenge(challengeId: String): Flow<List<WeeklyAssignment>>
    suspend fun getAssignmentById(assignmentId: String): WeeklyAssignment?
    suspend fun insertAssignment(assignment: WeeklyAssignment)
    suspend fun insertAssignments(assignments: List<WeeklyAssignment>)
    suspend fun updateAssignmentStatus(assignmentId: String, status: AssignmentStatus)
    
    // Completions
    suspend fun getCompletionByAssignment(assignmentId: String): ChoreCompletion?
    fun getCompletionByAssignmentFlow(assignmentId: String): Flow<ChoreCompletion?>
    suspend fun insertCompletion(completion: ChoreCompletion)
    suspend fun updateCompletion(completion: ChoreCompletion)
}
