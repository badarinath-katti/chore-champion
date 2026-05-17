package com.chorechampion.app.domain.repository

import com.chorechampion.app.domain.model.Reward
import kotlinx.coroutines.flow.Flow

interface RewardRepository {
    fun getAllRewards(): Flow<List<Reward>>
    fun getUnclaimedRewards(): Flow<List<Reward>>
    fun getRewardsByUser(userId: String): Flow<List<Reward>>
    suspend fun getRewardById(rewardId: String): Reward?
    suspend fun insertReward(reward: Reward)
    suspend fun claimReward(rewardId: String, userId: String)
}
