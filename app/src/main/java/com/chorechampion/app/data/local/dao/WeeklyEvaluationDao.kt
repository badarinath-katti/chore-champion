package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.WeeklyEvaluationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyEvaluationDao {
    
    @Query("SELECT * FROM weekly_evaluations WHERE weekStartDate = :weekStart")
    suspend fun getEvaluationByWeek(weekStart: Long): WeeklyEvaluationEntity?
    
    @Query("SELECT * FROM weekly_evaluations WHERE weekStartDate = :weekStart")
    fun getEvaluationByWeekFlow(weekStart: Long): Flow<WeeklyEvaluationEntity?>
    
    @Query("SELECT * FROM weekly_evaluations ORDER BY weekStartDate DESC")
    fun getAllEvaluations(): Flow<List<WeeklyEvaluationEntity>>
    
    @Query("SELECT * FROM weekly_evaluations ORDER BY weekStartDate DESC LIMIT :limit")
    fun getRecentEvaluations(limit: Int = 10): Flow<List<WeeklyEvaluationEntity>>
    
    @Query("""
        SELECT * FROM weekly_evaluations 
        WHERE user1Id = :userId OR user2Id = :userId 
        ORDER BY weekStartDate DESC
    """)
    fun getEvaluationsForUser(userId: String): Flow<List<WeeklyEvaluationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: WeeklyEvaluationEntity)
    
    @Update
    suspend fun updateEvaluation(evaluation: WeeklyEvaluationEntity)
    
    @Delete
    suspend fun deleteEvaluation(evaluation: WeeklyEvaluationEntity)
}
