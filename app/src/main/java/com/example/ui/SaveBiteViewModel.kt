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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class SaveBiteTab {
    EXPLORE,
    PICKUPS,
    MERCHANT_HUB,
    IMPACT
}

class SaveBiteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SaveBiteRepository

    // Current logged-in user profile
    private val _currentUser = MutableStateFlow(
        UserEntity(
            id = "user_customer_elena",
            email = "elena.green@savebite.com",
            name = "Elena Rostova",
            phone = "+1 (555) 349-8102",
            role = UserRole.CUSTOMER
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Navigation & UI selection state
    private val _currentTab = MutableStateFlow(SaveBiteTab.EXPLORE)
    val currentTab: StateFlow<SaveBiteTab> = _currentTab.asStateFlow()

    private val _selectedPackage = MutableStateFlow<FoodPackageEntity?>(null)
    val selectedPackage: StateFlow<FoodPackageEntity?> = _selectedPackage.asStateFlow()

    private val _selectedMerchant = MutableStateFlow<MerchantEntity?>(null)
    val selectedMerchant: StateFlow<MerchantEntity?> = _selectedMerchant.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<PackageCategory?>(null)
    val selectedCategory: StateFlow<PackageCategory?> = _selectedCategory.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val allMerchants: StateFlow<List<MerchantEntity>>
    val allPackages: StateFlow<List<FoodPackageEntity>>
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

        allMerchants = repository.getAllMerchants()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allPackages = repository.getAllActivePackages()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        customerOrders = repository.getCustomerOrders("user_customer_elena")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        merchantOrders = repository.getMerchantOrders("merchant_artisan_bakery")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        favoriteMerchantIds = repository.getFavoriteMerchantIds("user_customer_elena")
            .combine(MutableStateFlow(emptySet<String>())) { list, _ -> list.toSet() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

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

    fun reservePackage(pkg: FoodPackageEntity, quantity: Int = 1) {
        viewModelScope.launch {
            val merchant = allMerchants.value.find { it.id == pkg.merchantId }
            if (merchant == null) {
                _snackbarMessage.value = "Merchant information not found."
                return@launch
            }

            val result = repository.reservePackage(_currentUser.value, merchant, pkg, quantity)
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
        viewModelScope.launch {
            val isFav = favoriteMerchantIds.value.contains(merchantId)
            repository.toggleFavorite(_currentUser.value.id, merchantId, isFav)
        }
    }

    fun redeemOrder(pinOrQr: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.verifyAndCompletePickup(pinOrQr)
            result.onSuccess { order ->
                _snackbarMessage.value = "Verified! Order ${order.orderNumber} successfully collected."
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
            result.onSuccess { order ->
                _snackbarMessage.value = "Reservation ${order.orderNumber} cancelled. Inventory returned."
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to cancel reservation."
            }
        }
    }

    fun switchUserRole(role: UserRole) {
        when (role) {
            UserRole.CUSTOMER -> {
                _currentUser.value = UserEntity(
                    id = "user_customer_elena",
                    email = "elena.green@savebite.com",
                    name = "Elena Rostova",
                    phone = "+1 (555) 349-8102",
                    role = UserRole.CUSTOMER
                )
                _snackbarMessage.value = "Switched to Consumer Mode (Elena Rostova)"
            }
            UserRole.BAKERY, UserRole.RESTAURANT -> {
                _currentUser.value = UserEntity(
                    id = "user_merchant_artisan",
                    email = "manager@artisansourdough.com",
                    name = "Chef Marco Valenti (Le Petit Pain)",
                    phone = "+1 (555) 782-9014",
                    role = role
                )
                _currentTab.value = SaveBiteTab.MERCHANT_HUB
                _snackbarMessage.value = "Switched to Merchant Mode (Chef Marco Valenti)"
            }
            UserRole.NGO -> {
                _currentUser.value = UserEntity(
                    id = "user_ngo_rescue",
                    email = "coord@cityfoodrescue.org",
                    name = "David Chen (City Food Rescue)",
                    phone = "+1 (555) 670-3341",
                    role = UserRole.NGO
                )
                _snackbarMessage.value = "Switched to NGO Partner Mode (David Chen)"
            }
            else -> {
                _currentUser.value = _currentUser.value.copy(role = role)
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
        isDonation: Boolean = false
    ) {
        viewModelScope.launch {
            val newPkg = FoodPackageEntity(
                id = "pkg_${UUID.randomUUID().toString().take(8)}",
                merchantId = "merchant_artisan_bakery",
                title = title.ifBlank { "Evening Bakery Rescue Bag" },
                description = description.ifBlank { "Daily fresh surplus baked goods at over 65% off." },
                category = category,
                originalPrice = if (originalPrice > 0) originalPrice else 20.0,
                discountedPrice = if (isDonation) 0.0 else (if (discountedPrice > 0) discountedPrice else 5.99),
                quantityAvailable = if (quantity > 0) quantity else 5,
                initialQuantity = if (quantity > 0) quantity else 5,
                pickupWindow = pickupWindow.ifBlank { "Today 7:30 PM - 8:30 PM" },
                dietaryTags = dietaryTags.ifEmpty { listOf("Vegetarian") },
                co2SavedKg = 2.5,
                isDonation = isDonation
            )
            repository.createPackage(newPkg)
            _snackbarMessage.value = if (isDonation) "NGO Charity Donation Package published!" else "New surplus package published to nearby consumers!"
        }
    }
}
