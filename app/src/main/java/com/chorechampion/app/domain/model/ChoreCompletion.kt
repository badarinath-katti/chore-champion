package com.chorechampion.app.domain.model

data class ChoreCompletion(
    val id: String,
    val weeklyAssignmentId: String,
    val completionPercentage: Int,
    val pointsAwarded: Float,
    val photoUri: String? = null,
    val completedAt: Long,
    val notes: String? = null
)
