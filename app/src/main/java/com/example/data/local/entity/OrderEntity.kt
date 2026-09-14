package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.OrderStatus

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val merchantId: String,
    val merchantName: String,
    val packageId: String,
    val packageTitle: String,
    val quantity: Int,
    val totalPrice: Double,
    val totalSavings: Double,
    val pickupPin: String,
    val qrPayload: String,
    val status: OrderStatus,
    val pickupWindow: String,
    val co2SavedKg: Double,
    val reservedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
