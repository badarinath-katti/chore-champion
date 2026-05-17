package com.chorechampion.app.domain.repository

import com.chorechampion.app.domain.model.Chore
import com.chorechampion.app.domain.model.ChoreCategory
import kotlinx.coroutines.flow.Flow

interface ChoreRepository {
    // Chores
    fun getAllChores(): Flow<List<Chore>>
    suspend fun getChoreById(choreId: String): Chore?
    fun getChoreByIdFlow(choreId: String): Flow<Chore?>
    fun getChoresByCategory(categoryId: String): Flow<List<Chore>>
    fun getChoresByUser(userId: String): Flow<List<Chore>>
    suspend fun insertChore(chore: Chore)
    suspend fun updateChore(chore: Chore)
    suspend fun deleteChore(chore: Chore)
    
    // Categories
    fun getAllCategories(): Flow<List<ChoreCategory>>
    suspend fun getCategoryById(categoryId: String): ChoreCategory?
    suspend fun insertCategory(category: ChoreCategory)
    suspend fun initializeDefaultCategories()
}
