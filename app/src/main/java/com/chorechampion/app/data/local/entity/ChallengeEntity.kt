package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "challenges",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["creator_user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["partner_user_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["creator_user_id"]),
        Index(value = ["partner_user_id"]),
        Index(value = ["invite_code"], unique = true),
        Index(value = ["status"]),
        Index(value = ["start_date", "end_date"])
    ]
)
data class ChallengeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val creator_user_id: String,
    val partner_user_id: String?,
    val invite_code: String,
    val start_date: Long,
    val end_date: Long,
    val status: String, // PENDING, ACTIVE, COMPLETED, CANCELLED
    val created_at: Long
)
