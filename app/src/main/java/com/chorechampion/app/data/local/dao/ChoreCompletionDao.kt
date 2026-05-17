package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.ChoreCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreCompletionDao {
    
    @Query("SELECT * FROM chore_completions WHERE weeklyAssignmentId = :assignmentId")
    suspend fun getCompletionByAssignment(assignmentId: String): ChoreCompletionEntity?
    
    @Query("SELECT * FROM chore_completions WHERE weeklyAssignmentId = :assignmentId")
    fun getCompletionByAssignmentFlow(assignmentId: String): Flow<ChoreCompletionEntity?>
    
    @Query("SELECT * FROM chore_completions WHERE id = :completionId")
    suspend fun getCompletionById(completionId: String): ChoreCompletionEntity?
    
    @Query("""
        SELECT cc.* FROM chore_completions cc
        INNER JOIN weekly_assignments wa ON cc.weeklyAssignmentId = wa.id
        WHERE wa.weekStartDate = :weekStart
    """)
    fun getCompletionsForWeek(weekStart: Long): Flow<List<ChoreCompletionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: ChoreCompletionEntity)
    
    @Update
    suspend fun updateCompletion(completion: ChoreCompletionEntity)
    
    @Delete
    suspend fun deleteCompletion(completion: ChoreCompletionEntity)
}
