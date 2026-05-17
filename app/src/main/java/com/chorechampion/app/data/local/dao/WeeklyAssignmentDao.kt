package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.WeeklyAssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyAssignmentDao {
    
    @Query("SELECT * FROM weekly_assignments WHERE weekStartDate = :weekStart")
    fun getAssignmentsForWeek(weekStart: Long): Flow<List<WeeklyAssignmentEntity>>
    
    @Query("SELECT * FROM weekly_assignments WHERE id = :assignmentId")
    suspend fun getAssignmentById(assignmentId: String): WeeklyAssignmentEntity?
    
    @Query("SELECT * FROM weekly_assignments WHERE weekStartDate = :weekStart AND assignedToUserId = :userId")
    fun getAssignmentsForUserAndWeek(userId: String, weekStart: Long): Flow<List<WeeklyAssignmentEntity>>
    
    @Query("SELECT * FROM weekly_assignments WHERE assignedToUserId = :userId ORDER BY weekStartDate DESC")
    fun getAssignmentsByUser(userId: String): Flow<List<WeeklyAssignmentEntity>>
    
    @Query("SELECT * FROM weekly_assignments WHERE choreId = :choreId")
    fun getAssignmentsByChore(choreId: String): Flow<List<WeeklyAssignmentEntity>>
    
    @Query("SELECT * FROM weekly_assignments WHERE challengeId = :challengeId ORDER BY weekStartDate ASC")
    fun getAssignmentsByChallenge(challengeId: String): Flow<List<WeeklyAssignmentEntity>>
    
    @Query("SELECT * FROM weekly_assignments WHERE weekStartDate = :weekStart AND status = :status")
    fun getAssignmentsByWeekAndStatus(weekStart: Long, status: String): Flow<List<WeeklyAssignmentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: WeeklyAssignmentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<WeeklyAssignmentEntity>)
    
    @Update
    suspend fun updateAssignment(assignment: WeeklyAssignmentEntity)
    
    @Delete
    suspend fun deleteAssignment(assignment: WeeklyAssignmentEntity)
    
    @Query("UPDATE weekly_assignments SET status = :status WHERE id = :assignmentId")
    suspend fun updateAssignmentStatus(assignmentId: String, status: String)
}
