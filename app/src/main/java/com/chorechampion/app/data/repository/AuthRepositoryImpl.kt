package com.chorechampion.app.data.repository

import com.chorechampion.app.data.local.dao.UserDao
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override val isAuthenticated: Boolean
        get() = currentUser != null

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User creation failed")

            // Update Firebase profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Create user in local database
            val user = User(
                id = UUID.randomUUID().toString(),
                firebaseUid = firebaseUser.uid,
                email = email,
                name = name,
                createdAt = System.currentTimeMillis()
            )
            userDao.insertUser(user.toEntity())

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Sign in failed")

            // Get user from local database
            val user = userDao.getUserByFirebaseUid(firebaseUser.uid)?.toDomain()
                ?: throw Exception("User not found in database")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? {
        return currentUser?.uid
    }
}
