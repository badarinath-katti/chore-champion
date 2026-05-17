package com.chorechampion.app.domain.model

data class Reward(
    val id: String,
    val name: String,
    val description: String? = null,
    val pointsThreshold: Int,
    val imageUri: String? = null,
    val claimedByUserId: String? = null,
    val claimedAt: Long? = null,
    val createdAt: Long
) {
    fun isClaimed(): Boolean = claimedByUserId != null
    
    fun isClaimedBy(userId: String): Boolean = claimedByUserId == userId
}
