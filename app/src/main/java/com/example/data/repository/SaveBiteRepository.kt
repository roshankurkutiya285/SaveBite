package com.example.data.repository

import com.example.data.local.SaveBiteDatabase
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PackageCategory
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

class SaveBiteRepository(private val db: SaveBiteDatabase) {

    // --- Users ---
    fun getUser(userId: String): Flow<UserEntity?> = db.userDao().getUserById(userId)

    suspend fun saveUser(user: UserEntity) = db.userDao().insertUser(user)

    // --- Merchants ---
    fun getAllMerchants(): Flow<List<MerchantEntity>> = db.merchantDao().getAllMerchants()

    fun getMerchant(merchantId: String): Flow<MerchantEntity?> = db.merchantDao().getMerchantById(merchantId)

    // --- Packages ---
    fun getAllActivePackages(): Flow<List<FoodPackageEntity>> = db.foodPackageDao().getAllActivePackages()

    fun getPackage(packageId: String): Flow<FoodPackageEntity?> = db.foodPackageDao().getPackageById(packageId)

    fun getPackagesByMerchant(merchantId: String): Flow<List<FoodPackageEntity>> =
        db.foodPackageDao().getPackagesByMerchant(merchantId)

    fun getPackagesByCategory(category: PackageCategory): Flow<List<FoodPackageEntity>> =
        db.foodPackageDao().getPackagesByCategory(category)

    suspend fun createPackage(pkg: FoodPackageEntity) = db.foodPackageDao().insertPackage(pkg)

    // --- Orders & Reservation ---
    fun getCustomerOrders(customerId: String): Flow<List<OrderEntity>> =
        db.orderDao().getOrdersByCustomer(customerId)

    fun getMerchantOrders(merchantId: String): Flow<List<OrderEntity>> =
        db.orderDao().getOrdersByMerchant(merchantId)

    fun getOrder(orderId: String): Flow<OrderEntity?> = db.orderDao().getOrderById(orderId)

    suspend fun reservePackage(
        customer: UserEntity,
        merchant: MerchantEntity,
        pkg: FoodPackageEntity,
        quantity: Int = 1
    ): Result<OrderEntity> {
        if (pkg.quantityAvailable < quantity) {
            return Result.failure(IllegalStateException("Package is sold out or insufficient quantity available."))
        }

        // Decrement stock
        val updated = db.foodPackageDao().decrementStock(pkg.id, quantity)
        if (updated <= 0) {
            return Result.failure(IllegalStateException("Could not lock stock for this reservation."))
        }

        val randomPinPart1 = Random.nextInt(100, 999)
        val randomPinPart2 = Random.nextInt(100, 999)
        val pickupPin = "$randomPinPart1-$randomPinPart2"
        val orderShortId = Random.nextInt(1000, 9999)
        val orderNumber = "#SB-$orderShortId"
        val orderId = "ord_${UUID.randomUUID().toString().take(8)}"
        val qrPayload = "SAVEBITE:VERIFY:$orderId:$pickupPin"

        val order = OrderEntity(
            id = orderId,
            orderNumber = orderNumber,
            customerId = customer.id,
            customerName = customer.name,
            merchantId = merchant.id,
            merchantName = merchant.businessName,
            packageId = pkg.id,
            packageTitle = pkg.title,
            quantity = quantity,
            totalPrice = pkg.discountedPrice * quantity,
            totalSavings = (pkg.originalPrice - pkg.discountedPrice) * quantity,
            pickupPin = pickupPin,
            qrPayload = qrPayload,
            status = OrderStatus.RESERVED,
            pickupWindow = pkg.pickupWindow,
            co2SavedKg = pkg.co2SavedKg * quantity,
            reservedAt = System.currentTimeMillis()
        )

        db.orderDao().insertOrder(order)
        return Result.success(order)
    }

    suspend fun verifyAndCompletePickup(inputQuery: String): Result<OrderEntity> {
        val sanitized = inputQuery.trim()
        val order = db.orderDao().findOrderByPinOrQr(sanitized, sanitized)
            ?: return Result.failure(IllegalArgumentException("Invalid QR code or Pickup PIN. Please verify with customer."))

        if (order.status == OrderStatus.COMPLETED) {
            return Result.failure(IllegalStateException("Order ${order.orderNumber} has already been collected."))
        }

        db.orderDao().markOrderCompleted(order.id, OrderStatus.COMPLETED, System.currentTimeMillis())
        return Result.success(order.copy(status = OrderStatus.COMPLETED, completedAt = System.currentTimeMillis()))
    }

    suspend fun cancelOrder(orderId: String): Result<OrderEntity> {
        val order = db.orderDao().getOrderDirect(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found."))

        if (order.status == OrderStatus.COMPLETED) {
            return Result.failure(IllegalStateException("Cannot cancel an order that has already been collected."))
        }

        db.orderDao().updateOrderStatus(orderId, OrderStatus.CANCELLED)
        db.foodPackageDao().incrementStock(order.packageId, order.quantity)
        return Result.success(order.copy(status = OrderStatus.CANCELLED))
    }

    // --- Favorites ---
    fun getFavoriteMerchantIds(userId: String): Flow<List<String>> =
        db.favoriteDao().getFavoriteMerchantIds(userId)

    suspend fun toggleFavorite(userId: String, merchantId: String, isFavCurrently: Boolean) {
        if (isFavCurrently) {
            db.favoriteDao().removeFavorite(userId, merchantId)
        } else {
            db.favoriteDao().addFavorite(FavoriteEntity(userId = userId, merchantId = merchantId))
        }
    }
}
