package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.model.BusinessType
import com.example.data.model.OrderStatus
import com.example.data.model.PackageCategory
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteBadgeGreen
import com.example.ui.theme.SaveBiteBadgeGreenBg
import com.example.ui.theme.SaveBiteBadgeRed
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.SoundHapticsManager
import com.example.util.formatRupees

@Composable
fun MerchantDashboardScreen(
    merchantName: String,
    userMerchant: MerchantEntity?,
    packages: List<FoodPackageEntity>,
    merchantOrders: List<OrderEntity>,
    onRegisterShop: (
        businessName: String,
        businessType: BusinessType,
        description: String,
        address: String,
        pickupInstructions: String,
        coverEmoji: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onHandoverToPartner: (orderId: String) -> Unit = {},
    onRedeemCode: (String) -> Unit = {},
    onCreatePackage: (
        title: String,
        description: String,
        category: PackageCategory,
        originalPrice: Double,
        discountedPrice: Double,
        quantity: Int,
        pickupWindow: String,
        dietaryTags: List<String>,
        isDonation: Boolean,
        imageUrl: String
    ) -> Unit,
    onUpdateStock: (String, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val pendingOrders = merchantOrders.filter { it.status == OrderStatus.RESERVED || it.status == OrderStatus.READY_FOR_PICKUP }
    val completedOrders = merchantOrders.filter { it.status == OrderStatus.COMPLETED }
    val totalRevenue = completedOrders.sumOf { it.totalPrice }
    val totalRescued = completedOrders.sumOf { it.quantity }
    val activeBagsAvailable = packages.sumOf { it.quantityAvailable }
    val totalCo2Saved = completedOrders.sumOf { it.co2SavedKg }

    val navTabs = listOf(
        Pair("Overview", Icons.Default.Storefront),
        Pair("Orders (${pendingOrders.size})", Icons.Default.MonetizationOn),
        Pair("Inventory (${packages.size})", Icons.Default.Inventory2),
        Pair("Analytics", Icons.Default.Analytics),
        Pair("Store & Staff", Icons.Default.Settings)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("merchant_dashboard_screen")
    ) {
        // Top Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SaveBiteEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = userMerchant?.coverEmoji ?: "🏪", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userMerchant?.businessName ?: merchantName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (userMerchant?.verified == true) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Merchant",
                                    tint = SaveBiteEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Merchant Operations Portal",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaveBiteEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showNotificationsDialog = true }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { showSupportDialog = true }) {
                        Icon(Icons.Default.Help, contentDescription = "Support", tint = SaveBiteEmerald)
                    }
                }
            }
        }

        // Multi-Tab Navigation Bar (Shopify / Stripe Style)
        if (userMerchant != null) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SaveBiteEmerald,
                edgePadding = 16.dp
            ) {
                navTabs.forEachIndexed { index, (label, icon) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            selectedTab = index
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unregistered Store Setup Screen
            if (userMerchant == null) {
                item {
                    RegisterShopCard(
                        merchantName = merchantName,
                        onRegisterShop = onRegisterShop
                    )
                }
            } else {
                when (selectedTab) {
                    // TAB 0: OVERVIEW
                    0 -> {
                        item {
                            Column {
                                Text(
                                    text = "Today's Business Overview",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MetricSummaryCard(
                                        title = "Revenue Today",
                                        value = formatRupees(totalRevenue),
                                        subtitle = "Completed sales",
                                        icon = Icons.Default.MonetizationOn,
                                        iconTint = SaveBiteEmerald,
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricSummaryCard(
                                        title = "Orders Picked",
                                        value = "$totalRescued",
                                        subtitle = "Bags collected",
                                        icon = Icons.Default.Inventory2,
                                        iconTint = SaveBiteAmber,
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricSummaryCard(
                                        title = "CO₂ Avoided",
                                        value = "${"%.1f".format(totalCo2Saved)} kg",
                                        subtitle = "Landfill reduction",
                                        icon = Icons.Default.Nature,
                                        iconTint = SaveBiteBadgeGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Quick Action Buttons
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Manage Inventory", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(text = "$activeBagsAvailable active bags available for consumers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                                    }

                                    Button(
                                        onClick = { showCreateDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Post Dish")
                                    }
                                }
                            }
                        }

                        // Awaiting Handover Queue Preview
                        if (pendingOrders.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Awaiting Handover Queue (${pendingOrders.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(pendingOrders.take(3), key = { it.id }) { order ->
                                PendingOrderCard(order = order, onHandoverToPartner = onHandoverToPartner)
                            }
                        }
                    }

                    // TAB 1: ORDERS & REVENUE
                    1 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Orders & Fulfillment Queue",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${pendingOrders.size} pending",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SaveBiteEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (pendingOrders.isEmpty()) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(32.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🛍️", fontSize = 44.sp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("All Orders Handed Over", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("New customer orders will appear here in real time.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                                    }
                                }
                            }
                        } else {
                            items(pendingOrders, key = { it.id }) { order ->
                                PendingOrderCard(order = order, onHandoverToPartner = onHandoverToPartner)
                            }
                        }

                        if (completedOrders.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Completed Orders Audit Log (${completedOrders.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }

                            items(completedOrders, key = { it.id }) { order ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = SaveBiteEmerald,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = order.customerName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "${order.quantity}x ${order.packageTitle}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Order #${order.orderNumber}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = formatRupees(order.totalPrice),
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SaveBiteEmerald
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = SaveBiteBadgeGreenBg
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = SaveBiteBadgeGreen,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "COMPLETED",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 10.sp,
                                                        color = SaveBiteBadgeGreen,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 2: INVENTORY & DISHES
                    2 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Dishes & Stock Inventory (${packages.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { showCreateDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Post New Dish")
                                }
                            }
                        }

                        items(packages, key = { it.id }) { pkg ->
                            InventoryItemCard(pkg = pkg, onUpdateStock = onUpdateStock)
                        }

                        // Scheduled Listings Section
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = SaveBiteAmber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Scheduled Evening Listings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Automated daily release scheduled for 7:30 PM closing batch.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // TAB 3: ANALYTICS & REPORTS
                    3 -> {
                        // Key Business KPI Cards
                        item {
                            Column {
                                Text(
                                    text = "Key Performance Indicators (KPIs)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MetricSummaryCard(
                                        title = "Avg Order Value",
                                        value = "₹148",
                                        subtitle = "+12.4% vs last week",
                                        icon = Icons.Default.MonetizationOn,
                                        iconTint = SaveBiteEmerald,
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricSummaryCard(
                                        title = "Repeat Rate",
                                        value = "72%",
                                        subtitle = "High customer loyalty",
                                        icon = Icons.Default.Group,
                                        iconTint = SaveBiteAmber,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MetricSummaryCard(
                                        title = "Fulfillment Rate",
                                        value = "98.4%",
                                        subtitle = "0.2% cancellation",
                                        icon = Icons.Default.CheckCircle,
                                        iconTint = SaveBiteBadgeGreen,
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricSummaryCard(
                                        title = "Peak Hours",
                                        value = "7:30 - 8:30 PM",
                                        subtitle = "68% daily sales",
                                        icon = Icons.Default.Schedule,
                                        iconTint = SaveBiteBadgeRed,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Weekly Revenue Trend Chart Card
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Analytics, contentDescription = null, tint = SaveBiteEmerald, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Weekly Revenue Trend (₹)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        }
                                        Surface(shape = RoundedCornerShape(8.dp), color = SaveBiteEmerald.copy(alpha = 0.12f)) {
                                            Text("Weekly Breakdown", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SaveBiteEmerald, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    val dailyData = listOf(
                                        Pair("Mon", 450.0),
                                        Pair("Tue", 680.0),
                                        Pair("Wed", 890.0),
                                        Pair("Thu", 520.0),
                                        Pair("Fri", 1120.0),
                                        Pair("Sat", 1450.0),
                                        Pair("Sun", 980.0)
                                    )
                                    val maxRevenue = 1500.0

                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                    ) {
                                        val barWidth = size.width / (dailyData.size * 2)
                                        val space = size.width / dailyData.size

                                        dailyData.forEachIndexed { i, (_, rev) ->
                                            val barHeight = (rev / maxRevenue * size.height).toFloat()
                                            val x = i * space + (space - barWidth) / 2
                                            val y = size.height - barHeight

                                            val color = if (i == 5) Color(0xFFDC2626) else Color(0xFF0D7A53)

                                            drawRoundRect(
                                                color = color.copy(alpha = 0.85f),
                                                topLeft = Offset(x, y),
                                                size = Size(barWidth, barHeight),
                                                cornerRadius = CornerRadius(8f, 8f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        dailyData.forEach { (day, _) ->
                                            Text(
                                                text = day,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Environmental Impact & Waste Diversion Report Card
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Nature, contentDescription = null, tint = SaveBiteBadgeGreen, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Environmental Waste Saved Report", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "By selling surplus food instead of discarding it, your kitchen prevented ${"%.1f".format(totalCo2Saved)} kg of CO₂ greenhouse gas emissions from landfills.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = SaveBiteBadgeGreenBg,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "Verified Carbon Credit Status", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SaveBiteBadgeGreen)
                                            Surface(shape = RoundedCornerShape(6.dp), color = Color.White) {
                                                Text("ACTIVE 🌱", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SaveBiteBadgeGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 4: STORE & STAFF SETTINGS
                    4 -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Storefront, contentDescription = null, tint = SaveBiteEmerald)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Store Profile Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(text = "Shop Name: ${userMerchant.businessName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(text = "Category: ${userMerchant.businessType.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text(text = "Address: ${userMerchant.address}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text(text = "Pickup Instructions: ${userMerchant.pickupInstructions}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }
                            }
                        }

                        // Staff Roles Card
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Group, contentDescription = null, tint = SaveBiteEmerald)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Employee & Staff Roles (Future Ready)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    listOf(
                                        Pair("Kitchen Manager", "Full Dish & Inventory Control"),
                                        Pair("Counter Cashier", "Order Handover & Verification Only"),
                                        Pair("Packing Staff", "View Order Quantities")
                                    ).forEach { (role, desc) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(role, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                                Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                            }
                                            Surface(shape = RoundedCornerShape(6.dp), color = SaveBiteBadgeGreenBg) {
                                                Text("ACTIVE", style = MaterialTheme.typography.labelSmall, color = SaveBiteBadgeGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showCreateDialog) {
        CreatePackageDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, desc, cat, origPrice, discPrice, qty, window, tags, isDonation, imgUrl ->
                onCreatePackage(title, desc, cat, origPrice, discPrice, qty, window, tags, isDonation, imgUrl)
                showCreateDialog = false
            }
        )
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Merchant Partner Support") },
            text = { Text("Call 1800-SAVE-BITE or email merchant-support@savebite.in for 24/7 kitchen assistance.") },
            confirmButton = { Button(onClick = { showSupportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)) { Text("Close") } }
        )
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Store Alerts & Operational Notifications") },
            text = { Text("• 3 new customer orders reserved in last hour.\n• Restock alert: Bikaner Sweets stock running low (2 left).") },
            confirmButton = { Button(onClick = { showNotificationsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)) { Text("Acknowledge") } }
        )
    }
}

@Composable
private fun PendingOrderCard(
    order: OrderEntity,
    onHandoverToPartner: (String) -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SaveBiteEmerald.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SaveBiteEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = order.customerName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${order.quantity}x ${order.packageTitle} • ${formatRupees(order.totalPrice)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Order #${order.orderNumber}", style = MaterialTheme.typography.labelMedium, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = SaveBiteAmber)
            }

            if (order.status == OrderStatus.RESERVED) {
                Button(
                    onClick = {
                        SoundHapticsManager.playClick(context)
                        onHandoverToPartner(order.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Handover to Partner", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Surface(shape = RoundedCornerShape(8.dp), color = SaveBiteBadgeGreenBg) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaveBiteBadgeGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Out for Delivery", style = MaterialTheme.typography.labelSmall, color = SaveBiteBadgeGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    pkg: FoodPackageEntity,
    onUpdateStock: (String, Int) -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (pkg.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = pkg.imageUrl,
                        contentDescription = pkg.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pkg.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (pkg.isDonation) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFDE68A)) {
                            Text("FREE NGO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF78350F), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${formatRupees(pkg.discountedPrice)} (Was ${formatRupees(pkg.originalPrice)}) • -${pkg.discountPercent}% OFF",
                    style = MaterialTheme.typography.bodySmall,
                    color = SaveBiteEmerald,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = pkg.pickupWindow,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (pkg.quantityAvailable > 0) SaveBiteBadgeGreenBg else SaveBiteBadgeRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (pkg.quantityAvailable > 0) "${pkg.quantityAvailable} left" else "Sold Out",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (pkg.quantityAvailable > 0) SaveBiteBadgeGreen else SaveBiteBadgeRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                SoundHapticsManager.playClick(context)
                                onUpdateStock(pkg.id, (pkg.quantityAvailable - 1).coerceAtLeast(0))
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Stock", modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = SaveBiteEmerald.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                SoundHapticsManager.playClick(context)
                                onUpdateStock(pkg.id, pkg.quantityAvailable + 1)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Stock", tint = SaveBiteEmerald, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterShopCard(
    merchantName: String,
    onRegisterShop: (
        businessName: String,
        businessType: BusinessType,
        description: String,
        address: String,
        pickupInstructions: String,
        coverEmoji: String
    ) -> Unit
) {
    val context = LocalContext.current
    var shopName by remember { mutableStateOf(merchantName.ifBlank { "Sharma Ji Ki Rasoi" }) }
    var selectedType by remember { mutableStateOf(BusinessType.RESTAURANT) }
    var description by remember { mutableStateOf("Fresh daily North Indian thalis, paneer curries, and tandoori rotis.") }
    var address by remember { mutableStateOf("Shop 14, Main Market, Lajpat Nagar, New Delhi") }
    var pickupInstructions by remember { mutableStateOf("Show your 6-digit SaveBite PIN at the parcel counter.") }
    var selectedEmoji by remember { mutableStateOf("🍛") }

    val emojis = listOf("🍛", "🥐", "☕", "🪔", "🥑", "🥘", "🍕", "🍔")

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_register_shop")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SaveBiteEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = SaveBiteEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Register Your Shop / Restaurant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Set up your storefront profile to start listing surplus dishes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop / Restaurant Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Business Category",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(BusinessType.entries.toTypedArray()) { type ->
                    val isSelected = selectedType == type
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { selectedType = type }
                    ) {
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Specialties") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Store Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pickupInstructions,
                onValueChange = { pickupInstructions = it },
                label = { Text("Pickup Instructions for Customers") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select Cover Icon Emoji",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                emojis.forEach { emoji ->
                    val isSelected = selectedEmoji == emoji
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) SaveBiteEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) BorderStroke(1.5.dp, SaveBiteEmerald) else null,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { selectedEmoji = emoji }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (shopName.isNotBlank()) {
                        SoundHapticsManager.playClick(context)
                        onRegisterShop(
                            shopName,
                            selectedType,
                            description,
                            address,
                            pickupInstructions,
                            selectedEmoji
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register Shop & Start Adding Dishes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreatePackageDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        category: PackageCategory,
        originalPrice: Double,
        discountedPrice: Double,
        quantity: Int,
        pickupWindow: String,
        dietaryTags: List<String>,
        isDonation: Boolean,
        imageUrl: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("Evening Mithai & Tiffin Box") }
    var description by remember { mutableStateOf("Freshly prepared surplus delicacies from today's closing batch.") }
    var origPrice by remember { mutableStateOf("350.00") }
    var discPrice by remember { mutableStateOf("120.00") }
    var quantity by remember { mutableStateOf("5") }
    var window by remember { mutableStateOf("Today 8:00 PM - 9:30 PM") }
    var isDonation by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=800&q=80") }

    val presetImages = listOf(
        Pair("Mithai / Sweets", "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=800&q=80"),
        Pair("Biryani Pot", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80"),
        Pair("Dosa & Tiffins", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=800&q=80"),
        Pair("Curry & Naan", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&w=800&q=80"),
        Pair("Bakery / Chai", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Post New Surplus Dish",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Dish Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / What's Inside") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dish Image Picker Presets & URL
                Text(
                    text = "Dish Photo (Preset or Image URL)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presetImages) { (label, url) ->
                        val isSelected = imageUrl == url
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { imageUrl = url }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Custom Dish Image URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = origPrice,
                        onValueChange = { origPrice = it },
                        label = { Text("Original (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = if (isDonation) "0.00" else discPrice,
                        onValueChange = { discPrice = it },
                        enabled = !isDonation,
                        label = { Text("Rescue Price (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = window,
                        onValueChange = { window = it },
                        label = { Text("Pickup Window") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                }

                // NGO Charity Donation Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = isDonation,
                        onCheckedChange = { isDonation = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Donate Free to NGO Food Rescue",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SaveBiteEmerald
                        )
                        Text(
                            text = "Zero price; reserved directly for local food banks & charities.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        title,
                        description,
                        PackageCategory.MEALS,
                        origPrice.toDoubleOrNull() ?: 350.0,
                        if (isDonation) 0.0 else (discPrice.toDoubleOrNull() ?: 120.0),
                        quantity.toIntOrNull() ?: 5,
                        window,
                        listOf("Pure Veg"),
                        isDonation,
                        imageUrl
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)
            ) {
                Text("Publish Dish to Market")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
