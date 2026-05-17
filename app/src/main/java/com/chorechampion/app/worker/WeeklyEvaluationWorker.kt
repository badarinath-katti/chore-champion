package com.chorechampion.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chorechampion.app.domain.repository.EvaluationRepository
import com.chorechampion.app.domain.repository.UserRepository
import com.chorechampion.app.domain.usecase.EvaluateWeekUseCase
import com.chorechampion.app.util.NotificationHelper
import com.chorechampion.app.util.WeekCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek

@HiltWorker
class WeeklyEvaluationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val evaluateWeekUseCase: EvaluateWeekUseCase,
    private val evaluationRepository: EvaluationRepository,
    private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get current user
            val userId = inputData.getString("user_id") ?: return Result.failure()
            val user = userRepository.getUserById(userId).first() ?: return Result.failure()
            
            // Get week start day (already a DayOfWeek in the User model)
            val dayOfWeek = user.weekStartDay
            
            // Calculate previous week (the week that just ended)
            val currentWeekStart = WeekCalculator.getWeekStart(dayOfWeek)
            val previousWeekStart = currentWeekStart - (7 * 24 * 60 * 60 * 1000) // 7 days ago
            val previousWeekEnd = currentWeekStart - 1 // Just before current week starts
            
            // Check if evaluation already exists for this week
            val existingEvaluation = evaluationRepository.getEvaluationByWeek(previousWeekStart)
            if (existingEvaluation != null) {
                // Already evaluated, skip
                return Result.success()
            }
            
            // Get partner
            val partner = user.partnerId?.let { partnerId ->
                userRepository.getUserById(partnerId).first()
            } ?: return Result.success() // No partner yet
            
            // Evaluate the week
            val result = evaluateWeekUseCase(
                weekStartDate = previousWeekStart,
                weekEndDate = previousWeekEnd,
                user1Id = user.id,
                user2Id = partner.id
            )
            
            result.onSuccess { evaluation ->
                // Determine winner
                val isUser1Winner = evaluation.winnerId == user.id
                val winnerName = if (isUser1Winner) user.name else partner.name
                val winnerPoints = if (isUser1Winner) evaluation.user1Points else evaluation.user2Points
                val loserPoints = if (isUser1Winner) evaluation.user2Points else evaluation.user1Points
                
                // Send notification
                NotificationHelper.sendEvaluationNotification(
                    context = applicationContext,
                    winnerName = winnerName,
                    winnerPoints = winnerPoints,
                    loserPoints = loserPoints,
                    weekStartDate = previousWeekStart
                )
                
                Result.success()
            }.onFailure {
                Result.retry()
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
