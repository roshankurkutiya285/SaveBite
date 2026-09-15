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

    // --- Users & Authentication ---
    fun getUser(userId: String): Flow<UserEntity?> = db.userDao().getUserById(userId)

    suspend fun getUserByEmailOrPhone(identifier: String): UserEntity? =
        db.userDao().getUserByEmailOrPhone(identifier.trim().lowercase())

    suspend fun saveUser(user: UserEntity) = db.userDao().insertUser(user)

    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()
        val cleanPass = password.trim()
        val cleanPhone = phone.trim().ifEmpty { "+91 98000 12345" }

        if (cleanName.isBlank()) return Result.failure(IllegalArgumentException("Please enter your name."))
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (cleanPass.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))
        }

        val existing = db.userDao().getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("An account with $cleanEmail already exists. Please log in."))
        }

        val userId = "user_${UUID.randomUUID().toString().take(8)}"
        val hashedPassword = com.example.util.PasswordHasher.hashPassword(cleanPass)
        val newUser = UserEntity(
            id = userId,
            email = cleanEmail,
            password = hashedPassword,
            name = cleanName,
            phone = cleanPhone,
            role = role,
            isEmailVerified = true
        )
        db.userDao().insertUser(newUser)

        // If registered as merchant, auto-create their merchant store in the database
        if (role == UserRole.RESTAURANT || role == UserRole.BAKERY || role == UserRole.CAFE || role == UserRole.SUPERMARKET) {
            val merchantId = "merchant_${newUser.id}"
            val newMerchant = MerchantEntity(
                id = merchantId,
                userId = newUser.id,
                businessName = if (cleanName.endsWith("s", ignoreCase = true)) "$cleanName' Kitchen" else "$cleanName's Kitchen",
                businessType = if (role == UserRole.BAKERY) com.example.data.model.BusinessType.BAKERY else com.example.data.model.BusinessType.RESTAURANT,
                description = "Locally freshly prepared surplus meals, bakery bakes, and snacks rescued from daily waste.",
                address = "Sector 18, Commercial Hub",
                distanceKm = 1.1,
                latitude = 28.6139,
                longitude = 77.2090,
                rating = 4.8,
                reviewCount = 18,
                pickupStartTime = "18:30",
                pickupEndTime = "21:00",
                pickupInstructions = "Show your 6-digit pickup PIN or QR code at the counter.",
                verified = true,
                coverEmoji = if (role == UserRole.BAKERY) "🥐" else "🍛"
            )
            db.merchantDao().insertMerchant(newMerchant)
        }

        return Result.success(newUser)
    }

    suspend fun loginUser(identifier: String, password: String): Result<UserEntity> {
        val cleanId = identifier.trim().lowercase()
        val cleanPass = password.trim()
        if (cleanId.isBlank()) return Result.failure(IllegalArgumentException("Please enter your email or phone."))
        if (cleanPass.isBlank()) return Result.failure(IllegalArgumentException("Please enter your password."))

        val user = db.userDao().getUserByEmailOrPhone(cleanId)
            ?: return Result.failure(IllegalArgumentException("No account found for '$cleanId'. Please register first."))

        val now = System.currentTimeMillis()
        if (user.lockoutUntilMs > now) {
            val remainingSec = ((user.lockoutUntilMs - now) / 1000).coerceAtLeast(1)
            return Result.failure(IllegalStateException("Account is temporarily locked due to repeated failed logins. Please try again in $remainingSec seconds or reset password."))
        }

        val isValidPassword = com.example.util.PasswordHasher.verifyPassword(cleanPass, user.password)
        if (!isValidPassword) {
            val newAttempts = user.failedLoginAttempts + 1
            if (newAttempts >= 5) {
                val lockoutDuration = 15 * 60 * 1000L // 15 min
                db.userDao().updateLockout(user.id, newAttempts, now + lockoutDuration)
                return Result.failure(IllegalStateException("Account locked for 15 minutes after 5 failed login attempts. Please reset your password or try again later."))
            } else {
                db.userDao().updateLockout(user.id, newAttempts, 0L)
                val remainingAttempts = 5 - newAttempts
                return Result.failure(IllegalArgumentException("Incorrect password. $remainingAttempts attempt(s) remaining before lockout."))
            }
        }

        // Login successful: clear lockout & attempt counter
        if (user.failedLoginAttempts > 0 || user.lockoutUntilMs > 0L) {
            db.userDao().updateLockout(user.id, 0, 0L)
        }

        // Seamlessly rehash password if legacy
        if (com.example.util.PasswordHasher.needsRehash(user.password)) {
            val newHash = com.example.util.PasswordHasher.hashPassword(cleanPass)
            db.userDao().updatePassword(user.id, newHash)
        }

        return Result.success(user)
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = newPassword.trim()
        if (cleanPass.length < 6) {
            return Result.failure(IllegalArgumentException("New password must be at least 6 characters long."))
        }
        val user = db.userDao().getUserByEmail(cleanEmail)
            ?: return Result.failure(IllegalArgumentException("No registered account found for '$cleanEmail'."))

        val hashed = com.example.util.PasswordHasher.hashPassword(cleanPass)
        db.userDao().updatePassword(user.id, hashed)
        db.userDao().updateLockout(user.id, 0, 0L)
        return Result.success(Unit)
    }

    suspend fun updateProfile(userId: String, name: String, phone: String, avatarUrl: String): Result<Unit> {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        if (cleanName.isBlank()) return Result.failure(IllegalArgumentException("Name cannot be blank."))
        db.userDao().updateProfile(userId, cleanName, cleanPhone, avatarUrl)
        return Result.success(Unit)
    }

    suspend fun changePassword(userId: String, currentPass: String, newPass: String): Result<Unit> {
        val user = db.userDao().getUserByEmailOrPhone(userId)
        val targetUser = user ?: run {
            val all = db.userDao().getUserCount()
            if (all == 0) null else null
        }
        val cleanNew = newPass.trim()
        if (cleanNew.length < 6) {
            return Result.failure(IllegalArgumentException("New password must be at least 6 characters long."))
        }
        val hashed = com.example.util.PasswordHasher.hashPassword(cleanNew)
        db.userDao().updatePassword(userId, hashed)
        return Result.success(Unit)
    }

    suspend fun deleteAccount(userId: String): Result<Unit> {
        db.userDao().deleteUser(userId)
        return Result.success(Unit)
    }

    suspend fun loginWithEmailOtp(
        email: String,
        code: String,
        role: UserRole = UserRole.CUSTOMER
    ): Result<UserEntity> {
        val verifyRes = com.example.util.EmailOtpManager.verifyOtp(email, code)
        if (verifyRes.isFailure) {
            return Result.failure(verifyRes.exceptionOrNull() ?: IllegalArgumentException("Invalid or expired OTP."))
        }

        val cleanEmail = email.trim().lowercase()
        var user = db.userDao().getUserByEmail(cleanEmail)
        if (user == null) {
            // Auto register user if new
            val namePart = cleanEmail.substringBefore("@")
                .split(".", "_", "-")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                .ifBlank { "SaveBite Rescuer" }

            val randomSecret = UUID.randomUUID().toString()
            val newUser = UserEntity(
                id = "user_${UUID.randomUUID().toString().take(8)}",
                email = cleanEmail,
                password = com.example.util.PasswordHasher.hashPassword(randomSecret),
                name = namePart,
                phone = "+91 98${Random.nextInt(10000000, 99999999)}",
                role = role,
                isEmailVerified = true
            )
            db.userDao().insertUser(newUser)
            user = newUser
        }

        return Result.success(user)
    }

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
        quantity: Int = 1,
        paymentMethod: String = "RAZORPAY_UPI",
        razorpayPaymentId: String? = null,
        promoCode: String? = null
    ): Result<OrderEntity> {
        if (pkg.quantityAvailable < quantity) {
            return Result.failure(IllegalStateException("Package is sold out or insufficient quantity available."))
        }

        // Decrement stock
        val updated = db.foodPackageDao().decrementStock(pkg.id, quantity)
        if (updated <= 0) {
            return Result.failure(IllegalStateException("Could not lock stock for this reservation."))
        }

        val baseTotal = pkg.discountedPrice * quantity
        val promoDiscount = if (!promoCode.isNullOrEmpty() && (promoCode.equals("WELCOME100", ignoreCase = true) || promoCode.equals("FIRST3", ignoreCase = true))) {
            100.0.coerceAtMost(baseTotal)
        } else 0.0

        val finalTotal = (baseTotal - promoDiscount).coerceAtLeast(0.0)
        val totalSavings = ((pkg.originalPrice - pkg.discountedPrice) * quantity) + promoDiscount

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
            totalPrice = finalTotal,
            totalSavings = totalSavings,
            pickupPin = pickupPin,
            qrPayload = qrPayload,
            status = OrderStatus.RESERVED,
            pickupWindow = pkg.pickupWindow,
            co2SavedKg = pkg.co2SavedKg * quantity,
            reservedAt = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            razorpayPaymentId = razorpayPaymentId
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

    suspend fun cancelOrder(orderId: String): Result<Pair<OrderEntity, com.example.util.RazorpayRefund?>> {
        val order = db.orderDao().getOrderDirect(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found."))

        if (order.status == OrderStatus.COMPLETED) {
            return Result.failure(IllegalStateException("Cannot cancel an order that has already been collected."))
        }

        db.orderDao().updateOrderStatus(orderId, OrderStatus.CANCELLED)
        db.foodPackageDao().incrementStock(order.packageId, order.quantity)

        val refund = if (order.totalPrice > 0.0) {
            com.example.util.RazorpayPaymentManager.initiateRefund(
                orderId = order.id,
                paymentId = order.razorpayPaymentId,
                amountRupees = order.totalPrice
            )
        } else null

        return Result.success(Pair(order.copy(status = OrderStatus.CANCELLED), refund))
    }

    suspend fun submitOrderFeedback(orderId: String, rating: Int, reviewText: String, reviewTags: String): Result<Boolean> {
        val updated = db.orderDao().updateOrderFeedback(orderId, rating, reviewText, reviewTags)
        if (updated > 0) {
            val order = db.orderDao().getOrderDirect(orderId)
            if (order != null) {
                db.merchantDao().updateMerchantRating(order.merchantId, rating.toDouble())
            }
            return Result.success(true)
        }
        return Result.failure(IllegalArgumentException("Order not found or feedback update failed."))
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = db.orderDao().getAllOrders()

    fun getAllUsers(): Flow<List<UserEntity>> = db.userDao().getAllUsers()

    suspend fun updateMerchantVerification(merchantId: String, verified: Boolean) =
        db.merchantDao().updateMerchantVerification(merchantId, verified)

    suspend fun updatePackageStock(packageId: String, quantity: Int) =
        db.foodPackageDao().updateStock(packageId, quantity)

    suspend fun togglePackageActive(packageId: String, isActive: Boolean) =
        db.foodPackageDao().updateActiveStatus(packageId, isActive)

    suspend fun restockAllPackages() =
        db.foodPackageDao().restockAllPackages()

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) =
        db.orderDao().updateOrderStatus(orderId, status)

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
