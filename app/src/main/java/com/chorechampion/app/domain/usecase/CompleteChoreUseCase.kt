package com.chorechampion.app.domain.usecase

import com.chorechampion.app.data.local.entity.AssignmentStatus
import com.chorechampion.app.domain.model.ChoreCompletion
import com.chorechampion.app.domain.repository.WeeklyAssignmentRepository
import com.chorechampion.app.util.ScoringCalculator
import java.util.UUID
import javax.inject.Inject

class CompleteChoreUseCase @Inject constructor(
    private val assignmentRepository: WeeklyAssignmentRepository
) {
    suspend operator fun invoke(
        assignmentId: String,
        completionPercentage: Int,
        weightage: Int,
        photoUri: String? = null,
        notes: String? = null
    ): Result<ChoreCompletion> {
        return try {
            val points = ScoringCalculator.calculateFinalPoints(completionPercentage, weightage)
            
            val completion = ChoreCompletion(
                id = UUID.randomUUID().toString(),
                weeklyAssignmentId = assignmentId,
                completionPercentage = completionPercentage,
                pointsAwarded = points,
                photoUri = photoUri,
                completedAt = System.currentTimeMillis(),
                notes = notes
            )
            
            // Save completion
            assignmentRepository.insertCompletion(completion)
            
            // Update assignment status
            assignmentRepository.updateAssignmentStatus(assignmentId, AssignmentStatus.COMPLETED)
            
            Result.success(completion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
