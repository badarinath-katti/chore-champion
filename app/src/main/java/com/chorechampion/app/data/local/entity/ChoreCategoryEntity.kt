package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chore_categories")
data class ChoreCategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String, // Material icon name or emoji
    val colorHex: String, // Color for UI display
    val isDefault: Boolean = false // System default categories
)
