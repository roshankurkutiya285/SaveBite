package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PackageCategory

@Entity(tableName = "food_packages")
data class FoodPackageEntity(
    @PrimaryKey val id: String,
    val merchantId: String,
    val title: String,
    val description: String,
    val category: PackageCategory,
    val originalPrice: Double,
    val discountedPrice: Double,
    val quantityAvailable: Int,
    val initialQuantity: Int,
    val pickupWindow: String, // e.g. "Today 8:00 PM - 9:00 PM"
    val dietaryTags: List<String>, // e.g. ["Vegetarian", "Nut-Free"]
    val co2SavedKg: Double, // e.g. 2.5 kg
    val isDonation: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val discountPercent: Int
        get() = if (originalPrice > 0) {
            (((originalPrice - discountedPrice) / originalPrice) * 100).toInt()
        } else 0
}
