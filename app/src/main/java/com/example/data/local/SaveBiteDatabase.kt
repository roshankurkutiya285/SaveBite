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
    version = 2,
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
                    "savebite_production_db_v2"
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
            // Seed Users
            val users = listOf(
                UserEntity(
                    id = "user_customer_elena",
                    email = "elena.green@savebite.com",
                    name = "Elena Rostova",
                    phone = "+1 (555) 349-8102",
                    role = UserRole.CUSTOMER
                ),
                UserEntity(
                    id = "user_merchant_artisan",
                    email = "manager@artisansourdough.com",
                    name = "Chef Marco Valenti",
                    phone = "+1 (555) 782-9014",
                    role = UserRole.BAKERY
                ),
                UserEntity(
                    id = "user_merchant_bistro",
                    email = "kitchen@greenleafbistro.com",
                    name = "Aisha Khan",
                    phone = "+1 (555) 441-2390",
                    role = UserRole.RESTAURANT
                ),
                UserEntity(
                    id = "user_ngo_rescue",
                    email = "coord@cityfoodrescue.org",
                    name = "David Chen",
                    phone = "+1 (555) 670-3341",
                    role = UserRole.NGO
                )
            )
            db.userDao().insertUsers(users)

            // Seed Merchants
            val merchants = listOf(
                MerchantEntity(
                    id = "merchant_artisan_bakery",
                    userId = "user_merchant_artisan",
                    businessName = "Le Petit Pain Artisan Bakery",
                    businessType = BusinessType.BAKERY,
                    description = "Traditional slow-fermented sourdoughs, French croissants, and viennoiserie baked fresh daily.",
                    address = "142 Baker Street, Downtown",
                    distanceKm = 0.8,
                    latitude = 37.7749,
                    longitude = -122.4194,
                    rating = 4.9,
                    reviewCount = 318,
                    pickupStartTime = "19:00",
                    pickupEndTime = "20:15",
                    pickupInstructions = "Enter via the side bakery entrance. Present your SaveBite QR code to the counter barista.",
                    coverEmoji = "🥐"
                ),
                MerchantEntity(
                    id = "merchant_green_bistro",
                    userId = "user_merchant_bistro",
                    businessName = "Green Leaf Organic Bistro",
                    businessType = BusinessType.RESTAURANT,
                    description = "Farm-to-table warm grain bowls, seasonal roasted vegetables, grilled salmon, and Mediterranean sides.",
                    address = "78 Market Avenue, Mid-Town",
                    distanceKm = 1.4,
                    latitude = 37.7833,
                    longitude = -122.4167,
                    rating = 4.8,
                    reviewCount = 245,
                    pickupStartTime = "20:30",
                    pickupEndTime = "21:30",
                    pickupInstructions = "Go to the dedicated pickup counter near the kitchen pass. Ask for Aisha or head chef.",
                    coverEmoji = "🥗"
                ),
                MerchantEntity(
                    id = "merchant_tokyo_sushi",
                    userId = "user_merchant_sushi",
                    businessName = "Tokyo Bay Premium Sushi & Bento",
                    businessType = BusinessType.RESTAURANT,
                    description = "Chef's selection surplus sashimi, nigiri rolls, gyoza, and miso soups crafted before dinner close.",
                    address = "210 Pine Boulevard",
                    distanceKm = 2.1,
                    latitude = 37.7910,
                    longitude = -122.4040,
                    rating = 4.9,
                    reviewCount = 412,
                    pickupStartTime = "21:00",
                    pickupEndTime = "22:00",
                    pickupInstructions = "Show your digital SaveBite pickup pass at the host stand. Keep bag upright.",
                    coverEmoji = "🍱"
                ),
                MerchantEntity(
                    id = "merchant_nordic_cafe",
                    userId = "user_merchant_cafe",
                    businessName = "Fika Nordic Espresso Bar",
                    businessType = BusinessType.CAFE,
                    description = "Cardamom buns, cinnamon twists, gourmet breakfast bagels, and cold pressed seasonal juices.",
                    address = "45 Elmway Square",
                    distanceKm = 1.1,
                    latitude = 37.7690,
                    longitude = -122.4467,
                    rating = 4.7,
                    reviewCount = 189,
                    pickupStartTime = "17:30",
                    pickupEndTime = "18:30",
                    pickupInstructions = "Counter pickup. Please bring your own reusable container or bag if possible!",
                    coverEmoji = "☕"
                ),
                MerchantEntity(
                    id = "merchant_earth_market",
                    userId = "user_merchant_market",
                    businessName = "Harvest Moon Organics & Produce",
                    businessType = BusinessType.SUPERMARKET,
                    description = "Fresh surplus organic fruits, heirloom vegetables, artisanal cheeses, and dairy near shelf-date.",
                    address = "334 Orchard Lane",
                    distanceKm = 2.8,
                    latitude = 37.7550,
                    longitude = -122.4200,
                    rating = 4.6,
                    reviewCount = 156,
                    pickupStartTime = "18:00",
                    pickupEndTime = "19:30",
                    pickupInstructions = "Customer service desk at the store entrance. Bags are pre-packed in chiller boxes.",
                    coverEmoji = "🥑"
                )
            )
            db.merchantDao().insertMerchants(merchants)

            // Seed Food Packages
            val packages = listOf(
                FoodPackageEntity(
                    id = "pkg_artisan_sourdough_box",
                    merchantId = "merchant_artisan_bakery",
                    title = "Artisan Pastry & Sourdough Surprise Bag",
                    description = "A delightful surprise bag containing 1 whole crusty loaf (country sourdough or seeded rye) plus 3-4 freshly baked morning pastries (croissant, pain au chocolat, or danish).",
                    category = PackageCategory.BAKERY,
                    originalPrice = 22.00,
                    discountedPrice = 6.99,
                    quantityAvailable = 4,
                    initialQuantity = 8,
                    pickupWindow = "Today 7:00 PM - 8:15 PM",
                    dietaryTags = listOf("Vegetarian"),
                    co2SavedKg = 2.8
                ),
                FoodPackageEntity(
                    id = "pkg_bistro_dinner_box",
                    merchantId = "merchant_green_bistro",
                    title = "Chef's Harvest Dinner Box",
                    description = "Hearty warm dinner portion of our seasonal Mediterranean bowl, roasted baby potatoes, organic herb chicken or grilled tofu, plus house dressing.",
                    category = PackageCategory.MEALS,
                    originalPrice = 28.50,
                    discountedPrice = 8.50,
                    quantityAvailable = 3,
                    initialQuantity = 6,
                    pickupWindow = "Today 8:30 PM - 9:30 PM",
                    dietaryTags = listOf("Nut-Free", "High-Protein"),
                    co2SavedKg = 3.6
                ),
                FoodPackageEntity(
                    id = "pkg_sushi_omakase_box",
                    merchantId = "merchant_tokyo_sushi",
                    title = "Late Night Sushi & Roll Assortment",
                    description = "Freshly prepared dinner roll set (salmon avocado, spicy tuna, or California), edamame, and crispy vegetable tempura prepared fresh today.",
                    category = PackageCategory.MEALS,
                    originalPrice = 32.00,
                    discountedPrice = 10.99,
                    quantityAvailable = 5,
                    initialQuantity = 10,
                    pickupWindow = "Tonight 9:00 PM - 10:00 PM",
                    dietaryTags = listOf("Pescatarian"),
                    co2SavedKg = 4.2
                ),
                FoodPackageEntity(
                    id = "pkg_nordic_fika_bag",
                    merchantId = "merchant_nordic_cafe",
                    title = "Nordic Fika & Gourmet Sandwich Pack",
                    description = "2 freshly baked Swedish cardamom buns and 1 gourmet focaccia sandwich (smoked turkey or caprese) with fresh artisan dressing.",
                    category = PackageCategory.BAKERY,
                    originalPrice = 18.00,
                    discountedPrice = 5.49,
                    quantityAvailable = 2,
                    initialQuantity = 5,
                    pickupWindow = "Today 5:30 PM - 6:30 PM",
                    dietaryTags = listOf("Vegetarian Option"),
                    co2SavedKg = 2.1
                ),
                FoodPackageEntity(
                    id = "pkg_organic_produce_crate",
                    merchantId = "merchant_earth_market",
                    title = "Organic Fruit & Crisp Veggie Rescue Crate",
                    description = "Hefty 4kg rescue box of ripe avocados, bananas, crisp honeycrisp apples, heirloom tomatoes, and organic baby greens.",
                    category = PackageCategory.PRODUCE,
                    originalPrice = 30.00,
                    discountedPrice = 9.99,
                    quantityAvailable = 6,
                    initialQuantity = 8,
                    pickupWindow = "Today 6:00 PM - 7:30 PM",
                    dietaryTags = listOf("Vegan", "Gluten-Free", "100% Organic"),
                    co2SavedKg = 5.4
                )
            )
            db.foodPackageDao().insertPackages(packages)

            // Seed an active sample reservation for immediate test verification
            val initialOrder = OrderEntity(
                id = "ord_sample_active_01",
                orderNumber = "#SB-8492",
                customerId = "user_customer_elena",
                customerName = "Elena Rostova",
                merchantId = "merchant_artisan_bakery",
                merchantName = "Le Petit Pain Artisan Bakery",
                packageId = "pkg_artisan_sourdough_box",
                packageTitle = "Artisan Pastry & Sourdough Surprise Bag",
                quantity = 1,
                totalPrice = 6.99,
                totalSavings = 15.01,
                pickupPin = "849-210",
                qrPayload = "SAVEBITE:VERIFY:ord_sample_active_01:KEY9912",
                status = OrderStatus.RESERVED,
                pickupWindow = "Today 7:00 PM - 8:15 PM",
                co2SavedKg = 2.8,
                reservedAt = System.currentTimeMillis() - 1800000L // 30 mins ago
            )
            db.orderDao().insertOrder(initialOrder)
        }
    }
}
