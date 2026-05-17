package com.chorechampion.app.domain.model

import java.time.LocalDateTime

data class Challenge(
    val id: String,
    val name: String,
    val description: String?,
    val creatorUserId: String,
    val partnerUserId: String?, // Optional partner
    val startDate: Long, // Unix timestamp
    val endDate: Long, // Unix timestamp
    val status: ChallengeStatus,
    val createdAt: Long
) {
    fun isActive(): Boolean {
        val now = System.currentTimeMillis()
        return status == ChallengeStatus.ACTIVE && now in startDate..endDate
    }

    fun hasEnded(): Boolean {
        return System.currentTimeMillis() > endDate || status == ChallengeStatus.COMPLETED
    }

    fun hasStarted(): Boolean {
        return System.currentTimeMillis() >= startDate
    }

    fun daysRemaining(): Int {
        val now = System.currentTimeMillis()
        if (now >= endDate) return 0
        return ((endDate - now) / (24 * 60 * 60 * 1000)).toInt()
    }

    fun isSolo(): Boolean = partnerUserId == null

    fun duration(): Int {
        return ((endDate - startDate) / (24 * 60 * 60 * 1000)).toInt()
    }
}

enum class ChallengeStatus {
    PENDING,    // Created but not started yet
    ACTIVE,     // Currently running
    COMPLETED,  // Ended
    CANCELLED   // Cancelled by user
}
