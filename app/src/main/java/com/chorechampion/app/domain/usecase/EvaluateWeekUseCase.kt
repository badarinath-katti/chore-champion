package com.chorechampion.app.domain.usecase

import com.chorechampion.app.domain.model.WeeklyEvaluation
import com.chorechampion.app.domain.repository.EvaluationRepository
import com.chorechampion.app.domain.repository.UserRepository
import com.chorechampion.app.domain.repository.WeeklyAssignmentRepository
import com.chorechampion.app.util.WeekCalculator
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject

class EvaluateWeekUseCase @Inject constructor(
    private val assignmentRepository: WeeklyAssignmentRepository,
    private val evaluationRepository: EvaluationRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        weekStartDate: Long,
        weekEndDate: Long,
        user1Id: String,
        user2Id: String
    ): Result<WeeklyEvaluation> {
        return try {
            // Get all assignments for the week for both users
            val user1Assignments = assignmentRepository.getAssignmentsForUserAndWeek(user1Id, weekStartDate).first()
            val user2Assignments = assignmentRepository.getAssignmentsForUserAndWeek(user2Id, weekStartDate).first()
            
            // Calculate points for user 1
            var user1Points = 0f
            for (assignment in user1Assignments) {
                val completion = assignmentRepository.getCompletionByAssignment(assignment.id)
                if (completion != null) {
                    user1Points += completion.pointsAwarded
                }
            }
            
            // Calculate points for user 2  
            var user2Points = 0f
            for (assignment in user2Assignments) {
                val completion = assignmentRepository.getCompletionByAssignment(assignment.id)
                if (completion != null) {
                    user2Points += completion.pointsAwarded
                }
            }
            
            // Determine winner
            val winnerId = when {
                user1Points > user2Points -> user1Id
                user2Points > user1Points -> user2Id
                else -> null // Tie
            }
            
            val evaluation = WeeklyEvaluation(
                id = UUID.randomUUID().toString(),
                weekStartDate = weekStartDate,
                weekEndDate = weekEndDate,
                user1Id = user1Id,
                user1Points = user1Points,
                user2Id = user2Id,
                user2Points = user2Points,
                winnerId = winnerId,
                evaluatedAt = System.currentTimeMillis()
            )
            
            evaluationRepository.insertEvaluation(evaluation)
            
            Result.success(evaluation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
