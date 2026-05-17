package com.chorechampion.app.domain.repository

import com.chorechampion.app.domain.model.Challenge
import com.chorechampion.app.domain.model.ChallengeStatus
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun getChallengeById(challengeId: String): Flow<Challenge?>
    fun getChallengesByUser(userId: String): Flow<List<Challenge>>
    fun getChallengesByUserAndStatus(userId: String, status: ChallengeStatus): Flow<List<Challenge>>
    fun getActiveChallenges(userId: String): Flow<List<Challenge>>
    fun getUpcomingChallenges(userId: String): Flow<List<Challenge>>
    fun getCompletedChallenges(userId: String): Flow<List<Challenge>>
    suspend fun createChallenge(challenge: Challenge)
    suspend fun updateChallenge(challenge: Challenge)
    suspend fun deleteChallenge(challengeId: String)
    suspend fun updateChallengeStatus(challengeId: String, status: ChallengeStatus)
}
