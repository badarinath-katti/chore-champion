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
            // Validate inputs
            if (email.isBlank()) {
                return Result.failure(Exception("Email cannot be empty"))
            }
            if (password.isBlank()) {
                return Result.failure(Exception("Password cannot be empty"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }
            if (name.isBlank()) {
                return Result.failure(Exception("Name cannot be empty"))
            }

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User creation failed - no user returned")

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
        } catch (e: com.google.firebase.FirebaseNetworkException) {
            Result.failure(Exception("Network error: Please check your internet connection"))
        } catch (e: com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("Password is too weak. Use at least 6 characters"))
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Invalid email format"))
        } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            Result.failure(Exception("An account with this email already exists"))
        } catch (e: Exception) {
            Result.failure(Exception("Sign up failed: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // Check if email is valid
            if (email.isBlank()) {
                return Result.failure(Exception("Email cannot be empty"))
            }
            if (password.isBlank()) {
                return Result.failure(Exception("Password cannot be empty"))
            }

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Sign in failed - no user returned")

            // Get user from local database, or create if not exists
            var user = userDao.getUserByFirebaseUid(firebaseUser.uid)?.toDomain()
            
            if (user == null) {
                // User authenticated with Firebase but not in local DB - create entry
                user = User(
                    id = UUID.randomUUID().toString(),
                    firebaseUid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    name = firebaseUser.displayName ?: "User",
                    createdAt = System.currentTimeMillis()
                )
                userDao.insertUser(user.toEntity())
            }

            Result.success(user)
        } catch (e: com.google.firebase.FirebaseNetworkException) {
            Result.failure(Exception("Network error: Please check your internet connection"))
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Invalid email or password"))
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
            Result.failure(Exception("No account found with this email"))
        } catch (e: Exception) {
            Result.failure(Exception("Sign in failed: ${e.message ?: "Unknown error"}"))
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
