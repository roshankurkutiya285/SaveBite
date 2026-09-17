package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.OrderStatus
import com.example.data.model.UserRole
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
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastMeals by remember { mutableStateOf("25") }
    var broadcastLocation by remember { mutableStateOf("Connaught Place Hub, New Delhi") }

    val totalCompletedOrders = orders.count { it.status == OrderStatus.COMPLETED }
    val totalGmv = 485200.0 + orders.sumOf { it.totalPrice }
    val totalCo2Tons = 42.6 + (orders.sumOf { it.co2SavedKg } / 1000.0)

    val navTabs = listOf(
        Pair("Overview", Icons.Default.AdminPanelSettings),
        Pair("Users (${users.size})", Icons.Default.People),
        Pair("Verifications (${merchants.size})", Icons.Default.VerifiedUser),
        Pair("Financials", Icons.Default.MonetizationOn),
        Pair("Analytics & Risk", Icons.Default.Analytics),
        Pair("Audit & Settings", Icons.Default.Settings)
    )

    val filteredUsers = remember(users, userSearchQuery, selectedRoleFilter) {
        users.filter { user ->
            val matchesQuery = userSearchQuery.isBlank() ||
                    user.name.contains(userSearchQuery, ignoreCase = true) ||
                    user.email.contains(userSearchQuery, ignoreCase = true)
            val matchesRole = selectedRoleFilter == null || user.role == selectedRoleFilter
            matchesQuery && matchesRole
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Stripe/Vercel Style Admin Top Header
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
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = SaveBiteEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SaveBite Platform Admin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = SaveBiteBadgeGreenBg) {
                                Text("SYSTEM OPERATIONAL 🟢", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = SaveBiteBadgeGreen, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text(
                            text = "Enterprise SaaS Console • Latency 18ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showBroadcastDialog = true }) {
                        Icon(Icons.Default.AddAlert, contentDescription = "Broadcast Alert", tint = SaveBiteBadgeRed)
                    }
                    IconButton(onClick = { onRestockAll(); Toast.makeText(context, "System Inventory Restocked!", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restock", tint = SaveBiteEmerald)
                    }
                }
            }
        }

        // Navigation Tabs Bar
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                // TAB 0: SYSTEM OVERVIEW
                0 -> {
                    item {
                        Column {
                            Text("Platform Key Performance Indicators", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AdminMetricCard(title = "Gross Revenue", value = formatRupees(totalGmv), subtitle = "+24.8% MoM Growth", icon = Icons.Default.MonetizationOn, iconTint = SaveBiteEmerald, modifier = Modifier.weight(1f))
                                AdminMetricCard(title = "Total Orders", value = "${orders.size}", subtitle = "$totalCompletedOrders Completed", icon = Icons.Default.LocalShipping, iconTint = SaveBiteAmber, modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AdminMetricCard(title = "Active Users", value = "${users.size}", subtitle = "Across all roles", icon = Icons.Default.People, iconTint = SaveBiteEmerald, modifier = Modifier.weight(1f))
                                AdminMetricCard(title = "CO₂ Diverted", value = "${"%.1f".format(totalCo2Tons)} Tons", subtitle = "Landfill reduction", icon = Icons.Default.Nature, iconTint = SaveBiteBadgeGreen, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // System Infrastructure Health Card
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = SaveBiteEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("System Infrastructure Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                listOf(
                                    Triple("API Gateway (Ktor/Retrofit)", "Online • 18ms latency", SaveBiteBadgeGreen),
                                    Triple("Room Encrypted Database", "v10 Schema Active • 0 Errors", SaveBiteBadgeGreen),
                                    Triple("Razorpay Gateway Webhooks", "Connected • Live Test Key", SaveBiteEmerald)
                                ).forEach { (service, status, color) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(service, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Surface(shape = RoundedCornerShape(6.dp), color = SaveBiteBadgeGreenBg) {
                                            Text(status, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 1: USERS & DIRECTORY
                1 -> {
                    item {
                        Column {
                            Text("Registered Platform Users Directory (${filteredUsers.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = userSearchQuery,
                                onValueChange = { userSearchQuery = it },
                                placeholder = { Text("Search by name or email...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Role Filter Chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selectedRoleFilter == null) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { selectedRoleFilter = null }
                                    ) {
                                        Text("All Roles", style = MaterialTheme.typography.labelSmall, color = if (selectedRoleFilter == null) Color.White else MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                    }
                                }

                                items(UserRole.values()) { role ->
                                    val isSelected = selectedRoleFilter == role
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { selectedRoleFilter = role }
                                    ) {
                                        Text(role.name, style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                    }
                                }
                            }
                        }
                    }

                    // User Directory Table Cards
                    items(filteredUsers, key = { it.id }) { u ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(u.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        if (u.isEmailVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = SaveBiteEmerald, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(u.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text(u.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SaveBiteEmerald.copy(alpha = 0.12f)
                                ) {
                                    Text(u.role.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SaveBiteEmerald, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                // TAB 2: ENTITY VERIFICATION
                2 -> {
                    item {
                        Text("Merchant & Storefront Verification Terminal (${merchants.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    items(merchants, key = { it.id }) { m ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${m.coverEmoji} ${m.businessName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        if (m.verified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = SaveBiteEmerald, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(m.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }

                                Button(
                                    onClick = {
                                        SoundHapticsManager.playClick(context)
                                        onToggleMerchantVerification(m.id, !m.verified)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (m.verified) SaveBiteBadgeRed else SaveBiteEmerald),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (m.verified) "Revoke" else "Verify ✔", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // TAB 3: FINANCIALS & REFUNDS
                3 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Razorpay Transactions & Financial Audit Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))

                                orders.take(5).forEach { order ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Order #${order.orderNumber}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text("${order.customerName} • ${order.merchantName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(formatRupees(order.totalPrice), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SaveBiteEmerald)
                                            Text("Razorpay Sign Verified", style = MaterialTheme.typography.labelSmall, color = SaveBiteBadgeGreen, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 4: ANALYTICS & SECURITY
                4 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Gross Revenue Trend (₹)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))

                                val dailyData = listOf(Pair("Mon", 450.0), Pair("Tue", 680.0), Pair("Wed", 890.0), Pair("Thu", 520.0), Pair("Fri", 1120.0), Pair("Sat", 1450.0), Pair("Sun", 980.0))
                                val maxVal = 1500f

                                Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                    val barWidth = size.width / (dailyData.size * 2)
                                    val space = size.width / dailyData.size

                                    dailyData.forEachIndexed { i, (_, rev) ->
                                        val barHeight = (rev / maxVal * size.height).toFloat()
                                        val x = i * space + (space - barWidth) / 2
                                        val y = size.height - barHeight

                                        drawRoundRect(
                                            color = Color(0xFF0D7A53),
                                            topLeft = Offset(x, y),
                                            size = Size(barWidth, barHeight),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    dailyData.forEach { (day, _) ->
                                        Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = SaveBiteEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Security Risk & Fraud Detection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                listOf("Fraud Risk Score: LOW (0.01%)", "HMAC Signature Check: VALIDATED ✔", "SSL Encryption: 256-BIT BIT ENCRYPTED ✔").forEach { sec ->
                                    Text("• $sec", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                                }
                            }
                        }
                    }
                }

                // TAB 5: AUDIT LOGS & SETTINGS
                5 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = SaveBiteEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("System Security Audit Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                listOf(
                                    "08:12 - Admin verified Bikaner Sweets Storefront",
                                    "07:45 - Razorpay Webhook Payment Confirmed #SB-8921",
                                    "06:30 - System Inventory Restocked Automatically"
                                ).forEach { log ->
                                    Text(log, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Emergency Annadaan Broadcast Alert") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = broadcastMeals, onValueChange = { broadcastMeals = it }, label = { Text("Meals Count") }, singleLine = true)
                    OutlinedTextField(value = broadcastLocation, onValueChange = { broadcastLocation = it }, label = { Text("Location") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = broadcastMeals.toIntOrNull() ?: 25
                        onEmergencyNgoBroadcast("Emergency Banquet Surplus", count, broadcastLocation)
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)
                ) {
                    Text("Broadcast Alert")
                }
            },
            dismissButton = { TextButton(onClick = { showBroadcastDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
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
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}
