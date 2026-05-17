package com.chorechampion.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val firebaseUid: String,
    val email: String,
    val name: String,
    val profilePhotoUri: String? = null,
    val weekStartDay: String = DayOfWeek.SUNDAY.name, // Stored as string for Room compatibility
    val partnerId: String? = null, // Link to partner user
    val createdAt: Long = System.currentTimeMillis()
)
