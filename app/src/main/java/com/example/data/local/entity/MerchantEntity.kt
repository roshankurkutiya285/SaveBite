package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BusinessType

@Entity(tableName = "merchants")
data class MerchantEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val businessName: String,
    val businessType: BusinessType,
    val description: String,
    val address: String,
    val distanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val reviewCount: Int,
    val pickupStartTime: String, // e.g. "19:30"
    val pickupEndTime: String,   // e.g. "20:45"
    val pickupInstructions: String,
    val coverEmoji: String,      // Visual branding fallback (e.g. 🥐, 🥗, ☕)
    val verified: Boolean = true,
    val isPartnerNGO: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
