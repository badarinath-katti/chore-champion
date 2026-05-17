package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chores",
    foreignKeys = [
        ForeignKey(
            entity = ChoreCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class ChoreEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val categoryId: String,
    val defaultWeightage: Int = 10, // 1-100 scale, default 10
    val createdBy: String, // User ID
    val isTemplate: Boolean = false, // True for default chore templates
    val createdAt: Long = System.currentTimeMillis()
)
