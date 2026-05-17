package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.ChoreCompletionDao
import com.chorechampion.app.data.local.dao.WeeklyAssignmentDao
import com.chorechampion.app.data.local.entity.AssignmentStatus
import com.chorechampion.app.data.local.entity.ChoreCompletionEntity
import com.chorechampion.app.data.local.entity.WeeklyAssignmentEntity
import com.chorechampion.app.domain.model.ChoreCompletion
import com.chorechampion.app.domain.model.WeeklyAssignment
import com.chorechampion.app.domain.repository.WeeklyAssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeeklyAssignmentRepositoryImpl @Inject constructor(
    private val assignmentDao: WeeklyAssignmentDao,
    private val completionDao: ChoreCompletionDao
) : WeeklyAssignmentRepository {

    override fun getAssignmentsForWeek(weekStart: Long): Flow<List<WeeklyAssignment>> {
        return assignmentDao.getAssignmentsForWeek(weekStart).map { list -> list.map { it.toDomain() } }
    }

    override fun getAssignmentsForUserAndWeek(userId: String, weekStart: Long): Flow<List<WeeklyAssignment>> {
        return assignmentDao.getAssignmentsForUserAndWeek(userId, weekStart).map { list -> list.map { it.toDomain() } }
    }

    override fun getAssignmentsByChallenge(challengeId: String): Flow<List<WeeklyAssignment>> {
        return assignmentDao.getAssignmentsByChallenge(challengeId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAssignmentById(assignmentId: String): WeeklyAssignment? {
        return assignmentDao.getAssignmentById(assignmentId)?.toDomain()
    }

    override suspend fun insertAssignment(assignment: WeeklyAssignment) {
        assignmentDao.insertAssignment(assignment.toEntity())
    }

    override suspend fun insertAssignments(assignments: List<WeeklyAssignment>) {
        assignmentDao.insertAssignments(assignments.map { it.toEntity() })
    }

    override suspend fun updateAssignmentStatus(assignmentId: String, status: AssignmentStatus) {
        assignmentDao.updateAssignmentStatus(assignmentId, status.name)
    }

    override suspend fun getCompletionByAssignment(assignmentId: String): ChoreCompletion? {
        return completionDao.getCompletionByAssignment(assignmentId)?.toDomain()
    }

    override fun getCompletionByAssignmentFlow(assignmentId: String): Flow<ChoreCompletion?> {
        return completionDao.getCompletionByAssignmentFlow(assignmentId).map { it?.toDomain() }
    }

    override suspend fun insertCompletion(completion: ChoreCompletion) {
        completionDao.insertCompletion(completion.toEntity())
    }

    override suspend fun updateCompletion(completion: ChoreCompletion) {
        completionDao.updateCompletion(completion.toEntity())
    }
}

// Mappers
fun WeeklyAssignmentEntity.toDomain() = WeeklyAssignment(
    id = id,
    choreId = choreId,
    assignedToUserId = assignedToUserId,
    weekStartDate = weekStartDate,
    weekEndDate = weekEndDate,
    targetWeightage = targetWeightage,
    status = AssignmentStatus.valueOf(status),
    challengeId = challengeId,
    createdAt = createdAt
)

fun WeeklyAssignment.toEntity() = WeeklyAssignmentEntity(
    id = id,
    choreId = choreId,
    assignedToUserId = assignedToUserId,
    weekStartDate = weekStartDate,
    weekEndDate = weekEndDate,
    targetWeightage = targetWeightage,
    status = status.name,
    challengeId = challengeId,
    createdAt = createdAt
)

fun ChoreCompletionEntity.toDomain() = ChoreCompletion(
    id = id,
    weeklyAssignmentId = weeklyAssignmentId,
    completionPercentage = completionPercentage,
    pointsAwarded = pointsAwarded,
    photoUri = photoUri,
    completedAt = completedAt,
    notes = notes
)

fun ChoreCompletion.toEntity() = ChoreCompletionEntity(
    id = id,
    weeklyAssignmentId = weeklyAssignmentId,
    completionPercentage = completionPercentage,
    pointsAwarded = pointsAwarded,
    photoUri = photoUri,
    completedAt = completedAt,
    notes = notes
)
