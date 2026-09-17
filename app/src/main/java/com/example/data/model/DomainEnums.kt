package com.example.data.model

enum class UserRole {
    CUSTOMER,
    RESTAURANT,
    BAKERY,
    CAFE,
    SUPERMARKET,
    PICKUP_AGENT,
    NGO,
    ADMIN;

    val isMerchant: Boolean
        get() = this == RESTAURANT || this == BAKERY || this == CAFE || this == SUPERMARKET
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
