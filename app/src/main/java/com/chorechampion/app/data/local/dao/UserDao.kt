package com.chorechampion.app.data.local.dao

import androidx.room.*
import com.chorechampion.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid")
    suspend fun getUserByFirebaseUid(firebaseUid: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE partnerId = :userId")
    suspend fun getPartnerByUserId(userId: String): UserEntity?
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("UPDATE users SET partnerId = :partnerId WHERE id = :userId")
    suspend fun updatePartner(userId: String, partnerId: String)
    
    @Query("UPDATE users SET weekStartDay = :dayName WHERE id = :userId")
    suspend fun updateWeekStartDay(userId: String, dayName: String)
}
