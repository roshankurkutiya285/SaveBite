package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
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
fun PickupsDashboardScreen(
    orders: List<OrderEntity>,
    onSimulateRedeem: (String) -> Unit = {},
    onCancelOrder: (String) -> Unit,
    onSubmitFeedback: (orderId: String, rating: Int, reviewText: String, reviewTags: String) -> Unit = { _, _, _, _ -> },
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var orderToCancel by remember { mutableStateOf<OrderEntity?>(null) }

    val activeOrders = orders.filter { it.status == OrderStatus.RESERVED || it.status == OrderStatus.READY_FOR_PICKUP }
    val completedOrders = orders.filter { it.status == OrderStatus.COMPLETED || it.status == OrderStatus.CANCELLED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("pickups_dashboard_screen")
    ) {
        // Pickups Operations Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SaveBiteEmerald.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = SaveBiteEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pickups & Handovers",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "${activeOrders.size} active pickup pass${if (activeOrders.size == 1) "" else "es"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (activeOrders.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaveBiteBadgeGreenBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = SaveBiteBadgeGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ready for Collection",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaveBiteBadgeGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab Selector (Active Pickups vs Completed History)
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
                text = {
                    Text(
                        "Active Pickups (${activeOrders.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("tab_active_pickups")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    SoundHapticsManager.playClick(context)
                    selectedTab = 1
                },
                text = {
                    Text(
                        "Pickup History (${completedOrders.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("tab_past_pickups")
            )
        }

        if (selectedTab == 0) {
            if (activeOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛍️", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Active Pickup Passes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Explore nearby restaurants, bakeries, and sweet shops to rescue surplus meals at up to 70% off.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                onExploreClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_explore_surplus_empty")
                        ) {
                            Text("Browse Surplus Meals")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activeOrders, key = { it.id }) { order ->
                        ActivePickupCard(
                            order = order,
                            onSimulateRedeem = onSimulateRedeem,
                            onRequestCancel = { orderToCancel = order }
                        )
                    }
                }
            }
        } else {
            // Past Orders / History
            if (completedOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📦", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Past Pickups Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Completed pickups and environmental certificates will be saved here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(completedOrders, key = { it.id }) { order ->
                        PastPickupCard(
                            order = order,
                            onSubmitFeedback = onSubmitFeedback
                        )
                    }
                }
            }
        }
    }

    // Cancellation confirmation modal
    orderToCancel?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            title = { Text("Cancel Surplus Reservation?") },
            text = {
                Text("Are you sure you want to cancel ${order.packageTitle} (${order.orderNumber})? The bag will be released back to the store for another rescuer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelOrder(order.id)
                        orderToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaveBiteBadgeRed)
                ) {
                    Text("Confirm Cancellation")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToCancel = null }) {
                    Text("Keep Order")
                }
            }
        )
    }
}

@Composable
private fun ActivePickupCard(
    order: OrderEntity,
    onSimulateRedeem: (String) -> Unit,
    onRequestCancel: () -> Unit
) {
    val context = LocalContext.current
    var showReceiptDetails by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bar: Order # & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = SaveBiteEmerald
                    )
                    Text(
                        text = order.packageTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SaveBiteBadgeGreenBg
                ) {
                    Text(
                        text = "READY FOR PICKUP",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaveBiteBadgeGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pickup Window Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SaveBiteAmber.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = SaveBiteAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pickup Window: ${order.pickupWindow}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent 6-Digit Verification PIN & QR Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COUNTER PICKUP PIN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = order.pickupPin,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Pickup PIN", order.pickupPin)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "PIN ${order.pickupPin} copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy PIN",
                                tint = SaveBiteEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = "Show this 6-digit code or QR to store cashier",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Dynamic Scannable QR Code
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.size(90.dp)
                ) {
                    DynamicQrCodeCanvas(payload = order.qrPayload, modifier = Modifier.padding(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Merchant Location & Fast Actions
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = SaveBiteEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = order.merchantName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Amount: ${formatRupees(order.totalPrice)} • Saved: ${formatRupees(order.totalSavings)}", style = MaterialTheme.typography.bodySmall, color = SaveBiteEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Share Pass Button
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "SaveBite Pickup Pass for ${order.packageTitle} at ${order.merchantName}.\nOrder: ${order.orderNumber}\nPIN: ${order.pickupPin}\nPickup Window: ${order.pickupWindow}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Pickup Pass"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = SaveBiteEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Counter Handover Instructions Banner (Restricted Customer Pass)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SaveBiteEmerald.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, SaveBiteEmerald.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SaveBiteEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Store Handover Pass",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaveBiteEmerald
                            )
                            Text(
                                text = "Present this 6-digit PIN or QR to store cashier to collect your food.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            onRequestCancel()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SaveBiteBadgeRed)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            }

            // Receipt Details Accordion Toggle
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReceiptDetails = !showReceiptDetails }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showReceiptDetails) "Hide Item Receipt" else "View Item Receipt & Eco Stats",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = if (showReceiptDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = showReceiptDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Quantity:", style = MaterialTheme.typography.bodySmall)
                        Text(text = "${order.quantity} box(es)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total Paid:", style = MaterialTheme.typography.bodySmall)
                        Text(text = formatRupees(order.totalPrice), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SaveBiteEmerald)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "You Saved:", style = MaterialTheme.typography.bodySmall)
                        Text(text = formatRupees(order.totalSavings), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "CO₂ Landfill Avoided:", style = MaterialTheme.typography.bodySmall)
                        Text(text = "${"%.1f".format(order.co2SavedKg)} kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SaveBiteEmerald)
                    }
                }
            }
        }
    }
}

