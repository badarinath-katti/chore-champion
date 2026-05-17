package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_assignments",
    foreignKeys = [
        ForeignKey(
            entity = ChoreEntity::class,
            parentColumns = ["id"],
            childColumns = ["choreId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignedToUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("choreId"),
        Index("assignedToUserId"),
        Index("weekStartDate"),
        Index("challengeId")
    ]
)
data class WeeklyAssignmentEntity(
    @PrimaryKey val id: String,
    val choreId: String,
    val assignedToUserId: String,
    val weekStartDate: Long,
    val weekEndDate: Long,
    val targetWeightage: Int, // Weightage for this specific week
    val status: String = AssignmentStatus.PENDING.name, // PENDING, IN_PROGRESS, COMPLETED, SKIPPED
    val challengeId: String? = null, // Optional link to a challenge
    val createdAt: Long = System.currentTimeMillis()
)

enum class AssignmentStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED
}
