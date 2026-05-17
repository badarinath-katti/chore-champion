package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    
    @Query("SELECT * FROM rewards ORDER BY pointsThreshold ASC")
    fun getAllRewards(): Flow<List<RewardEntity>>
    
    @Query("SELECT * FROM rewards WHERE id = :rewardId")
    suspend fun getRewardById(rewardId: String): RewardEntity?
    
    @Query("SELECT * FROM rewards WHERE claimedByUserId IS NULL ORDER BY pointsThreshold ASC")
    fun getUnclaimedRewards(): Flow<List<RewardEntity>>
    
    @Query("SELECT * FROM rewards WHERE claimedByUserId = :userId ORDER BY claimedAt DESC")
    fun getRewardsByUser(userId: String): Flow<List<RewardEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<RewardEntity>)
    
    @Update
    suspend fun updateReward(reward: RewardEntity)
    
    @Delete
    suspend fun deleteReward(reward: RewardEntity)
    
    @Query("UPDATE rewards SET claimedByUserId = :userId, claimedAt = :claimedAt WHERE id = :rewardId")
    suspend fun claimReward(rewardId: String, userId: String, claimedAt: Long)
}
