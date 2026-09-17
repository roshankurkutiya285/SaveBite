package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserByIdDirect(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:identifier) OR phone = :identifier LIMIT 1")
    suspend fun getUserByEmailOrPhone(identifier: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("UPDATE users SET password = :newPasswordHash WHERE id = :userId")
    suspend fun updatePassword(userId: String, newPasswordHash: String)

    @Query("UPDATE users SET name = :name, phone = :phone, avatarUrl = :avatarUrl WHERE id = :userId")
    suspend fun updateProfile(userId: String, name: String, phone: String, avatarUrl: String)

    @Query("UPDATE users SET failedLoginAttempts = :attempts, lockoutUntilMs = :lockoutUntilMs WHERE id = :userId")
    suspend fun updateLockout(userId: String, attempts: Int, lockoutUntilMs: Long)

    @Query("UPDATE users SET isEmailVerified = 1 WHERE id = :userId")
    suspend fun markEmailVerified(userId: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}
