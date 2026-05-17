package com.chorechampion.app.domain.model

data class WeeklyEvaluation(
    val id: String,
    val weekStartDate: Long,
    val weekEndDate: Long,
    val user1Id: String,
    val user1Points: Float,
    val user2Id: String,
    val user2Points: Float,
    val winnerId: String?,
    val evaluatedAt: Long
) {
    fun getPointsFor(userId: String): Float {
        return when (userId) {
            user1Id -> user1Points
            user2Id -> user2Points
            else -> 0f
        }
    }
    
    fun isWinner(userId: String): Boolean {
        return winnerId == userId
    }
    
    fun isTie(): Boolean {
        return winnerId == null
    }
}
