package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.WeeklyEvaluationDao
import com.chorechampion.app.data.local.entity.WeeklyEvaluationEntity
import com.chorechampion.app.domain.model.WeeklyEvaluation
import com.chorechampion.app.domain.repository.EvaluationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EvaluationRepositoryImpl @Inject constructor(
    private val evaluationDao: WeeklyEvaluationDao
) : EvaluationRepository {

    override suspend fun getEvaluationByWeek(weekStart: Long): WeeklyEvaluation? {
        return evaluationDao.getEvaluationByWeek(weekStart)?.toDomain()
    }

    override fun getEvaluationByWeekFlow(weekStart: Long): Flow<WeeklyEvaluation?> {
        return evaluationDao.getEvaluationByWeekFlow(weekStart).map { it?.toDomain() }
    }

    override fun getAllEvaluations(): Flow<List<WeeklyEvaluation>> {
        return evaluationDao.getAllEvaluations().map { list -> list.map { it.toDomain() } }
    }

    override fun getRecentEvaluations(limit: Int): Flow<List<WeeklyEvaluation>> {
        return evaluationDao.getRecentEvaluations(limit).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertEvaluation(evaluation: WeeklyEvaluation) {
        evaluationDao.insertEvaluation(evaluation.toEntity())
    }
}

// Mappers
fun WeeklyEvaluationEntity.toDomain() = WeeklyEvaluation(
    id = id,
    weekStartDate = weekStartDate,
    weekEndDate = weekEndDate,
    user1Id = user1Id,
    user1Points = user1Points,
    user2Id = user2Id,
    user2Points = user2Points,
    winnerId = winnerId,
    evaluatedAt = evaluatedAt
)

fun WeeklyEvaluation.toEntity() = WeeklyEvaluationEntity(
    id = id,
    weekStartDate = weekStartDate,
    weekEndDate = weekEndDate,
    user1Id = user1Id,
    user1Points = user1Points,
    user2Id = user2Id,
    user2Points = user2Points,
    winnerId = winnerId,
    evaluatedAt = evaluatedAt
)