@Composable
private fun PastPickupCard(
    order: OrderEntity,
    onSubmitFeedback: (orderId: String, rating: Int, reviewText: String, reviewTags: String) -> Unit
) {
    val isCancelled = order.status == OrderStatus.CANCELLED
    var showFeedbackDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isCancelled) SaveBiteBadgeRedBg else SaveBiteBadgeGreenBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCancelled) Icons.Default.Close else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isCancelled) SaveBiteBadgeRed else SaveBiteBadgeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${order.orderNumber} • ${order.packageTitle}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${order.merchantName} • ${if (isCancelled) "Cancelled" else "Collected"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = if (isCancelled) "₹0 refunded" else formatRupees(order.totalPrice),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCancelled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else SaveBiteEmerald
                )
            }

            if (!isCancelled) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(10.dp))

                if (order.rating != null) {
                    // Display existing feedback
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SaveBiteAmber.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < order.rating) SaveBiteAmber else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${order.rating}/5 Rated",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SaveBiteAmber
                                )
                            }
                            if (!order.reviewTags.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tags: ${order.reviewTags}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaveBiteEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (!order.reviewText.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"${order.reviewText}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                } else {
                    // Button to open Feedback dialog
                    OutlinedButton(
                        onClick = { showFeedbackDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SaveBiteEmerald)
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SaveBiteAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rate & Review Food Quality")
                    }
                }
            }
        }
    }

    if (showFeedbackDialog) {
        OrderFeedbackDialog(
            order = order,
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { rating, review, tags ->
                onSubmitFeedback(order.id, rating, review, tags)
                showFeedbackDialog = false
            }
        )
    }
}

@Composable
private fun OrderFeedbackDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, review: String, tags: String) -> Unit
) {
    val context = LocalContext.current
    var selectedRating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf("Fresh & Tasty", "Generous Portion")) }

    val availableTags = listOf("Fresh & Tasty", "Generous Portion", "Hygienic Packaging", "Polite Staff", "Great Value", "Eco Package")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = SaveBiteAmber, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rate ${order.packageTitle}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "How was your meal from ${order.merchantName}?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = {
                                SoundHapticsManager.playClick(context)
                                selectedRating = star
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star stars",
                                tint = if (star <= selectedRating) SaveBiteAmber else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Tag Chips
                Text(
                    text = "Select Complement Tags:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableTags.chunked(2).forEach { rowTags ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clickable {
                                            selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                        }
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Review Comment Input
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Write a quick review for store & food quality...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tagString = selectedTags.joinToString(", ")
                    onSubmit(selectedRating, reviewText, tagString)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)
            ) {
                Text("Submit Review")
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
private fun DynamicQrCodeCanvas(payload: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gridSize = 13
        val cellSize = size.width / gridSize
        val hash = payload.hashCode()

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                // Keep 3 corner position markers solid
                val isCornerMarker =
                    (row < 3 && col < 3) ||
                    (row < 3 && col >= gridSize - 3) ||
                    (row >= gridSize - 3 && col < 3)

                val isFilled = if (isCornerMarker) {
                    true
                } else {
                    ((hash xor (row * 31 + col * 17)) % 3) == 0
                }

                if (isFilled) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize * 0.92f, cellSize * 0.92f)
                    )
                }
            }
        }
    }
}
