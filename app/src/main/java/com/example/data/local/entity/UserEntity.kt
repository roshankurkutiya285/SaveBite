package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val role: UserRole,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
