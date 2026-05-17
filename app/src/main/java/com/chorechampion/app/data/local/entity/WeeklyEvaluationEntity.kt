package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_evaluations",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user1Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user2Id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user1Id"),
        Index("user2Id"),
        Index("weekStartDate")
    ]
)
data class WeeklyEvaluationEntity(
    @PrimaryKey val id: String,
    val weekStartDate: Long,
    val weekEndDate: Long,
    val user1Id: String,
    val user1Points: Float,
    val user2Id: String,
    val user2Points: Float,
    val winnerId: String?, // User ID or null if tie
    val evaluatedAt: Long = System.currentTimeMillis()
)
