package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    fun getChallengeById(challengeId: String): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE creator_user_id = :userId OR partner_user_id = :userId ORDER BY created_at DESC")
    fun getChallengesByUser(userId: String): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE (creator_user_id = :userId OR partner_user_id = :userId) AND status = :status ORDER BY start_date DESC")
    fun getChallengesByUserAndStatus(userId: String, status: String): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE (creator_user_id = :userId OR partner_user_id = :userId) AND status = 'ACTIVE' AND start_date <= :currentTime AND end_date >= :currentTime ORDER BY end_date ASC")
    fun getActiveChallenges(userId: String, currentTime: Long): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE (creator_user_id = :userId OR partner_user_id = :userId) AND status = 'PENDING' AND start_date > :currentTime ORDER BY start_date ASC")
    fun getUpcomingChallenges(userId: String, currentTime: Long): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE (creator_user_id = :userId OR partner_user_id = :userId) AND (status = 'COMPLETED' OR (status = 'ACTIVE' AND end_date < :currentTime)) ORDER BY end_date DESC")
    fun getCompletedChallenges(userId: String, currentTime: Long): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challenge: ChallengeEntity)

    @Query("UPDATE challenges SET status = :status WHERE id = :challengeId")
    suspend fun updateChallengeStatus(challengeId: String, status: String)

    @Query("DELETE FROM challenges WHERE id = :challengeId")
    suspend fun deleteChallengeById(challengeId: String)
}
