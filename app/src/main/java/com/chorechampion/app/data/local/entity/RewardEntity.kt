package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rewards",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["claimedByUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("claimedByUserId")]
)
data class RewardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val pointsThreshold: Int, // Points needed to unlock
    val imageUri: String? = null,
    val claimedByUserId: String? = null,
    val claimedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
