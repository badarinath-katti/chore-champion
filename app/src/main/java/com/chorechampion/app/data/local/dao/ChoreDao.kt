package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.ChoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreDao {
    
    @Query("SELECT * FROM chores WHERE isTemplate = 0 ORDER BY createdAt DESC")
    fun getAllChores(): Flow<List<ChoreEntity>>
    
    @Query("SELECT * FROM chores WHERE id = :choreId")
    suspend fun getChoreById(choreId: String): ChoreEntity?
    
    @Query("SELECT * FROM chores WHERE id = :choreId")
    fun getChoreByIdFlow(choreId: String): Flow<ChoreEntity?>
    
    @Query("SELECT * FROM chores WHERE categoryId = :categoryId AND isTemplate = 0")
    fun getChoresByCategory(categoryId: String): Flow<List<ChoreEntity>>
    
    @Query("SELECT * FROM chores WHERE createdBy = :userId AND isTemplate = 0")
    fun getChoresByUser(userId: String): Flow<List<ChoreEntity>>
    
    @Query("SELECT * FROM chores WHERE isTemplate = 1")
    suspend fun getTemplateChores(): List<ChoreEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChore(chore: ChoreEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChores(chores: List<ChoreEntity>)
    
    @Update
    suspend fun updateChore(chore: ChoreEntity)
    
    @Delete
    suspend fun deleteChore(chore: ChoreEntity)
    
    @Query("DELETE FROM chores WHERE id = :choreId")
    suspend fun deleteChoreById(choreId: String)
}
