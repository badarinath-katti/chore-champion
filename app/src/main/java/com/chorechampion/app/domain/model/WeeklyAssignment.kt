package com.chorechampion.app.domain.model

import com.chorechampion.app.data.local.entity.AssignmentStatus

data class WeeklyAssignment(
    val id: String,
    val choreId: String,
    val assignedToUserId: String,
    val weekStartDate: Long,
    val weekEndDate: Long,
    val targetWeightage: Int,
    val status: AssignmentStatus,
    val challengeId: String? = null,
    val createdAt: Long
)
