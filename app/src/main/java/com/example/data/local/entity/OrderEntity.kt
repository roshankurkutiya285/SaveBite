package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.OrderStatus

@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["merchantId"]),
        Index(value = ["pickupPin"]),
        Index(value = ["status"])
    ]
)
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
    val completedAt: Long? = null,
    val paymentMethod: String = "RAZORPAY_UPI",
    val razorpayPaymentId: String? = null
)
