package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.util.SoundHapticsManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PackageCategory
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.formatRupees

@Composable
fun MerchantDashboardScreen(
    merchantName: String,
    packages: List<FoodPackageEntity>,
    merchantOrders: List<OrderEntity>,
    onRedeemCode: (String) -> Unit,
    onCreatePackage: (
        title: String,
        description: String,
        category: PackageCategory,
        originalPrice: Double,
        discountedPrice: Double,
        quantity: Int,
        pickupWindow: String,
        dietaryTags: List<String>,
        isDonation: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var redeemInput by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val pendingOrders = merchantOrders.filter { it.status == OrderStatus.RESERVED || it.status == OrderStatus.READY_FOR_PICKUP }
    val completedOrders = merchantOrders.filter { it.status == OrderStatus.COMPLETED }
    val totalRevenue = completedOrders.sumOf { it.totalPrice }
    val totalRescued = completedOrders.sumOf { it.quantity }
    val activeBagsAvailable = packages.sumOf { it.quantityAvailable }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("merchant_dashboard_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Merchant Header
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
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = merchantName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Merchant Partner Portal • Live Inventory",
                            style = MaterialTheme.typography.bodySmall,
                            color = SaveBiteEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Fast Verification Section (Customer PIN / Scanned QR verification)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = SaveBiteEmerald
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Redeem Customer Pass",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter customer's 6-digit confirmation PIN (e.g. 849-210) to verify pickup handoff.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = redeemInput,
                            onValueChange = { redeemInput = it },
                            placeholder = { Text("e.g. 849-210") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pin_verify_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (redeemInput.isNotBlank()) {
                                    SoundHapticsManager.playClick(context)
                                    onRedeemCode(redeemInput)
                                    redeemInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("verify_pin_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Verify")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verify")
                        }
                    }
                }
            }
        }

        // Metrics Grid (Revenue, Rescued, Active bags)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricSummaryCard(
                    title = "Revenue",
                    value = formatRupees(totalRevenue),
                    icon = Icons.Default.MonetizationOn,
                    iconTint = SaveBiteEmerald,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Rescued",
                    value = "$totalRescued bags",
                    icon = Icons.Default.Nature,
                    iconTint = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Active Stock",
                    value = "$activeBagsAvailable bags",
                    icon = Icons.Default.Inventory2,
                    iconTint = SaveBiteAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Pending Customer Pickups Awaiting Collection
        if (pendingOrders.isNotEmpty()) {
            item {
                Text(
                    text = "Awaiting Customer Collection (${pendingOrders.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(pendingOrders, key = { it.id }) { order ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SaveBiteEmerald.copy(alpha = 0.3f)),
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
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = order.customerName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${order.quantity}x ${order.packageTitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "PIN: ${order.pickupPin} • ${order.orderNumber}",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SaveBiteAmber
                            )
                        }

                        Button(
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                onRedeemCode(order.pickupPin)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Handed Over", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Header: Active Surplus Bags & "Post Surplus" Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Surplus Listings (${packages.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("post_surplus_bag_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Bag", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // Listing Items
        items(packages, key = { it.id }) { pkg ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pkg.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (pkg.isDonation) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFDE68A)
                                ) {
                                    Text(
                                        text = "NGO FREE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF78350F),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${pkg.pickupWindow} • ${formatRupees(pkg.discountedPrice)} (Was ${formatRupees(pkg.originalPrice)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (pkg.quantityAvailable > 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = if (pkg.quantityAvailable > 0) "${pkg.quantityAvailable} left" else "Sold Out",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (pkg.quantityAvailable > 0) Color(0xFF065F46) else Color(0xFF991B1B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog: Create Surplus Package
    if (showCreateDialog) {
        CreatePackageDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, desc, cat, origPrice, discPrice, qty, window, tags, isDonation ->
                onCreatePackage(title, desc, cat, origPrice, discPrice, qty, window, tags, isDonation)
                showCreateDialog = false
            }
        )
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
        isDonation: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("Evening Pastry & Bread Box") }
    var description by remember { mutableStateOf("Freshly baked artisan goods from today's closing batch.") }
    var origPrice by remember { mutableStateOf("24.00") }
    var discPrice by remember { mutableStateOf("7.50") }
    var quantity by remember { mutableStateOf("5") }
    var window by remember { mutableStateOf("Today 8:00 PM - 9:00 PM") }
    var isDonation by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Post Surplus Package",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Package Title") },
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = origPrice,
                        onValueChange = { origPrice = it },
                        label = { Text("Original (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = if (isDonation) "0.00" else discPrice,
                        onValueChange = { discPrice = it },
                        enabled = !isDonation,
                        label = { Text("Rescue (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
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
                        PackageCategory.BAKERY,
                        origPrice.toDoubleOrNull() ?: 20.0,
                        if (isDonation) 0.0 else (discPrice.toDoubleOrNull() ?: 6.0),
                        quantity.toIntOrNull() ?: 5,
                        window,
                        listOf("Vegetarian"),
                        isDonation
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)
            ) {
                Text("Publish to Market")
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
