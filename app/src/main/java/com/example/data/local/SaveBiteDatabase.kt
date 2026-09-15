package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.converter.Converters
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.FoodPackageDao
import com.example.data.local.dao.MerchantDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.BusinessType
import com.example.data.model.OrderStatus
import com.example.data.model.PackageCategory
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        MerchantEntity::class,
        FoodPackageEntity::class,
        OrderEntity::class,
        FavoriteEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SaveBiteDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun merchantDao(): MerchantDao
    abstract fun foodPackageDao(): FoodPackageDao
    abstract fun orderDao(): OrderDao
    abstract fun favoriteDao(): FavoriteDao

    suspend fun seedIfEmpty() {
        if (userDao().getUserCount() == 0) {
            seedProductionData(this)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SaveBiteDatabase? = null

        fun getDatabase(context: Context): SaveBiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaveBiteDatabase::class.java,
                    "savebite_enterprise_db_v8"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedProductionData(database)
                    }
                }
            }
        }

        private suspend fun seedProductionData(db: SaveBiteDatabase) {
            val defaultPasswordHash = com.example.util.PasswordHasher.hashPassword("password123")
            // Seed Indian Users & Rescuers
            val users = listOf(
                UserEntity(
                    id = "user_customer_elena",
                    email = "aarav.sharma@savebite.in",
                    password = defaultPasswordHash,
                    name = "Aarav Sharma",
                    phone = "+91 98450 23145",
                    role = UserRole.CUSTOMER,
                    isEmailVerified = true
                ),
                UserEntity(
                    id = "user_merchant_artisan",
                    email = "vikramaditya@bikanersweets.in",
                    password = defaultPasswordHash,
                    name = "Chef Vikramaditya Singh",
                    phone = "+91 98110 54321",
                    role = UserRole.BAKERY,
                    isEmailVerified = true
                ),
                UserEntity(
                    id = "user_merchant_bistro",
                    email = "tanvi.reddy@saravanabhavan.in",
                    password = defaultPasswordHash,
                    name = "Chef Tanvi Reddy",
                    phone = "+91 97410 88901",
                    role = UserRole.RESTAURANT,
                    isEmailVerified = true
                ),
                UserEntity(
                    id = "user_pickup_rajat",
                    email = "rajat.courier@savebite.in",
                    password = defaultPasswordHash,
                    name = "Rajat Verma (Pickup Partner)",
                    phone = "+91 98765 43210",
                    role = UserRole.PICKUP_AGENT,
                    isEmailVerified = true
                ),
                UserEntity(
                    id = "user_ngo_rescue",
                    email = "ananya@robinhoodarmy.org",
                    password = defaultPasswordHash,
                    name = "Ananya Mukherjee",
                    phone = "+91 99301 77654",
                    role = UserRole.NGO,
                    isEmailVerified = true
                ),
                UserEntity(
                    id = "user_admin_rajesh",
                    email = "admin@savebite.in",
                    password = defaultPasswordHash,
                    name = "Rajesh Verma (Platform Admin)",
                    phone = "+91 98001 11222",
                    role = UserRole.ADMIN,
                    isEmailVerified = true
                )
            )
            db.userDao().insertUsers(users)

            // Seed Indian Food Merchants (Sweets, South Tiffins, North Dhabas, Biryani, Chai, Mandi, NGO)
            val merchants = listOf(
                MerchantEntity(
                    id = "merchant_artisan_bakery",
                    userId = "user_merchant_artisan",
                    businessName = "Bikaner Sweets & Namkeen Hub",
                    businessType = BusinessType.BAKERY,
                    description = "Freshly prepared Motichoor Laddoos, Kaju Katli, Bengali Rasgullas, Dhokla, and crispy Khasta Kachoris.",
                    address = "Connaught Place, Central Delhi",
                    distanceKm = 0.8,
                    latitude = 28.6315,
                    longitude = 77.2167,
                    rating = 4.9,
                    reviewCount = 524,
                    pickupStartTime = "19:00",
                    pickupEndTime = "20:30",
                    pickupInstructions = "Enter via front entrance. Present your SaveBite QR code at the special Mithai parcel counter.",
                    coverEmoji = "🪔"
                ),
                MerchantEntity(
                    id = "merchant_green_bistro",
                    userId = "user_merchant_bistro",
                    businessName = "Saravana Bhavan South Tiffins",
                    businessType = BusinessType.RESTAURANT,
                    description = "Steaming hot Ghee Podi Idlis, Medu Vadas, Mysore Masala Dosas with authentic coconut chutney and filter coffee.",
                    address = "100 Feet Road, Indiranagar, Bengaluru",
                    distanceKm = 1.2,
                    latitude = 12.9784,
                    longitude = 77.6408,
                    rating = 4.8,
                    reviewCount = 412,
                    pickupStartTime = "18:00",
                    pickupEndTime = "19:30",
                    pickupInstructions = "Head to the dedicated tiffin pickup counter. Bags are pre-packed hot with chutneys in secure containers.",
                    coverEmoji = "☕"
                ),
                MerchantEntity(
                    id = "merchant_tokyo_sushi",
                    userId = "user_merchant_punjab",
                    businessName = "Punjab Grill & Dhaba Rasoi",
                    businessType = BusinessType.RESTAURANT,
                    description = "12-hour slow simmered Dal Makhani, fragrant Paneer Butter Masala, garlic butter naans, and tandoori feast combos.",
                    address = "Cyber Hub, DLF Phase 2, Gurugram",
                    distanceKm = 2.1,
                    latitude = 28.4986,
                    longitude = 77.0878,
                    rating = 4.9,
                    reviewCount = 680,
                    pickupStartTime = "21:00",
                    pickupEndTime = "22:15",
                    pickupInstructions = "Present your SaveBite verification PIN at the takeaway desk near the kitchen pass.",
                    coverEmoji = "🍛"
                ),
                MerchantEntity(
                    id = "merchant_paradise_biryani",
                    userId = "user_merchant_biryani",
                    businessName = "Paradise Shahi Dum Biryani",
                    businessType = BusinessType.RESTAURANT,
                    description = "Authentic Hyderabadi Dum Biryani cooked in traditional sealed handi with rich aromatic spices, Mirchi Ka Salan, and Raita.",
                    address = "Road No. 1, Banjara Hills, Hyderabad",
                    distanceKm = 2.5,
                    latitude = 17.4156,
                    longitude = 78.4350,
                    rating = 4.8,
                    reviewCount = 745,
                    pickupStartTime = "21:30",
                    pickupEndTime = "22:45",
                    pickupInstructions = "Show your pickup pass at the host desk. Biryani pots are sealed hot for collection.",
                    coverEmoji = "🥘"
                ),
                MerchantEntity(
                    id = "merchant_nordic_cafe",
                    userId = "user_merchant_cafe",
                    businessName = "Irani Chai & Karachi Bakery Cafe",
                    businessType = BusinessType.CAFE,
                    description = "Crispy Bun Maska, famous Osmania fruit biscuits, freshly baked vegetable puffs, and decadent mawa cakes.",
                    address = "Colaba Causeway, South Mumbai",
                    distanceKm = 1.5,
                    latitude = 18.9220,
                    longitude = 72.8347,
                    rating = 4.7,
                    reviewCount = 310,
                    pickupStartTime = "17:30",
                    pickupEndTime = "18:45",
                    pickupInstructions = "Direct counter pickup. Reusable tote bags are highly encouraged!",
                    coverEmoji = "🥐"
                ),
                MerchantEntity(
                    id = "merchant_earth_market",
                    userId = "user_merchant_market",
                    businessName = "Apni Sabzi Mandi & Kisan Mart",
                    businessType = BusinessType.SUPERMARKET,
                    description = "Direct-from-farm organic surplus vegetables, fresh Shimla capsicum, farm tomatoes, coriander, and seasonal fruits.",
                    address = "80ft Road, Koramangala 4th Block, Bengaluru",
                    distanceKm = 2.9,
                    latitude = 12.9352,
                    longitude = 77.6245,
                    rating = 4.6,
                    reviewCount = 215,
                    pickupStartTime = "18:00",
                    pickupEndTime = "19:30",
                    pickupInstructions = "Customer service desk at store entrance. 5kg produce crates are pre-sorted and weighed.",
                    coverEmoji = "🥑"
                ),
                MerchantEntity(
                    id = "merchant_robin_hood_ngo",
                    userId = "user_ngo_rescue",
                    businessName = "Robin Hood Army & Feeding India Hub",
                    businessType = BusinessType.RESTAURANT,
                    description = "Community surplus food recovery drive. Wholesome dal khichdi and fresh chapati packages distributed to underprivileged shelters.",
                    address = "Community Center, Lajpat Nagar, New Delhi",
                    distanceKm = 3.2,
                    latitude = 28.5677,
                    longitude = 77.2433,
                    rating = 5.0,
                    reviewCount = 890,
                    pickupStartTime = "20:00",
                    pickupEndTime = "21:30",
                    pickupInstructions = "Volunteer coordination desk. All meal packages are 100% free community donations.",
                    coverEmoji = "🍲"
                )
            )
            db.merchantDao().insertMerchants(merchants)

            // Seed Indian Food Packages (Rupee Pricing & High-Res Food Images)
            val packages = listOf(
                FoodPackageEntity(
                    id = "pkg_artisan_sourdough_box",
                    merchantId = "merchant_artisan_bakery",
                    title = "Royal Mithai & Namkeen Magic Box",
                    description = "Festive surprise assortment of 500g fresh sweets (Motichoor Laddoos, Kaju Katli, Cham Cham) plus spicy Khasta Kachoris and samosas.",
                    category = PackageCategory.SWEETS,
                    originalPrice = 450.0,
                    discountedPrice = 149.0,
                    quantityAvailable = 5,
                    initialQuantity = 10,
                    pickupWindow = "Today 7:00 PM - 8:30 PM",
                    dietaryTags = listOf("Pure Veg", "Mithai Special", "Jain Friendly"),
                    co2SavedKg = 3.2,
                    imageUrl = "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=800&q=80"
                ),
                FoodPackageEntity(
                    id = "pkg_bistro_dinner_box",
                    merchantId = "merchant_green_bistro",
                    title = "Heritage South Tiffin & Dosa Surplus Assortment",
                    description = "3 fluffy Ghee Podi Idlis, 2 crispy Medu Vadas, 1 Mysore Masala Dosa, hot piping sambar, and fresh coconut & tomato chutneys.",
                    category = PackageCategory.MEALS,
                    originalPrice = 320.0,
                    discountedPrice = 99.0,
                    quantityAvailable = 4,
                    initialQuantity = 8,
                    pickupWindow = "Today 6:00 PM - 7:30 PM",
                    dietaryTags = listOf("Pure Veg", "100% Shakahari", "Fresh Morning Batch"),
                    co2SavedKg = 2.6,
                    imageUrl = "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=800&q=80"
                ),
                FoodPackageEntity(
                    id = "pkg_sushi_omakase_box",
                    merchantId = "merchant_tokyo_sushi",
                    title = "Grand Makhani Curry & Tandoor Feast Box",
                    description = "Hearty slow-cooked Dal Makhani, Paneer Butter Masala (or Chicken Makhani), 2 hot butter naans, jeera rice, and spiced salad.",
                    category = PackageCategory.MEALS,
                    originalPrice = 550.0,
                    discountedPrice = 189.0,
                    quantityAvailable = 4,
                    initialQuantity = 8,
                    pickupWindow = "Tonight 9:00 PM - 10:15 PM",
                    dietaryTags = listOf("North Indian", "High-Protein", "Freshly Cooked"),
                    co2SavedKg = 4.5,
                    imageUrl = "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&w=800&q=80"
                ),
                FoodPackageEntity(
                    id = "pkg_hyderabadi_biryani_pot",
                    merchantId = "merchant_paradise_biryani",
                    title = "Shahi Hyderabadi Dum Biryani Surplus Pot",
                    description = "Rich authentic Dum Biryani cooked with fragrant aged basmati rice, saffron, caramelised onions, Mirchi Ka Salan, and chilled cucumber raita.",
                    category = PackageCategory.MEALS,
                    originalPrice = 480.0,
                    discountedPrice = 179.0,
                    quantityAvailable = 3,
                    initialQuantity = 6,
                    pickupWindow = "Tonight 9:30 PM - 10:45 PM",
                    dietaryTags = listOf("Halal", "Chef Special", "Dum Cooked"),
                    co2SavedKg = 4.0,
                    imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80"
                ),
                FoodPackageEntity(
                    id = "pkg_nordic_fika_bag",
                    merchantId = "merchant_nordic_cafe",
                    title = "Irani Bun Maska, Osmania Biscuits & Puffs Box",
                    description = "2 crusty Irani Bun Maska, 6 famous Osmania fruit biscuits, 2 flaky vegetable puffs, and 2 traditional mawa cakes.",
                    category = PackageCategory.BAKERY,
                    originalPrice = 280.0,
                    discountedPrice = 89.0,
                    quantityAvailable = 3,
                    initialQuantity = 6,
                    pickupWindow = "Today 5:30 PM - 6:45 PM",
                    dietaryTags = listOf("Eggless", "Pure Veg", "Chai Time Classic"),
                    co2SavedKg = 2.1,
                    imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80"
                ),
                FoodPackageEntity(
                    id = "pkg_organic_produce_crate",
                    merchantId = "merchant_earth_market",
                    title = "Farm Fresh Desi Sabzi & Fruit Rescue Crate (5kg)",
                    description = "Generous 5kg crate of farm-harvested tomatoes, crisp Shimla capsicum, ginger, green chillies, seasonal fruits, and organic palak.",
                    category = PackageCategory.PRODUCE,
                    originalPrice = 350.0,
                    discountedPrice = 119.0,
                    quantityAvailable = 6,
                    initialQuantity = 8,
                    pickupWindow = "Today 6:00 PM - 7:30 PM",
                    dietaryTags = listOf("100% Organic", "Pure Veg", "Direct from Farmers"),
                    co2SavedKg = 5.8,
                    imageUrl = "https://images.unsplash.com/photo-1610832958506-aa56368176cf?auto=format&fit=crop&w=800&q=80"
                ),
                FoodPackageEntity(
                    id = "pkg_annadaan_zero_hunger",
                    merchantId = "merchant_robin_hood_ngo",
                    title = "Bhojan Annadaan Zero Hunger Meal Pack",
                    description = "Hot, freshly packed wholesome dal khichdi, seasonal sabzi, and 4 soft rotis prepared for free community surplus redistribution.",
                    category = PackageCategory.MEALS,
                    originalPrice = 150.0,
                    discountedPrice = 0.0,
                    isDonation = true,
                    quantityAvailable = 8,
                    initialQuantity = 10,
                    pickupWindow = "Tonight 8:00 PM - 9:30 PM",
                    dietaryTags = listOf("Pure Veg", "Community Annadaan", "Zero Waste"),
                    co2SavedKg = 3.5,
                    imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=800&q=80"
                )
            )
            db.foodPackageDao().insertPackages(packages)

            // Seed an active sample reservation for immediate test verification
            val initialOrder = OrderEntity(
                id = "ord_sample_active_01",
                orderNumber = "#SB-8492",
                customerId = "user_customer_elena",
                customerName = "Aarav Sharma",
                merchantId = "merchant_artisan_bakery",
                merchantName = "Bikaner Sweets & Namkeen Hub",
                packageId = "pkg_artisan_sourdough_box",
                packageTitle = "Royal Mithai & Namkeen Magic Box",
                quantity = 1,
                totalPrice = 149.0,
                totalSavings = 301.0,
                pickupPin = "849-210",
                qrPayload = "SAVEBITE:VERIFY:ord_sample_active_01:KEY9912",
                status = OrderStatus.RESERVED,
                pickupWindow = "Today 7:00 PM - 8:30 PM",
                co2SavedKg = 3.2,
                reservedAt = System.currentTimeMillis() - 1800000L, // 30 mins ago
                paymentMethod = "RAZORPAY_UPI",
                razorpayPaymentId = "pay_rzp_demo_8492"
            )
            db.orderDao().insertOrder(initialOrder)
        }
    }
}
