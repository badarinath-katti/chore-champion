package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.ChoreCategoryDao
import com.chorechampion.app.data.local.dao.ChoreDao
import com.chorechampion.app.data.local.entity.ChoreCategoryEntity
import com.chorechampion.app.data.local.entity.ChoreEntity
import com.chorechampion.app.domain.model.Chore
import com.chorechampion.app.domain.model.ChoreCategory
import com.chorechampion.app.domain.repository.ChoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChoreRepositoryImpl @Inject constructor(
    private val choreDao: ChoreDao,
    private val categoryDao: ChoreCategoryDao
) : ChoreRepository {

    override fun getAllChores(): Flow<List<Chore>> {
        return choreDao.getAllChores().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getChoreById(choreId: String): Chore? {
        return choreDao.getChoreById(choreId)?.toDomain()
    }

    override fun getChoreByIdFlow(choreId: String): Flow<Chore?> {
        return choreDao.getChoreByIdFlow(choreId).map { it?.toDomain() }
    }

    override fun getChoresByCategory(categoryId: String): Flow<List<Chore>> {
        return choreDao.getChoresByCategory(categoryId).map { list -> list.map { it.toDomain() } }
    }

    override fun getChoresByUser(userId: String): Flow<List<Chore>> {
        return choreDao.getChoresByUser(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertChore(chore: Chore) {
        choreDao.insertChore(chore.toEntity())
    }

    override suspend fun updateChore(chore: Chore) {
        choreDao.updateChore(chore.toEntity())
    }

    override suspend fun deleteChore(chore: Chore) {
        choreDao.deleteChore(chore.toEntity())
    }

    override fun getAllCategories(): Flow<List<ChoreCategory>> {
        return categoryDao.getAllCategories().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getCategoryById(categoryId: String): ChoreCategory? {
        return categoryDao.getCategoryById(categoryId)?.toDomain()
    }

    override suspend fun insertCategory(category: ChoreCategory) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun initializeDefaultCategories() {
        val existingCategories = categoryDao.getDefaultCategories()
        if (existingCategories.isEmpty()) {
            val defaultCategories = listOf(
                ChoreCategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Cleaning",
                    icon = "🧹",
                    colorHex = "#4CAF50",
                    isDefault = true
                ),
                ChoreCategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Cooking",
                    icon = "🍳",
                    colorHex = "#FF9800",
                    isDefault = true
                ),
                ChoreCategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Shopping",
                    icon = "🛒",
                    colorHex = "#2196F3",
                    isDefault = true
                ),
                ChoreCategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Laundry",
                    icon = "👕",
                    colorHex = "#9C27B0",
                    isDefault = true
                ),
                ChoreCategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Maintenance",
                    icon = "🔧",
                    colorHex = "#F44336",
                    isDefault = true
                ),
                ChoreCategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Other",
                    icon = "📋",
                    colorHex = "#607D8B",
                    isDefault = true
                )
            )
            categoryDao.insertCategories(defaultCategories)
        }
    }
}

// Mappers
fun ChoreEntity.toDomain() = Chore(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    defaultWeightage = defaultWeightage,
    createdBy = createdBy,
    isTemplate = isTemplate,
    createdAt = createdAt
)

fun Chore.toEntity() = ChoreEntity(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    defaultWeightage = defaultWeightage,
    createdBy = createdBy,
    isTemplate = isTemplate,
    createdAt = createdAt
)

fun ChoreCategoryEntity.toDomain() = ChoreCategory(
    id = id,
    name = name,
    icon = icon,
    colorHex = colorHex,
    isDefault = isDefault
)

fun ChoreCategory.toEntity() = ChoreCategoryEntity(
    id = id,
    name = name,
    icon = icon,
    colorHex = colorHex,
    isDefault = isDefault
)
