package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.RewardDao
import com.chorechampion.app.data.local.entity.RewardEntity
import com.chorechampion.app.domain.model.Reward
import com.chorechampion.app.domain.repository.RewardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RewardRepositoryImpl @Inject constructor(
    private val rewardDao: RewardDao
) : RewardRepository {

    override fun getAllRewards(): Flow<List<Reward>> {
        return rewardDao.getAllRewards().map { list -> list.map { it.toDomain() } }
    }

    override fun getUnclaimedRewards(): Flow<List<Reward>> {
        return rewardDao.getUnclaimedRewards().map { list -> list.map { it.toDomain() } }
    }

    override fun getRewardsByUser(userId: String): Flow<List<Reward>> {
        return rewardDao.getRewardsByUser(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRewardById(rewardId: String): Reward? {
        return rewardDao.getRewardById(rewardId)?.toDomain()
    }

    override suspend fun insertReward(reward: Reward) {
        rewardDao.insertReward(reward.toEntity())
    }

    override suspend fun claimReward(rewardId: String, userId: String) {
        rewardDao.claimReward(rewardId, userId, System.currentTimeMillis())
    }
}

// Mappers
fun RewardEntity.toDomain() = Reward(
    id = id,
    name = name,
    description = description,
    pointsThreshold = pointsThreshold,
    imageUri = imageUri,
    claimedByUserId = claimedByUserId,
    claimedAt = claimedAt,
    createdAt = createdAt
)

fun Reward.toEntity() = RewardEntity(
    id = id,
    name = name,
    description = description,
    pointsThreshold = pointsThreshold,
    imageUri = imageUri,
    claimedByUserId = claimedByUserId,
    claimedAt = claimedAt,
    createdAt = createdAt
)
