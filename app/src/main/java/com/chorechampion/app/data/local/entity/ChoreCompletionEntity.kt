package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chore_completions",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyAssignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["weeklyAssignmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("weeklyAssignmentId")]
)
data class ChoreCompletionEntity(
    @PrimaryKey val id: String,
    val weeklyAssignmentId: String,
    val completionPercentage: Int, // 0-100
    val pointsAwarded: Float, // Calculated: base points (1-5) * (weightage / 10)
    val photoUri: String? = null, // Optional photo proof
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)
