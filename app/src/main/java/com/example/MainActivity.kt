package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import com.example.data.model.UserRole
import com.example.ui.CelebrationEvent
import com.example.ui.SaveBiteTab
import com.example.ui.SaveBiteViewModel
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.RazorpayCheckoutSheet
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.ImpactScreen
import com.example.ui.screens.MerchantDashboardScreen
import com.example.ui.screens.NgoDashboardScreen
import com.example.ui.screens.PickupsDashboardScreen
import com.example.ui.screens.PackageDetailScreen
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import com.example.ui.theme.SaveBiteTheme
import com.example.util.SoundHapticsManager

class MainActivity : ComponentActivity() {

    private val viewModel: SaveBiteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val colorPalette by viewModel.colorPalette.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            SaveBiteTheme(palette = colorPalette, darkTheme = isDark) {
                SaveBiteApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBiteApp(viewModel: SaveBiteViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val celebrationEvent by viewModel.celebrationEvent.collectAsStateWithLifecycle()
    val activeOtpDispatch by viewModel.activeOtpDispatch.collectAsStateWithLifecycle()
    val activeJwtToken by viewModel.activeJwtToken.collectAsStateWithLifecycle()
    val pendingRazorpayOrder by viewModel.pendingRazorpayOrder.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    if (currentUser == null) {
        // Show Register & Login Screen
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            AuthScreen(
                onLogin = { identifier, password, callback ->
                    viewModel.login(identifier, password, callback)
                },
                onRegister = { name, email, phone, password, role, callback ->
                    viewModel.register(name, email, phone, password, role, callback)
                },
                onQuickLogin = { role ->
                    viewModel.quickLogin(role)
                },
                onSendOtp = { email, callback ->
                    viewModel.sendEmailOtp(email, callback)
                },
                onVerifyOtp = { email, code, role, callback ->
                    viewModel.verifyEmailOtp(email, code, role, callback)
                },
                activeOtpDispatch = activeOtpDispatch,
                activeJwtToken = activeJwtToken,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        // Logged-in user: Show role-based dashboard!
        val user = currentUser!!
        val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
        val packagesWithMerchants by viewModel.filteredPackages.collectAsStateWithLifecycle()
        val allPackages by viewModel.allPackages.collectAsStateWithLifecycle()
        val allMerchants by viewModel.allMerchants.collectAsStateWithLifecycle()
        val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
        val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
        val selectedMerchantStoreId by viewModel.selectedMerchantStoreId.collectAsStateWithLifecycle()
        val customerOrders by viewModel.customerOrders.collectAsStateWithLifecycle()
        val merchantOrders by viewModel.merchantOrders.collectAsStateWithLifecycle()
        val favoriteIds by viewModel.favoriteMerchantIds.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
        val selectedPackage by viewModel.selectedPackage.collectAsStateWithLifecycle()
        val selectedMerchant by viewModel.selectedMerchant.collectAsStateWithLifecycle()
        val colorPalette by viewModel.colorPalette.collectAsStateWithLifecycle()
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        val context = LocalContext.current

        var showRoleMenu by remember { mutableStateOf(false) }

        // Handle back button when detailed package is open
        BackHandler(enabled = selectedPackage != null) {
            viewModel.selectPackage(null)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (selectedPackage == null) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "SaveBite India",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SaveBiteEmerald
                                )
                                Text(
                                    text = when (user.role) {
                                        UserRole.CUSTOMER -> "Customer Food Rescue Hub"
                                        UserRole.PICKUP_AGENT -> "Pickup & Logistics Partner"
                                        UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> "Merchant Kitchen Portal"
                                        UserRole.ADMIN -> "Pan-India Admin Console"
                                        UserRole.NGO -> "Annadaan NGO Rescue Hub"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "SaveBite Logo",
                                tint = SaveBiteEmerald,
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .size(28.dp)
                            )
                        },
                        actions = {
                            // User Profile Pill & Quick Role Switcher
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SaveBiteAmber.copy(alpha = 0.15f),
                                    modifier = Modifier.clickable { showRoleMenu = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (user.role) {
                                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                                UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> Icons.Default.Store
                                                UserRole.PICKUP_AGENT -> Icons.Default.LocalShipping
                                                UserRole.NGO -> Icons.Default.VolunteerActivism
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            tint = SaveBiteAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = user.name.substringBefore(" ").take(10),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showRoleMenu,
                                    onDismissRequest = { showRoleMenu = false }
                                ) {
                                    Surface(
                                        color = SaveBiteEmerald.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = SaveBiteEmerald,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "JWT Session: Active",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SaveBiteEmerald
                                            )
                                        }
                                    }
                                    DropdownMenuItem(
                                        text = { Text("Customer: Aarav Sharma") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                        onClick = {
                                            viewModel.quickLogin(UserRole.CUSTOMER)
                                            showRoleMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Merchant: Bikaner Sweets") },
                                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                                        onClick = {
                                            viewModel.quickLogin(UserRole.BAKERY)
                                            showRoleMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Pickup Partner: Rajat Verma") },
                                        leadingIcon = { Icon(Icons.Default.LocalShipping, contentDescription = null) },
                                        onClick = {
                                            viewModel.quickLogin(UserRole.PICKUP_AGENT)
                                            showRoleMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Admin: Rajesh Verma (HQ)") },
                                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                                        onClick = {
                                            viewModel.quickLogin(UserRole.ADMIN)
                                            showRoleMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("NGO: Robin Hood Army") },
                                        leadingIcon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null) },
                                        onClick = {
                                            viewModel.quickLogin(UserRole.NGO)
                                            showRoleMenu = false
                                        }
                                    )
                                }
                            }

                            // Logout Button
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .testTag("btn_logout")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Log Out",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            },
            bottomBar = {
                // BottomBar is ONLY shown for CUSTOMER role (Deals, My Pickups, Impact).
                // Merchants, Pickup Agents, Admins, and NGOs have their dedicated single-purpose dashboards!
                if (selectedPackage == null && user.role == UserRole.CUSTOMER) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == SaveBiteTab.CUSTOMER,
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                viewModel.selectTab(SaveBiteTab.CUSTOMER)
                            },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Deals") },
                            label = { Text("Deals", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SaveBiteEmerald,
                                selectedTextColor = SaveBiteEmerald,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_customer")
                        )

                        NavigationBarItem(
                            selected = currentTab == SaveBiteTab.PICKUPS,
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                viewModel.selectTab(SaveBiteTab.PICKUPS)
                            },
                            icon = { Icon(Icons.Default.QrCode2, contentDescription = "My Pickups") },
                            label = { Text("My Pickups", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SaveBiteEmerald,
                                selectedTextColor = SaveBiteEmerald,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_pickups")
                        )

                        NavigationBarItem(
                            selected = currentTab == SaveBiteTab.IMPACT,
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                viewModel.selectTab(SaveBiteTab.IMPACT)
                            },
                            icon = { Icon(Icons.Default.Nature, contentDescription = "Impact") },
                            label = { Text("Impact", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SaveBiteEmerald,
                                selectedTextColor = SaveBiteEmerald,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_impact")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (user.role) {
                    UserRole.CUSTOMER -> {
                        if (selectedPackage != null) {
                            PackageDetailScreen(
                                pkg = selectedPackage!!,
                                merchant = selectedMerchant,
                                onBack = { viewModel.selectPackage(null) },
                                onReserve = { quantity ->
                                    viewModel.initiateRazorpayCheckout(selectedPackage!!, quantity)
                                }
                            )
                        } else {
                            when (currentTab) {
                                SaveBiteTab.CUSTOMER -> {
                                    CustomerHomeScreen(
                                        packages = packagesWithMerchants,
                                        searchQuery = searchQuery,
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        selectedCategory = selectedCategory,
                                        onCategorySelect = { viewModel.selectCategory(it) },
                                        favoriteIds = favoriteIds,
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onPackageClick = { viewModel.selectPackage(it) }
                                    )
                                }
                                SaveBiteTab.PICKUPS -> {
                                    PickupsDashboardScreen(
                                        orders = customerOrders,
                                        onSimulateRedeem = { pin ->
                                            viewModel.redeemOrder(pin) { _, _ -> }
                                        },
                                        onCancelOrder = { orderId ->
                                            viewModel.cancelOrder(orderId)
                                        },
                                        onExploreClick = { viewModel.selectTab(SaveBiteTab.CUSTOMER) }
                                    )
                                }
                                SaveBiteTab.IMPACT -> {
                                    ImpactScreen(
                                        currentUser = user,
                                        customerOrders = customerOrders,
                                        currentColorPalette = colorPalette,
                                        currentThemeMode = themeMode,
                                        onSelectColorPalette = { viewModel.setColorPalette(it) },
                                        onSelectThemeMode = { viewModel.setThemeMode(it) },
                                        onSwitchRole = { viewModel.quickLogin(it) },
                                        onBadgeClick = { badgeName, desc, emoji, isUnlocked ->
                                            viewModel.triggerCelebration(
                                                CelebrationEvent(
                                                    title = badgeName,
                                                    subtitle = desc,
                                                    iconEmoji = emoji,
                                                    statHighlight = if (isUnlocked) "🎉 Verified Eco Badge Unlocked!" else "🔒 Keep rescuing surplus food to unlock this achievement!",
                                                    isBadgeUnlock = true
                                                )
                                            )
                                        }
                                    )
                                }
                                else -> {
                                    CustomerHomeScreen(
                                        packages = packagesWithMerchants,
                                        searchQuery = searchQuery,
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        selectedCategory = selectedCategory,
                                        onCategorySelect = { viewModel.selectCategory(it) },
                                        favoriteIds = favoriteIds,
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onPackageClick = { viewModel.selectPackage(it) }
                                    )
                                }
                            }
                        }
                    }

                    UserRole.BAKERY, UserRole.RESTAURANT, UserRole.CAFE, UserRole.SUPERMARKET -> {
                        val activeMerchant = allMerchants.firstOrNull { it.id == selectedMerchantStoreId }
                            ?: allMerchants.firstOrNull { it.userId == user.id }
                            ?: allMerchants.firstOrNull()
                        val storePackages = allPackages.filter { it.merchantId == (activeMerchant?.id ?: "merchant_artisan_bakery") }

                        MerchantDashboardScreen(
                            merchantName = activeMerchant?.businessName ?: user.name,
                            packages = storePackages,
                            merchantOrders = merchantOrders,
                            onRedeemCode = { code ->
                                viewModel.redeemOrder(code) { _, _ -> }
                            },
                            onCreatePackage = { title, desc, cat, orig, disc, qty, window, tags, isDonation ->
                                viewModel.createMerchantSurplusBag(title, desc, cat, orig, disc, qty, window, tags, isDonation)
                            },
                            allMerchants = allMerchants,
                            selectedMerchantId = selectedMerchantStoreId,
                            onSelectMerchantStore = { viewModel.selectMerchantStore(it) },
                            onUpdateStock = { pkgId, newQty -> viewModel.updatePackageStock(pkgId, newQty) }
                        )
                    }

                    UserRole.PICKUP_AGENT -> {
                        PickupsDashboardScreen(
                            orders = allOrders,
                            onSimulateRedeem = { pin ->
                                viewModel.redeemOrder(pin) { _, _ -> }
                            },
                            onCancelOrder = { orderId ->
                                viewModel.cancelOrder(orderId)
                            },
                            onExploreClick = { }
                        )
                    }

                    UserRole.ADMIN -> {
                        AdminDashboardScreen(
                            merchants = allMerchants,
                            packages = allPackages,
                            orders = allOrders,
                            users = allUsers,
                            onToggleMerchantVerification = { merchantId, verified ->
                                viewModel.updateMerchantVerification(merchantId, verified)
                            },
                            onRestockAll = { viewModel.restockAllPackages() },
                            onGenerateDemoOrder = { viewModel.generateDemoTestOrder() },
                            onEmergencyNgoBroadcast = { title, qty, location ->
                                viewModel.dispatchEmergencyNgoAlert(title, qty, location)
                            }
                        )
                    }

                    UserRole.NGO -> {
                        NgoDashboardScreen(
                            ngoName = user.name,
                            donationPackages = allPackages,
                            onClaimDonation = { pkgId ->
                                val pkg = allPackages.find { it.id == pkgId }
                                if (pkg != null) {
                                    viewModel.reservePackage(pkg, 1)
                                }
                            },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }

                // Global Confetti & Haptic Celebration Modal
                celebrationEvent?.let { event ->
                    CelebrationDialog(
                        isVisible = true,
                        title = event.title,
                        subtitle = event.subtitle,
                        iconEmoji = event.iconEmoji,
                        statHighlight = event.statHighlight,
                        isBadgeUnlock = event.isBadgeUnlock,
                        onDismiss = { viewModel.dismissCelebration() }
                    )
                }

                // Razorpay Payment Gateway Modal
                pendingRazorpayOrder?.let { order ->
                    RazorpayCheckoutSheet(
                        order = order,
                        onPaymentSuccess = { result ->
                            viewModel.onRazorpayPaymentSuccess(result)
                        },
                        onPaymentFailed = { _ ->
                            viewModel.dismissRazorpayCheckout()
                        },
                        onDismiss = {
                            viewModel.dismissRazorpayCheckout()
                        }
                    )
                }
            }
        }
    }
}
