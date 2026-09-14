package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.OrderStatus
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteBadgeGreen
import com.example.ui.theme.SaveBiteBadgeGreenBg
import com.example.ui.theme.SaveBiteBadgeRed
import com.example.ui.theme.SaveBiteBadgeRedBg
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.SoundHapticsManager
import com.example.util.formatRupees

@Composable
fun AdminDashboardScreen(
    merchants: List<MerchantEntity>,
    packages: List<FoodPackageEntity>,
    orders: List<OrderEntity>,
    users: List<UserEntity>,
    onToggleMerchantVerification: (String, Boolean) -> Unit,
    onRestockAll: () -> Unit,
    onGenerateDemoOrder: () -> Unit,
    onEmergencyNgoBroadcast: (String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastMeals by remember { mutableStateOf("25") }
    var broadcastLocation by remember { mutableStateOf("Connaught Place Hub, New Delhi") }

    val totalCompletedOrders = orders.count { it.status == OrderStatus.COMPLETED }
    val totalRescuedMeals = 14820 + totalCompletedOrders
    val totalRupeesSaved = 284500.0 + orders.sumOf { it.totalSavings }
    val totalCo2Tons = 42.6 + (orders.sumOf { it.co2SavedKg } / 1000.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SaveBiteEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Shield",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SaveBite India Admin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SaveBiteBadgeGreenBg
                            ) {
                                Text(
                                    text = "LIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaveBiteBadgeGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Platform Control & Food Waste Moderation HQ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Platform KPI Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pan-India Rescue Impact",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Meals Rescued",
                        value = "%,d".format(totalRescuedMeals),
                        subtext = "Across 5 Metros",
                        icon = Icons.Default.Eco,
                        iconTint = SaveBiteEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Value Saved",
                        value = formatRupees(totalRupeesSaved),
                        subtext = "By Indian Consumers",
                        icon = Icons.Default.MonetizationOn,
                        iconTint = SaveBiteAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "CO₂ Diverted",
                        value = "${"%.1f".format(totalCo2Tons)} Tons",
                        subtext = "Landfill Methane Avoided",
                        icon = Icons.Default.Shield,
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Partners & Users",
                        value = "${merchants.size} Stores",
                        subtext = "${users.size} Verified Profiles",
                        icon = Icons.Default.People,
                        iconTint = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Admin Action Ribbon
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Super-Admin Actions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                onGenerateDemoOrder()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_btn_demo_order")
                        ) {
                            Icon(imageVector = Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Demo Order", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                onRestockAll()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteAmber),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_btn_restock_all")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restock All", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                showBroadcastDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteBadgeRed),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_btn_ngo_broadcast")
                        ) {
                            Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NGO Alert", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Sub-tabs for Admin Console
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SaveBiteEmerald
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        SoundHapticsManager.playClick(context)
                        selectedTab = 0
                    },
                    text = { Text("Merchants & FSSAI (${merchants.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("admin_tab_merchants")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        SoundHapticsManager.playClick(context)
                        selectedTab = 1
                    },
                    text = { Text("City Hubs", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("admin_tab_cities")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        SoundHapticsManager.playClick(context)
                        selectedTab = 2
                    },
                    text = { Text("Audit Stream (${orders.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("admin_tab_audit")
                )
            }
        }

        // Tab Content 0: Merchants Moderation
        if (selectedTab == 0) {
            items(merchants, key = { it.id }) { merchant ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = merchant.coverEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = merchant.businessName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${merchant.businessType} • ${merchant.address}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // Verification Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (merchant.verified) SaveBiteBadgeGreenBg else SaveBiteBadgeRedBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (merchant.verified) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (merchant.verified) SaveBiteBadgeGreen else SaveBiteBadgeRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (merchant.verified) "Verified" else "Pending Review",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (merchant.verified) SaveBiteBadgeGreen else SaveBiteBadgeRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val storePackages = packages.filter { it.merchantId == merchant.id }
                            Text(
                                text = "Active Deals: ${storePackages.size} • Pickup: ${merchant.pickupStartTime} - ${merchant.pickupEndTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            OutlinedButton(
                                onClick = {
                                    SoundHapticsManager.playClick(context)
                                    onToggleMerchantVerification(merchant.id, !merchant.verified)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (merchant.verified) "Revoke Verify" else "Approve FSSAI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab Content 1: City Hubs
        if (selectedTab == 1) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CityOperationsCard(
                        cityName = "Delhi NCR (Capital Region)",
                        outletsCount = 28,
                        dailyRescueMeals = "680 meals/day",
                        keyHubs = "Connaught Place, Cyber Hub Gurgaon, Lajpat Nagar",
                        status = "Optimal"
                    )
                    CityOperationsCard(
                        cityName = "Bengaluru (Silicon Valley)",
                        outletsCount = 24,
                        dailyRescueMeals = "540 meals/day",
                        keyHubs = "Indiranagar, Koramangala 80ft Rd, Whitefield",
                        status = "Expanding"
                    )
                    CityOperationsCard(
                        cityName = "Mumbai (Metropolitan Hub)",
                        outletsCount = 21,
                        dailyRescueMeals = "490 meals/day",
                        keyHubs = "Colaba Causeway, Bandra West, Dadar Tiffin Circle",
                        status = "Active"
                    )
                    CityOperationsCard(
                        cityName = "Hyderabad (Deccan Region)",
                        outletsCount = 18,
                        dailyRescueMeals = "390 meals/day",
                        keyHubs = "Banjara Hills Rd 12, Hitec City, Charminar",
                        status = "Active"
                    )
                }
            }
        }

        // Tab Content 2: Live Audit Stream
        if (selectedTab == 2) {
            if (orders.isEmpty()) {
                item {
                    Text(
                        text = "No system transactions recorded yet. Click 'Demo Order' above to trigger transactions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${order.orderNumber} • ${order.packageTitle}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Customer: ${order.customerName} → Store: ${order.merchantName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "PIN: ${order.pickupPin} • Paid: ${formatRupees(order.totalPrice)} • CO₂: ${"%.1f".format(order.co2SavedKg)} kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaveBiteEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (order.status) {
                                    OrderStatus.COMPLETED -> SaveBiteBadgeGreenBg
                                    OrderStatus.RESERVED -> Color(0xFFEFF6FF)
                                    OrderStatus.READY_FOR_PICKUP -> Color(0xFFFEF3C7)
                                    OrderStatus.CANCELLED -> SaveBiteBadgeRedBg
                                }
                            ) {
                                Text(
                                    text = order.status.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when (order.status) {
                                        OrderStatus.COMPLETED -> SaveBiteBadgeGreen
                                        OrderStatus.RESERVED -> Color(0xFF2563EB)
                                        OrderStatus.READY_FOR_PICKUP -> Color(0xFFD97706)
                                        OrderStatus.CANCELLED -> SaveBiteBadgeRed
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Emergency Annadaan NGO Broadcast Modal
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, tint = SaveBiteBadgeRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Emergency Food Rescue Broadcast")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Trigger immediate surplus distribution push notifications to volunteer networks (Robin Hood Army, Feeding India, local shelters) for large wedding or banquet surplus.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = broadcastMeals,
                        onValueChange = { broadcastMeals = it },
                        label = { Text("Number of Surplus Meals") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastLocation,
                        onValueChange = { broadcastLocation = it },
                        label = { Text("Kitchen / Venue Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = broadcastMeals.toIntOrNull() ?: 20
                        onEmergencyNgoBroadcast("Large Banquet Surplus Alert", count, broadcastLocation)
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaveBiteBadgeRed)
                ) {
                    Text("Dispatch Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun CityOperationsCard(
    cityName: String,
    outletsCount: Int,
    dailyRescueMeals: String,
    keyHubs: String,
    status: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SaveBiteBadgeGreenBg
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = SaveBiteBadgeGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Active Outlets: $outletsCount • Average: $dailyRescueMeals",
                style = MaterialTheme.typography.bodySmall,
                color = SaveBiteEmerald,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Key Zones: $keyHubs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
