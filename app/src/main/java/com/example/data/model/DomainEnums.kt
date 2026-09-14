package com.example.data.model

enum class UserRole {
    CUSTOMER,
    RESTAURANT,
    BAKERY,
    CAFE,
    SUPERMARKET,
    PICKUP_AGENT,
    NGO,
    ADMIN
}

enum class BusinessType {
    RESTAURANT,
    BAKERY,
    CAFE,
    SUPERMARKET,
    HOTEL
}

enum class PackageCategory {
    BAKERY,
    MEALS,
    GROCERIES,
    PRODUCE,
    VEGAN,
    SWEETS
}

enum class OrderStatus {
    RESERVED,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED
}
