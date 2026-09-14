package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.UserRole

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val password: String, // PBKDF2 with HMAC-SHA256 salted hash
    val name: String,
    val phone: String,
    val role: UserRole,
    val avatarUrl: String = "",
    val isEmailVerified: Boolean = false,
    val failedLoginAttempts: Int = 0,
    val lockoutUntilMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

