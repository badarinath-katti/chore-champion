package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.UserDao
import com.chorechampion.app.data.local.entity.UserEntity
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUserById(userId: String): Flow<User?> {
        return userDao.getUserById(userId).map { it?.toDomain() }
    }

    override suspend fun getUserByFirebaseUid(firebaseUid: String): User? {
        return userDao.getUserByFirebaseUid(firebaseUid)?.toDomain()
    }

    override suspend fun getPartnerByUserId(userId: String): User? {
        return userDao.getPartnerByUserId(userId)?.toDomain()
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    override suspend fun linkPartners(user1Id: String, user2Id: String) {
        userDao.updatePartner(user1Id, user2Id)
        userDao.updatePartner(user2Id, user1Id)
    }

    override suspend fun updateWeekStartDay(userId: String, day: DayOfWeek) {
        userDao.updateWeekStartDay(userId, day.name)
    }
}

// Extension functions for mapping
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        firebaseUid = firebaseUid,
        email = email,
        name = name,
        profilePhotoUri = profilePhotoUri,
        weekStartDay = DayOfWeek.valueOf(weekStartDay),
        partnerId = partnerId,
        createdAt = createdAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        firebaseUid = firebaseUid,
        email = email,
        name = name,
        profilePhotoUri = profilePhotoUri,
        weekStartDay = weekStartDay.name,
        partnerId = partnerId,
        createdAt = createdAt
    )
}
