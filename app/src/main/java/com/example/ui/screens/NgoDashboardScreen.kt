package com.example.ui.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.FoodPackageEntity
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteBadgeGreen
import com.example.ui.theme.SaveBiteBadgeGreenBg
import com.example.ui.theme.SaveBiteBadgeRed
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.SoundHapticsManager
import com.example.util.formatRupees

data class EmergencyAlertItem(
    val id: String,
    val title: String,
    val venue: String,
    val mealsCount: Int,
    val preparedAt: String,
    val pickupDeadline: String,
    val contactPerson: String,
    val contactPhone: String,
    val claimed: Boolean = false
)

@Composable
fun NgoDashboardScreen(
    ngoName: String,
    donationPackages: List<FoodPackageEntity>,
    onClaimDonation: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSupportModal by remember { mutableStateOf(false) }
    var showNotificationsModal by remember { mutableStateOf(false) }

    var emergencyAlerts by remember {
        mutableStateOf(
            listOf(
                EmergencyAlertItem(
                    id = "alert_1",
                    title = "Wedding Banquet Surplus (Dal Makhani, Pulao & Roti)",
                    venue = "Grand Emerald Banquet Hall, Connaught Place",
                    mealsCount = 140,
                    preparedAt = "Today, 19:30",
                    pickupDeadline = "Tonight by 23:30",
                    contactPerson = "Sukhwinder Singh (Catering Manager)",
                    contactPhone = "+91 98100 88990"
                ),
                EmergencyAlertItem(
                    id = "alert_2",
                    title = "Corporate Gala Dinner Surplus (Mixed Veg & Breads)",
                    venue = "The Imperial Hotel, Janpath",
                    mealsCount = 85,
                    preparedAt = "Today, 20:15",
                    pickupDeadline = "Tonight by 23:45",
                    contactPerson = "Kunal Rawat",
                    contactPhone = "+91 98450 77661"
                )
            )
        )
    }

    val navTabs = listOf(
        Pair("Overview", Icons.Default.VolunteerActivism),
        Pair("Available Meals (${donationPackages.size})", Icons.Default.Restaurant),
        Pair("Distribution", Icons.Default.LocalShipping),
        Pair("Impact Analytics", Icons.Default.Analytics),
        Pair("Partners & 80G", Icons.Default.Description)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ngo_dashboard_screen")
    ) {
        // Top Header Bar with Verified 80G Badge & Actions
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
                            .background(Color(0xFF831843)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ngoName.ifBlank { "Robin Hood Army - Delhi Chapter" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "80G Certified",
                                tint = SaveBiteEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "✔ Registered NGO • 80G Tax Exemption Certified",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaveBiteEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showNotificationsModal = true }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { showSupportModal = true }) {
                        Icon(Icons.Default.Help, contentDescription = "Support", tint = SaveBiteEmerald)
                    }
                }
            }
        }

        // Multi-Tab Navigation Bar
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
                // TAB 0: OVERVIEW
                0 -> {
                    // Mission Impact Hero Card
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF831843)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                                        Text("🤝", fontSize = 22.sp, modifier = Modifier.padding(6.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("100,000 Zero Hunger Meals Mission", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Direct food rescue for homeless shelters & local orphanages", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("42,850 Meals Rescued", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("42.8% Completed", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { 0.428f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFFFDE68A),
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }

                    // Key Metrics Grid
                    item {
                        Column {
                            Text(text = "Today's Rescue Impact", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                NgoMetricCard(title = "People Fed", value = "1,240", subtitle = "Today in Delhi", icon = Icons.Default.People, iconTint = SaveBiteEmerald, modifier = Modifier.weight(1f))
                                NgoMetricCard(title = "Meals Claimed", value = "${donationPackages.size}", subtitle = "Active donations", icon = Icons.Default.Restaurant, iconTint = SaveBiteAmber, modifier = Modifier.weight(1f))
                                NgoMetricCard(title = "Shelters Served", value = "14", subtitle = "Local distribution", icon = Icons.Default.LocationOn, iconTint = SaveBiteBadgeGreen, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Emergency Annadaan Banquet Surplus Alerts
                    item {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SaveBiteBadgeRed)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Emergency Banquet Annadaan Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = SaveBiteBadgeRed.copy(alpha = 0.12f)) {
                                    Text("LIVE BROADCAST", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SaveBiteBadgeRed, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            emergencyAlerts.forEach { alert ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, SaveBiteBadgeRed.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(alert.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                            Surface(shape = RoundedCornerShape(6.dp), color = SaveBiteBadgeGreenBg) {
                                                Text("${alert.mealsCount} MEALS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SaveBiteBadgeGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("📍 Venue: ${alert.venue}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                                        Text("⏰ Pickup Deadline: ${alert.pickupDeadline}", style = MaterialTheme.typography.bodySmall, color = SaveBiteAmber, fontWeight = FontWeight.Bold)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = {
                                                SoundHapticsManager.playClick(context)
                                                emergencyAlerts = emergencyAlerts.map { if (it.id == alert.id) it.copy(claimed = true) else it }
                                                Toast.makeText(context, "Claimed ${alert.mealsCount} meals! Dispatching volunteer team.", Toast.LENGTH_LONG).show()
                                            },
                                            enabled = !alert.claimed,
                                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (alert.claimed) "Claimed & Volunteer Dispatched ✔" else "Accept ${alert.mealsCount} Meals & Dispatch Team")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 1: AVAILABLE DONATED MEALS
                1 -> {
                    item {
                        Text("Available Free Meals from Partner Restaurants (${donationPackages.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    if (donationPackages.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🍲", fontSize = 44.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("No New Donated Meals Pending", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        items(donationPackages, key = { it.id }) { pkg ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                        if (pkg.imageUrl.isNotBlank()) {
                                            AsyncImage(model = pkg.imageUrl, contentDescription = pkg.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                        } else {
                                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.align(Alignment.Center))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pkg.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("${pkg.pickupWindow} • ${pkg.quantityAvailable} units available", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = {
                                                SoundHapticsManager.playClick(context)
                                                onClaimDonation(pkg.id)
                                                Toast.makeText(context, "Claimed ${pkg.title}! Reserved for NGO collection.", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Accept & Dispatch Team", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: PICKUPS & SHELTER DISTRIBUTION
                2 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = SaveBiteEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Active NGO Food Dispatch Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                listOf(
                                    Triple("1. Donation Accepted", "Bikaner Sweets - 140 Meals Reserved", SaveBiteBadgeGreen),
                                    Triple("2. Volunteer Dispatched", "Aarav Sharma en route with EV Van", SaveBiteEmerald),
                                    Triple("3. Kitchen Collection", "Food verified & temp checked", SaveBiteAmber),
                                    Triple("4. Shelter Distribution", "Aashray Homeless Shelter, Old Delhi", Color.Gray)
                                ).forEach { (step, desc, color) ->
                                    Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(step, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: IMPACT ANALYTICS
                3 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Weekly Meals Distributed Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))

                                val dailyData = listOf(Pair("Mon", 850), Pair("Tue", 1100), Pair("Wed", 1450), Pair("Thu", 920), Pair("Fri", 1680), Pair("Sat", 2100), Pair("Sun", 1850))
                                val maxVal = 2500f

                                Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                    val barWidth = size.width / (dailyData.size * 2)
                                    val space = size.width / dailyData.size

                                    dailyData.forEachIndexed { i, (_, count) ->
                                        val barHeight = (count / maxVal * size.height)
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
                }

                // TAB 4: PARTNERS, 80G DOCS & VOLUNTEERS
                4 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = SaveBiteEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("80G Tax Exemption Certificates & Compliance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                listOf("Income Tax Dept 80G Registration • Valid ✔", "FSSAI Food Safety Compliance • Valid ✔", "Robin Hood Army Registration #RHA-DEL-8921 ✔").forEach { doc ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = SaveBiteEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(doc, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSupportModal) {
        AlertDialog(
            onDismissRequest = { showSupportModal = false },
            title = { Text("24/7 NGO Emergency Helpline") },
            text = { Text("Call 1800-ROBIN-HOOD for emergency banquet food pickup teams or logistics support.") },
            confirmButton = { Button(onClick = { showSupportModal = false }, colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)) { Text("Close") } }
        )
    }

    if (showNotificationsModal) {
        AlertDialog(
            onDismissRequest = { showNotificationsModal = false },
            title = { Text("NGO Operational Alerts") },
            text = { Text("• Emergency alert: 140 banquet meals available at Connaught Place.\n• Bikaner Sweets donated 5 surplus sweet boxes.") },
            confirmButton = { Button(onClick = { showNotificationsModal = false }, colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)) { Text("Acknowledge") } }
        )
    }
}

@Composable
private fun NgoMetricCard(
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
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}
