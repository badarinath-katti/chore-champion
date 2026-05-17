package com.chorechampion.app.domain.repository

import com.chorechampion.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserById(userId: String): Flow<User?>
    suspend fun getUserByFirebaseUid(firebaseUid: String): User?
    suspend fun getPartnerByUserId(userId: String): User?
    fun getAllUsers(): Flow<List<User>>
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun linkPartners(user1Id: String, user2Id: String)
    suspend fun updateWeekStartDay(userId: String, day: java.time.DayOfWeek)
}
