package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.data.model.BusinessType
import com.example.data.model.OrderStatus
import com.example.data.model.PackageCategory
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(";;") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(";;").map { it.trim() }.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromUserRole(role: UserRole?): String = role?.name ?: UserRole.CUSTOMER.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole =
        value?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: UserRole.CUSTOMER

    @TypeConverter
    fun fromBusinessType(type: BusinessType?): String = type?.name ?: BusinessType.RESTAURANT.name

    @TypeConverter
    fun toBusinessType(value: String?): BusinessType =
        value?.let { runCatching { BusinessType.valueOf(it) }.getOrNull() } ?: BusinessType.RESTAURANT

    @TypeConverter
    fun fromPackageCategory(category: PackageCategory?): String = category?.name ?: PackageCategory.MEALS.name

    @TypeConverter
    fun toPackageCategory(value: String?): PackageCategory =
        value?.let { runCatching { PackageCategory.valueOf(it) }.getOrNull() } ?: PackageCategory.MEALS

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus?): String = status?.name ?: OrderStatus.RESERVED.name

    @TypeConverter
    fun toOrderStatus(value: String?): OrderStatus =
        value?.let { runCatching { OrderStatus.valueOf(it) }.getOrNull() } ?: OrderStatus.RESERVED
}
