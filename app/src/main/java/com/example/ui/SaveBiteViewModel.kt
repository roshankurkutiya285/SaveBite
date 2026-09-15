package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SaveBiteDatabase
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.BusinessType
import com.example.data.model.OrderStatus
import com.example.data.model.PackageCategory
import com.example.data.model.UserRole
import com.example.data.repository.SaveBiteRepository
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import com.example.util.EmailOtpManager
import com.example.util.JwtManager
import com.example.util.JwtSessionManager
import com.example.util.OtpDispatchInfo
import com.example.util.RazorpayPaymentManager
import com.example.util.RazorpayPaymentOrder
import com.example.util.RazorpayPaymentResult
import com.example.util.SoundHapticsManager
import com.example.util.formatRupees
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class SaveBiteTab {
    CUSTOMER,
    PICKUPS,
    MERCHANT_HUB,
    ADMIN,
    IMPACT,
    PROFILE
}

data class CelebrationEvent(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val statHighlight: String? = null,
    val isBadgeUnlock: Boolean = false
)

class SaveBiteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SaveBiteRepository

    // Current logged-in user profile (null means unauthenticated / show Login & Register)
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // JWT Session State
    private val jwtSessionManager = JwtSessionManager(application)
    private val _activeJwtToken = MutableStateFlow<String?>(null)
    val activeJwtToken: StateFlow<String?> = _activeJwtToken.asStateFlow()

    // Email OTP Dispatch State
    private val _activeOtpDispatch = MutableStateFlow<OtpDispatchInfo?>(null)
    val activeOtpDispatch: StateFlow<OtpDispatchInfo?> = _activeOtpDispatch.asStateFlow()

    // Razorpay Pending Checkout State
    private val _pendingRazorpayOrder = MutableStateFlow<RazorpayPaymentOrder?>(null)
    val pendingRazorpayOrder: StateFlow<RazorpayPaymentOrder?> = _pendingRazorpayOrder.asStateFlow()

    // Celebration modal event
    private val _celebrationEvent = MutableStateFlow<CelebrationEvent?>(null)
    val celebrationEvent: StateFlow<CelebrationEvent?> = _celebrationEvent.asStateFlow()

    // Theme & Appearance Customization
    private val _colorPalette = MutableStateFlow(AppColorPalette.ROYAL_SAFFRON)
    val colorPalette: StateFlow<AppColorPalette> = _colorPalette.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Navigation & UI selection state
    private val _currentTab = MutableStateFlow(SaveBiteTab.CUSTOMER)
    val currentTab: StateFlow<SaveBiteTab> = _currentTab.asStateFlow()

    private val _selectedPackage = MutableStateFlow<FoodPackageEntity?>(null)
    val selectedPackage: StateFlow<FoodPackageEntity?> = _selectedPackage.asStateFlow()

    private val _selectedMerchant = MutableStateFlow<MerchantEntity?>(null)
    val selectedMerchant: StateFlow<MerchantEntity?> = _selectedMerchant.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<PackageCategory?>(null)
    val selectedCategory: StateFlow<PackageCategory?> = _selectedCategory.asStateFlow()

    private val _selectedMerchantStoreId = MutableStateFlow("merchant_artisan_bakery")
    val selectedMerchantStoreId: StateFlow<String> = _selectedMerchantStoreId.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val allMerchants: StateFlow<List<MerchantEntity>>
    val allPackages: StateFlow<List<FoodPackageEntity>>
    val allOrders: StateFlow<List<OrderEntity>>
    val allUsers: StateFlow<List<UserEntity>>
    val customerOrders: StateFlow<List<OrderEntity>>
    val merchantOrders: StateFlow<List<OrderEntity>>
    val favoriteMerchantIds: StateFlow<Set<String>>

    // Combined filtered packages stream
    val filteredPackages: StateFlow<List<Pair<FoodPackageEntity, MerchantEntity>>>

    init {
        val db = SaveBiteDatabase.getDatabase(application)
        repository = SaveBiteRepository(db)

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            db.seedIfEmpty()
        }

        // Restore active JWT Session if exists and unexpired
        val savedJwt = jwtSessionManager.getAccessToken()
        val verifiedPayload = jwtSessionManager.getVerifiedPayload()
        if (savedJwt != null && verifiedPayload != null) {
            _activeJwtToken.value = savedJwt
            val restoredUser = UserEntity(
                id = verifiedPayload.sub,
                email = verifiedPayload.email,
                password = "jwt_authenticated",
                name = verifiedPayload.name,
                phone = "+91 98450 23145",
                role = verifiedPayload.role
            )
            _currentUser.value = restoredUser
            when (restoredUser.role) {
                UserRole.CUSTOMER -> _currentTab.value = SaveBiteTab.CUSTOMER
                UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> _currentTab.value = SaveBiteTab.MERCHANT_HUB
                UserRole.PICKUP_AGENT -> _currentTab.value = SaveBiteTab.PICKUPS
                UserRole.ADMIN -> _currentTab.value = SaveBiteTab.ADMIN
                UserRole.NGO -> _currentTab.value = SaveBiteTab.CUSTOMER
            }
        }

        allMerchants = repository.getAllMerchants()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allPackages = repository.getAllActivePackages()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allOrders = repository.getAllOrders()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allUsers = repository.getAllUsers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        @OptIn(ExperimentalCoroutinesApi::class)
        customerOrders = _currentUser.flatMapLatest { user ->
            if (user != null) {
                repository.getCustomerOrders(user.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        @OptIn(ExperimentalCoroutinesApi::class)
        merchantOrders = combine(_currentUser, _selectedMerchantStoreId) { user, storeId ->
            if (storeId.isNotBlank()) storeId else "merchant_artisan_bakery"
        }.flatMapLatest { storeId ->
            repository.getMerchantOrders(storeId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        @OptIn(ExperimentalCoroutinesApi::class)
        favoriteMerchantIds = _currentUser.flatMapLatest { user ->
            if (user != null) {
                repository.getFavoriteMerchantIds(user.id).map { it.toSet() }
            } else {
                flowOf(emptySet())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

        filteredPackages = combine(
            allPackages,
            allMerchants,
            _searchQuery,
            _selectedCategory
        ) { pkgs, merchants, query, category ->
            val merchantMap = merchants.associateBy { it.id }
            pkgs.mapNotNull { pkg ->
                val merchant = merchantMap[pkg.merchantId] ?: return@mapNotNull null
                val matchesCategory = category == null || pkg.category == category
                val matchesQuery = query.isBlank() ||
                        pkg.title.contains(query, ignoreCase = true) ||
                        merchant.businessName.contains(query, ignoreCase = true) ||
                        pkg.description.contains(query, ignoreCase = true)

                if (matchesCategory && matchesQuery) {
                    Pair(pkg, merchant)
                } else {
                    null
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun selectTab(tab: SaveBiteTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: PackageCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun selectPackage(pkg: FoodPackageEntity?) {
        _selectedPackage.value = pkg
        if (pkg != null) {
            val merchant = allMerchants.value.find { it.id == pkg.merchantId }
            _selectedMerchant.value = merchant
        }
    }

    fun selectMerchant(merchant: MerchantEntity?) {
        _selectedMerchant.value = merchant
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun applyWelcomeBenefitCode() {
        _celebrationEvent.value = CelebrationEvent(
            title = "Welcome Benefit Unlocked!",
            subtitle = "₹100 OFF auto-applied on your checkout for your first 3 surplus orders.",
            iconEmoji = "🎁",
            statHighlight = "Promo Code: WELCOME100 • ₹100 Instant Discount"
        )
        _snackbarMessage.value = "Welcome Benefit Applied! ₹100 OFF applied to your cart."
    }

    fun reservePackage(pkg: FoodPackageEntity, quantity: Int = 1) {
        val user = _currentUser.value
        if (user == null) {
            _snackbarMessage.value = "Please sign in or register to reserve food."
            return
        }
        viewModelScope.launch {
            val merchant = allMerchants.value.find { it.id == pkg.merchantId }
            if (merchant == null) {
                _snackbarMessage.value = "Merchant information not found."
                return@launch
            }

            val result = repository.reservePackage(user, merchant, pkg, quantity)
            result.onSuccess { order ->
                _snackbarMessage.value = "Reserved! Order ${order.orderNumber} is ready. Pickup PIN: ${order.pickupPin}"
                _selectedPackage.value = null
                _currentTab.value = SaveBiteTab.PICKUPS
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to reserve package."
            }
        }
    }

    fun toggleFavorite(merchantId: String) {
        val user = _currentUser.value ?: allUsers.value.firstOrNull { it.role == UserRole.CUSTOMER } ?: return
        viewModelScope.launch {
            val isFav = favoriteMerchantIds.value.contains(merchantId)
            repository.toggleFavorite(user.id, merchantId, isFav)
            _snackbarMessage.value = if (!isFav) "Added to your Liked deals!" else "Removed from Liked deals."
        }
    }

    // --- Authentication & Session Management ---
    fun login(identifier: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(identifier, password)
            result.onSuccess { user ->
                val jwt = JwtManager.generateToken(user)
                jwtSessionManager.saveToken(jwt)
                _activeJwtToken.value = jwt.accessToken
                _currentUser.value = user
                _snackbarMessage.value = "Welcome back, ${user.name}! (JWT Authenticated)"
                // Configure default tab according to role
                when (user.role) {
                    UserRole.CUSTOMER -> _currentTab.value = SaveBiteTab.CUSTOMER
                    UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> {
                        _currentTab.value = SaveBiteTab.MERCHANT_HUB
                        val userStore = allMerchants.value.find { it.userId == user.id }
                        if (userStore != null) {
                            _selectedMerchantStoreId.value = userStore.id
                        }
                    }
                    UserRole.PICKUP_AGENT -> _currentTab.value = SaveBiteTab.PICKUPS
                    UserRole.ADMIN -> _currentTab.value = SaveBiteTab.ADMIN
                    UserRole.NGO -> _currentTab.value = SaveBiteTab.CUSTOMER
                }
                onResult(true, "Logged in successfully with JWT session!")
            }.onFailure { err ->
                onResult(false, err.message ?: "Authentication failed.")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(name, email, phone, password, role)
            result.onSuccess { user ->
                val jwt = JwtManager.generateToken(user)
                jwtSessionManager.saveToken(jwt)
                _activeJwtToken.value = jwt.accessToken
                _currentUser.value = user
                _snackbarMessage.value = "Welcome to SaveBite India, ${user.name}!"
                when (user.role) {
                    UserRole.CUSTOMER -> _currentTab.value = SaveBiteTab.CUSTOMER
                    UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> {
                        _currentTab.value = SaveBiteTab.MERCHANT_HUB
                        _selectedMerchantStoreId.value = "merchant_${user.id}"
                    }
                    UserRole.PICKUP_AGENT -> _currentTab.value = SaveBiteTab.PICKUPS
                    UserRole.ADMIN -> _currentTab.value = SaveBiteTab.ADMIN
                    UserRole.NGO -> _currentTab.value = SaveBiteTab.CUSTOMER
                }
                onResult(true, "Registered successfully with JWT session!")
            }.onFailure { err ->
                onResult(false, err.message ?: "Registration failed.")
            }
        }
    }

    fun sendEmailOtp(email: String, onResult: (Boolean, OtpDispatchInfo?, String) -> Unit) {
        val res = EmailOtpManager.dispatchOtp(email)
        res.onSuccess { info ->
            _activeOtpDispatch.value = info
            _snackbarMessage.value = "📧 OTP sent to $email! (Valid for 5 minutes)"
            onResult(true, info, "OTP sent successfully")
        }.onFailure { err ->
            onResult(false, null, err.message ?: "Failed to generate OTP")
        }
    }

    fun verifyEmailOtp(
        email: String,
        code: String,
        role: UserRole = UserRole.CUSTOMER,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.loginWithEmailOtp(email, code, role)
            res.onSuccess { user ->
                val jwt = JwtManager.generateToken(user)
                jwtSessionManager.saveToken(jwt)
                _activeJwtToken.value = jwt.accessToken
                _currentUser.value = user
                _activeOtpDispatch.value = null
                _snackbarMessage.value = "Welcome, ${user.name}! (Email OTP Verified)"
                when (user.role) {
                    UserRole.CUSTOMER -> _currentTab.value = SaveBiteTab.CUSTOMER
                    UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> {
                        _currentTab.value = SaveBiteTab.MERCHANT_HUB
                        val store = allMerchants.value.find { it.userId == user.id }
                        if (store != null) _selectedMerchantStoreId.value = store.id
                    }
                    UserRole.PICKUP_AGENT -> _currentTab.value = SaveBiteTab.PICKUPS
                    UserRole.ADMIN -> _currentTab.value = SaveBiteTab.ADMIN
                    UserRole.NGO -> _currentTab.value = SaveBiteTab.CUSTOMER
                }
                onResult(true, "OTP verified and JWT created")
            }.onFailure { err ->
                onResult(false, err.message ?: "Invalid OTP")
            }
        }
    }

    fun logout() {
        jwtSessionManager.clearSession()
        _activeJwtToken.value = null
        _currentUser.value = null
        _selectedPackage.value = null
        _snackbarMessage.value = "Logged out successfully. Secure session cleared."
    }

    // --- Production Onboarding & Password Recovery ---
    fun registerWithOtp(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole,
        otpCode: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val verifyRes = EmailOtpManager.verifyOtp(email, otpCode)
            if (verifyRes.isFailure) {
                onResult(false, verifyRes.exceptionOrNull()?.message ?: "Invalid or expired OTP code.")
                return@launch
            }

            val result = repository.registerUser(name, email, phone, password, role)
            result.onSuccess { user ->
                val jwt = JwtManager.generateToken(user)
                jwtSessionManager.saveToken(jwt)
                _activeJwtToken.value = jwt.accessToken
                _currentUser.value = user
                _activeOtpDispatch.value = null
                _snackbarMessage.value = "Account created & verified! Welcome to SaveBite, ${user.name}."
                when (user.role) {
                    UserRole.CUSTOMER -> _currentTab.value = SaveBiteTab.CUSTOMER
                    UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> {
                        _currentTab.value = SaveBiteTab.MERCHANT_HUB
                        _selectedMerchantStoreId.value = "merchant_${user.id}"
                    }
                    UserRole.PICKUP_AGENT -> _currentTab.value = SaveBiteTab.PICKUPS
                    UserRole.ADMIN -> _currentTab.value = SaveBiteTab.ADMIN
                    UserRole.NGO -> _currentTab.value = SaveBiteTab.CUSTOMER
                }
                onResult(true, "Account created and verified successfully!")
            }.onFailure { err ->
                onResult(false, err.message ?: "Registration failed.")
            }
        }
    }

    fun forgotPassword(email: String, onResult: (Boolean, OtpDispatchInfo?, String) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                onResult(false, null, "Please enter a valid registered email address.")
                return@launch
            }
            val existing = repository.getUserByEmailOrPhone(cleanEmail)
            if (existing == null) {
                onResult(false, null, "No account found registered with $cleanEmail.")
                return@launch
            }
            val res = EmailOtpManager.dispatchOtp(cleanEmail)
            res.onSuccess { info ->
                _activeOtpDispatch.value = info
                _snackbarMessage.value = "Reset code sent to $cleanEmail."
                onResult(true, info, "Reset code dispatched successfully")
            }.onFailure { err ->
                onResult(false, null, err.message ?: "Failed to generate reset OTP.")
            }
        }
    }

    fun resetPasswordWithOtp(
        email: String,
        otpCode: String,
        newPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val verifyRes = EmailOtpManager.verifyOtp(email, otpCode)
            if (verifyRes.isFailure) {
                onResult(false, verifyRes.exceptionOrNull()?.message ?: "Invalid or expired OTP.")
                return@launch
            }
            val result = repository.resetPassword(email, newPass)
            result.onSuccess {
                _snackbarMessage.value = "Password reset successfully! You can now sign in."
                onResult(true, "Password updated successfully!")
            }.onFailure { err ->
                onResult(false, err.message ?: "Failed to reset password.")
            }
        }
    }

    fun updateProfile(name: String, phone: String, avatarUrl: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false, "No active session found.")
            return
        }
        viewModelScope.launch {
            val res = repository.updateProfile(user.id, name, phone, avatarUrl)
            res.onSuccess {
                _currentUser.value = user.copy(name = name.trim(), phone = phone.trim(), avatarUrl = avatarUrl)
                _snackbarMessage.value = "Profile details updated successfully."
                onResult(true, "Profile updated successfully.")
            }.onFailure { err ->
                onResult(false, err.message ?: "Failed to update profile.")
            }
        }
    }

    fun changePassword(currentPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false, "No active session found.")
            return
        }
        viewModelScope.launch {
            val res = repository.changePassword(user.id, currentPass, newPass)
            res.onSuccess {
                _snackbarMessage.value = "Password changed successfully."
                onResult(true, "Password changed successfully.")
            }.onFailure { err ->
                onResult(false, err.message ?: "Failed to update password.")
            }
        }
    }

    fun deleteAccount(onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false, "No active session found.")
            return
        }
        viewModelScope.launch {
            val res = repository.deleteAccount(user.id)
            res.onSuccess {
                logout()
                _snackbarMessage.value = "Your account and data have been completely deleted."
                onResult(true, "Account deleted.")
            }.onFailure { err ->
                onResult(false, err.message ?: "Failed to delete account.")
            }
        }
    }

    // --- Razorpay Payment Integration ---
    fun initiateRazorpayCheckout(pkg: FoodPackageEntity, quantity: Int = 1) {
        val user = _currentUser.value
        if (user == null) {
            _snackbarMessage.value = "Please sign in or register to reserve food."
            return
        }
        val merchant = allMerchants.value.find { it.id == pkg.merchantId } ?: allMerchants.value.firstOrNull()
        if (merchant == null) {
            _snackbarMessage.value = "Merchant details not available."
            return
        }

        if (pkg.discountedPrice <= 0.0) {
            // Annadaan free donation food package
            reservePackage(pkg, quantity)
            return
        }

        val totalAmount = pkg.discountedPrice * quantity
        val rzpOrder = RazorpayPaymentManager.createOrder(
            packageTitle = pkg.title,
            merchantName = merchant.businessName,
            quantity = quantity,
            amountRupees = totalAmount
        )
        _pendingRazorpayOrder.value = rzpOrder
    }

    fun onRazorpayPaymentSuccess(result: RazorpayPaymentResult.Success) {
        val pending = _pendingRazorpayOrder.value ?: return
        val pkg = allPackages.value.find { it.title == pending.packageTitle } ?: _selectedPackage.value
        val merchant = allMerchants.value.find { it.businessName == pending.merchantName } ?: _selectedMerchant.value
        val user = _currentUser.value ?: allUsers.value.firstOrNull { it.role == UserRole.CUSTOMER }

        if (pkg != null && merchant != null && user != null) {
            viewModelScope.launch {
                val reserveRes = repository.reservePackage(
                    customer = user,
                    merchant = merchant,
                    pkg = pkg,
                    quantity = pending.quantity,
                    paymentMethod = result.method.displayName,
                    razorpayPaymentId = result.paymentId
                )
                _pendingRazorpayOrder.value = null
                reserveRes.onSuccess { order ->
                    _selectedPackage.value = null
                    _currentTab.value = SaveBiteTab.PICKUPS
                    SoundHapticsManager.playSuccessChime(getApplication<Application>())
                    _celebrationEvent.value = CelebrationEvent(
                        title = "Payment Confirmed via Razorpay!",
                        subtitle = "Paid ${formatRupees(order.totalPrice)} • Order ${order.orderNumber} confirmed",
                        iconEmoji = "💳",
                        statHighlight = "Payment ID: ${result.paymentId}\nPickup PIN: ${order.pickupPin}"
                    )
                    _snackbarMessage.value = "Order ${order.orderNumber} booked! Razorpay Payment ID: ${result.paymentId}"
                }.onFailure { err ->
                    _snackbarMessage.value = "Order reservation failed: ${err.message}"
                }
            }
        } else {
            _pendingRazorpayOrder.value = null
        }
    }

    fun dismissRazorpayCheckout() {
        _pendingRazorpayOrder.value = null
    }

    fun selectMerchantStore(merchantId: String) {
        _selectedMerchantStoreId.value = merchantId
    }

    fun updatePackageStock(packageId: String, newQty: Int) {
        viewModelScope.launch {
            repository.updatePackageStock(packageId, newQty.coerceAtLeast(0))
            _snackbarMessage.value = "Inventory updated to $newQty units."
        }
    }

    fun togglePackageActive(packageId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.togglePackageActive(packageId, isActive)
            _snackbarMessage.value = if (isActive) "Package listing activated" else "Package listing paused"
        }
    }

    fun updateMerchantVerification(merchantId: String, verified: Boolean) {
        viewModelScope.launch {
            repository.updateMerchantVerification(merchantId, verified)
            _snackbarMessage.value = if (verified) "Merchant verified and approved!" else "Merchant status changed to pending."
        }
    }

    fun restockAllPackages() {
        viewModelScope.launch {
            repository.restockAllPackages()
            _snackbarMessage.value = "All active surplus food packages restocked to full capacity."
        }
    }

    fun markOrderReadyForPickup(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.READY_FOR_PICKUP)
            _snackbarMessage.value = "Order handed over to Pickup Partner! Delivery agent is en route for doorstep delivery."
        }
    }

    fun redeemOrder(pinOrQr: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.verifyAndCompletePickup(pinOrQr)
            result.onSuccess { order ->
                _snackbarMessage.value = "Verified! Order ${order.orderNumber} successfully collected."
                _celebrationEvent.value = CelebrationEvent(
                    title = "Surplus Rescued!",
                    subtitle = "Order ${order.orderNumber} (${order.packageTitle}) is successfully verified and collected.",
                    iconEmoji = "🌱",
                    statHighlight = "🎉 Avoided ${"%.1f".format(order.co2SavedKg)} kg CO₂ • Saved ${formatRupees(order.totalSavings)}",
                    isBadgeUnlock = false
                )
                onResult(true, "Collected: ${order.packageTitle} (${order.orderNumber})")
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Verification failed."
                onResult(false, err.message ?: "Verification failed.")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val result = repository.cancelOrder(orderId)
            result.onSuccess { (order, refund) ->
                if (refund != null) {
                    _snackbarMessage.value = "Reservation ${order.orderNumber} cancelled. Instant refund of ${formatRupees(refund.amountRupees)} initiated via Razorpay (${refund.refundId})."
                    _celebrationEvent.value = CelebrationEvent(
                        title = "Refund Processed via Razorpay",
                        subtitle = "Instant reversal of ${formatRupees(refund.amountRupees)} sent to original payment method",
                        iconEmoji = "💰",
                        statHighlight = "Refund ID: ${refund.refundId}\nSpeed: Instant UPI / Bank Reversal\nOrder: ${order.orderNumber}",
                        isBadgeUnlock = false
                    )
                } else {
                    _snackbarMessage.value = "Reservation ${order.orderNumber} cancelled. Inventory returned to marketplace."
                }
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to cancel reservation."
            }
        }
    }

    fun submitOrderFeedback(orderId: String, rating: Int, reviewText: String, reviewTags: String) {
        viewModelScope.launch {
            val result = repository.submitOrderFeedback(orderId, rating, reviewText, reviewTags)
            result.onSuccess {
                _snackbarMessage.value = "Thank you! Your $rating★ review has been recorded."
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to save feedback."
            }
        }
    }

    fun triggerCelebration(event: CelebrationEvent) {
        _celebrationEvent.value = event
    }

    fun dismissCelebration() {
        _celebrationEvent.value = null
    }

    fun setColorPalette(palette: AppColorPalette) {
        _colorPalette.value = palette
        _snackbarMessage.value = "Theme switched to ${palette.displayName}"
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        _snackbarMessage.value = "Theme mode: ${mode.displayName}"
    }

    fun generateDemoTestOrder() {
        viewModelScope.launch {
            val packages = allPackages.value
            val merchants = allMerchants.value
            if (packages.isNotEmpty() && merchants.isNotEmpty()) {
                val pkg = packages.first()
                val merchant = merchants.firstOrNull { it.id == pkg.merchantId } ?: merchants.first()
                val user = _currentUser.value ?: allUsers.value.firstOrNull { it.role == UserRole.CUSTOMER } ?: UserEntity(
                    id = "user_demo_test",
                    email = "demo.test@savebite.in",
                    password = "password123",
                    name = "Demo Customer",
                    phone = "+91 99999 11111",
                    role = UserRole.CUSTOMER
                )
                val result = repository.reservePackage(user, merchant, pkg, 1)
                result.onSuccess { order ->
                    _snackbarMessage.value = "Demo order ${order.orderNumber} created! Ready in Pickups Dashboard."
                }.onFailure {
                    _snackbarMessage.value = "Could not generate demo order: ${it.message}"
                }
            }
        }
    }

    fun dispatchEmergencyNgoAlert(title: String, quantity: Int, location: String) {
        viewModelScope.launch {
            _snackbarMessage.value = "🚨 Annadaan Emergency Alert sent to Robin Hood Army & Feeding India for $quantity surplus meals at $location!"
        }
    }

    fun registerMerchantShop(
        businessName: String,
        businessType: BusinessType,
        description: String,
        address: String,
        pickupInstructions: String,
        coverEmoji: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.createMerchantStoreProfile(
                userId = user.id,
                businessName = businessName,
                businessType = businessType,
                description = description,
                address = address,
                pickupInstructions = pickupInstructions,
                coverEmoji = coverEmoji
            )
            res.onSuccess { store ->
                _selectedMerchantStoreId.value = store.id
                _snackbarMessage.value = "Shop '${store.businessName}' registered successfully!"
            }
        }
    }

    fun createMerchantSurplusBag(
        title: String,
        description: String,
        category: PackageCategory,
        originalPrice: Double,
        discountedPrice: Double,
        quantity: Int,
        pickupWindow: String,
        dietaryTags: List<String>,
        isDonation: Boolean = false,
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            val newPkg = FoodPackageEntity(
                id = "pkg_${UUID.randomUUID().toString().take(8)}",
                merchantId = _selectedMerchantStoreId.value,
                title = title.ifBlank { "Evening Food Rescue Bag" },
                description = description.ifBlank { "Daily fresh surplus baked goods at over 65% off." },
                category = category,
                originalPrice = if (originalPrice > 0) originalPrice else 200.0,
                discountedPrice = if (isDonation) 0.0 else (if (discountedPrice > 0) discountedPrice else 89.0),
                quantityAvailable = if (quantity > 0) quantity else 5,
                initialQuantity = if (quantity > 0) quantity else 5,
                pickupWindow = pickupWindow.ifBlank { "Today 7:30 PM - 8:30 PM" },
                dietaryTags = dietaryTags.ifEmpty { listOf("Pure Veg") },
                co2SavedKg = 2.5,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=800&q=80" },
                isDonation = isDonation
            )
            repository.createPackage(newPkg)
            _snackbarMessage.value = if (isDonation) "NGO Charity Donation Package published!" else "New surplus package published to nearby consumers!"
        }
    }
}
