package com.chorechampion.app.domain.repository

import com.chorechampion.app.domain.model.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isAuthenticated: Boolean
    
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUserId(): String?
}
