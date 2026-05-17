package com.chorechampion.app.domain.model

import java.time.DayOfWeek

data class User(
    val id: String,
    val firebaseUid: String,
    val email: String,
    val name: String,
    val profilePhotoUri: String? = null,
    val weekStartDay: DayOfWeek = DayOfWeek.SUNDAY,
    val partnerId: String? = null,
    val createdAt: Long
)
