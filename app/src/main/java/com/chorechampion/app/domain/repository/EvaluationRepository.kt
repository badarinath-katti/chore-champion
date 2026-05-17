package com.chorechampion.app.domain.repository

import com.chorechampion.app.domain.model.WeeklyEvaluation
import kotlinx.coroutines.flow.Flow

interface EvaluationRepository {
    suspend fun getEvaluationByWeek(weekStart: Long): WeeklyEvaluation?
    fun getEvaluationByWeekFlow(weekStart: Long): Flow<WeeklyEvaluation?>
    fun getAllEvaluations(): Flow<List<WeeklyEvaluation>>
    fun getRecentEvaluations(limit: Int): Flow<List<WeeklyEvaluation>>
    suspend fun insertEvaluation(evaluation: WeeklyEvaluation)
}
