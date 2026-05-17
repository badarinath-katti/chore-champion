package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.ChallengeDao
import com.chorechampion.app.data.local.entity.ChallengeEntity
import com.chorechampion.app.domain.model.Challenge
import com.chorechampion.app.domain.model.ChallengeStatus
import com.chorechampion.app.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao
) : ChallengeRepository {

    override fun getChallengeById(challengeId: String): Flow<Challenge?> {
        return challengeDao.getChallengeById(challengeId).map { it?.toDomain() }
    }

    override fun getChallengesByUser(userId: String): Flow<List<Challenge>> {
        return challengeDao.getChallengesByUser(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getChallengesByUserAndStatus(userId: String, status: ChallengeStatus): Flow<List<Challenge>> {
        return challengeDao.getChallengesByUserAndStatus(userId, status.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getActiveChallenges(userId: String): Flow<List<Challenge>> {
        return challengeDao.getActiveChallenges(userId, System.currentTimeMillis()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getUpcomingChallenges(userId: String): Flow<List<Challenge>> {
        return challengeDao.getUpcomingChallenges(userId, System.currentTimeMillis()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCompletedChallenges(userId: String): Flow<List<Challenge>> {
        return challengeDao.getCompletedChallenges(userId, System.currentTimeMillis()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createChallenge(challenge: Challenge) {
        challengeDao.insertChallenge(challenge.toEntity())
    }

    override suspend fun updateChallenge(challenge: Challenge) {
        challengeDao.updateChallenge(challenge.toEntity())
    }

    override suspend fun deleteChallenge(challengeId: String) {
        challengeDao.deleteChallengeById(challengeId)
    }

    override suspend fun updateChallengeStatus(challengeId: String, status: ChallengeStatus) {
        challengeDao.updateChallengeStatus(challengeId, status.name)
    }

    private fun ChallengeEntity.toDomain(): Challenge {
        return Challenge(
            id = id,
            name = name,
            description = description,
            creatorUserId = creator_user_id,
            partnerUserId = partner_user_id,
            startDate = start_date,
            endDate = end_date,
            status = ChallengeStatus.valueOf(status),
            createdAt = created_at
        )
    }

    private fun Challenge.toEntity(): ChallengeEntity {
        return ChallengeEntity(
            id = id,
            name = name,
            description = description,
            creator_user_id = creatorUserId,
            partner_user_id = partnerUserId,
            start_date = startDate,
            end_date = endDate,
            status = status.name,
            created_at = createdAt
        )
    }
}
