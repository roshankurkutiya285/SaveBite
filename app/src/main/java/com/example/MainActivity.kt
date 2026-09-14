package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.SaveBiteTab
import com.example.ui.SaveBiteViewModel
import com.example.ui.components.CelebrationDialog
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.ImpactScreen
import com.example.ui.screens.MerchantDashboardScreen
import com.example.ui.screens.OrdersScreen
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
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val packagesWithMerchants by viewModel.filteredPackages.collectAsStateWithLifecycle()
    val allPackages by viewModel.allPackages.collectAsStateWithLifecycle()
    val customerOrders by viewModel.customerOrders.collectAsStateWithLifecycle()
    val merchantOrders by viewModel.merchantOrders.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteMerchantIds.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPackage by viewModel.selectedPackage.collectAsStateWithLifecycle()
    val selectedMerchant by viewModel.selectedMerchant.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val celebrationEvent by viewModel.celebrationEvent.collectAsStateWithLifecycle()
    val colorPalette by viewModel.colorPalette.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

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
                        Text(
                            text = "SaveBite",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaveBiteEmerald
                        )
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
                        // User Role Indicator
                        Text(
                            text = currentUser.role.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SaveBiteAmber,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (selectedPackage == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == SaveBiteTab.EXPLORE,
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            viewModel.selectTab(SaveBiteTab.EXPLORE)
                        },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                        label = { Text("Explore") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaveBiteEmerald,
                            selectedTextColor = SaveBiteEmerald,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    NavigationBarItem(
                        selected = currentTab == SaveBiteTab.PICKUPS,
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            viewModel.selectTab(SaveBiteTab.PICKUPS)
                        },
                        icon = { Icon(Icons.Default.QrCode2, contentDescription = "My Pickups") },
                        label = { Text("My Pickups") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaveBiteEmerald,
                            selectedTextColor = SaveBiteEmerald,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_pickups")
                    )

                    NavigationBarItem(
                        selected = currentTab == SaveBiteTab.MERCHANT_HUB,
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            viewModel.selectTab(SaveBiteTab.MERCHANT_HUB)
                        },
                        icon = { Icon(Icons.Default.Store, contentDescription = "Merchant") },
                        label = { Text("Merchant") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaveBiteEmerald,
                            selectedTextColor = SaveBiteEmerald,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_merchant")
                    )

                    NavigationBarItem(
                        selected = currentTab == SaveBiteTab.IMPACT,
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            viewModel.selectTab(SaveBiteTab.IMPACT)
                        },
                        icon = { Icon(Icons.Default.Nature, contentDescription = "Impact") },
                        label = { Text("Impact") },
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
            if (selectedPackage != null) {
                PackageDetailScreen(
                    pkg = selectedPackage!!,
                    merchant = selectedMerchant,
                    onBack = { viewModel.selectPackage(null) },
                    onReserve = { quantity ->
                        viewModel.reservePackage(selectedPackage!!, quantity)
                    }
                )
            } else {
                when (currentTab) {
                    SaveBiteTab.EXPLORE -> {
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
                        OrdersScreen(
                            orders = customerOrders,
                            onSimulateRedeem = { pin ->
                                viewModel.redeemOrder(pin) { _, _ -> }
                            },
                            onCancelOrder = { orderId ->
                                viewModel.cancelOrder(orderId)
                            },
                            onExploreClick = { viewModel.selectTab(SaveBiteTab.EXPLORE) }
                        )
                    }
                    SaveBiteTab.MERCHANT_HUB -> {
                        MerchantDashboardScreen(
                            merchantName = "Le Petit Pain Artisan Bakery",
                            packages = allPackages.filter { it.merchantId == "merchant_artisan_bakery" },
                            merchantOrders = merchantOrders,
                            onRedeemCode = { code ->
                                viewModel.redeemOrder(code) { _, _ -> }
                            },
                            onCreatePackage = { title, desc, cat, orig, disc, qty, window, tags, isDonation ->
                                viewModel.createMerchantSurplusBag(title, desc, cat, orig, disc, qty, window, tags, isDonation)
                            }
                        )
                    }
                    SaveBiteTab.IMPACT -> {
                        ImpactScreen(
                            currentUser = currentUser,
                            customerOrders = customerOrders,
                            currentColorPalette = colorPalette,
                            currentThemeMode = themeMode,
                            onSelectColorPalette = { viewModel.setColorPalette(it) },
                            onSelectThemeMode = { viewModel.setThemeMode(it) },
                            onSwitchRole = { viewModel.switchUserRole(it) },
                            onBadgeClick = { badgeName, desc, emoji, isUnlocked ->
                                viewModel.triggerCelebration(
                                    com.example.ui.CelebrationEvent(
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
        }
    }
}
