package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.ChoreCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreCategoryDao {
    
    @Query("SELECT * FROM chore_categories ORDER BY isDefault DESC, name ASC")
    fun getAllCategories(): Flow<List<ChoreCategoryEntity>>
    
    @Query("SELECT * FROM chore_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): ChoreCategoryEntity?
    
    @Query("SELECT * FROM chore_categories WHERE isDefault = 1")
    suspend fun getDefaultCategories(): List<ChoreCategoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ChoreCategoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ChoreCategoryEntity>)
    
    @Update
    suspend fun updateCategory(category: ChoreCategoryEntity)
    
    @Delete
    suspend fun deleteCategory(category: ChoreCategoryEntity)
}
