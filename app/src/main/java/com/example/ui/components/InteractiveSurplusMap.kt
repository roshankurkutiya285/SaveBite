package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.IndianDietaryBadge
import com.example.util.formatRupees
import kotlin.math.sqrt

@Composable
fun InteractiveSurplusMap(
    packagesWithMerchants: List<Pair<FoodPackageEntity, MerchantEntity>>,
    onPackageClick: (FoodPackageEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Distinct merchants with their cheapest surplus package
    val merchantEntries = remember(packagesWithMerchants) {
        packagesWithMerchants
            .groupBy { it.second.id }
            .values
            .map { list ->
                val merchant = list.first().second
                val cheapestPkg = list.minByOrNull { it.first.discountedPrice }?.first ?: list.first().first
                Pair(merchant, cheapestPkg)
            }
    }

    var selectedEntry by remember { mutableStateOf<Pair<MerchantEntity, FoodPackageEntity>?>(merchantEntries.firstOrNull()) }
    var selectedRadiusKm by remember { mutableStateOf(3.0) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("interactive_surplus_map")
    ) {
        val width = maxWidth
        val height = maxHeight

        // Relative coordinates for merchants mapped across viewport
        val pinOffsets = remember(merchantEntries, width, height) {
            val offsets = mutableMapOf<String, Offset>()
            val baseOffsets = listOf(
                Offset(0.35f, 0.42f),
                Offset(0.68f, 0.30f),
                Offset(0.72f, 0.65f),
                Offset(0.28f, 0.75f),
                Offset(0.50f, 0.82f)
            )
            merchantEntries.forEachIndexed { idx, entry ->
                val rel = baseOffsets.getOrElse(idx) { Offset(0.4f + (idx * 0.1f), 0.5f) }
                offsets[entry.first.id] = rel
            }
            offsets
        }

        val mapEmerald = SaveBiteEmerald

        // Map Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(merchantEntries) {
                    detectTapGestures { tapOffset ->
                        // Find closest merchant pin
                        val clicked = merchantEntries.minByOrNull { entry ->
                            val rel = pinOffsets[entry.first.id] ?: Offset(0.5f, 0.5f)
                            val pinPos = Offset(rel.x * size.width, rel.y * size.height)
                            val dx = tapOffset.x - pinPos.x
                            val dy = tapOffset.y - pinPos.y
                            sqrt(dx * dx + dy * dy)
                        }
                        if (clicked != null) {
                            val rel = pinOffsets[clicked.first.id] ?: Offset(0.5f, 0.5f)
                            val pinPos = Offset(rel.x * size.width, rel.y * size.height)
                            val dx = tapOffset.x - pinPos.x
                            val dy = tapOffset.y - pinPos.y
                            if (sqrt(dx * dx + dy * dy) < 140f) {
                                selectedEntry = clicked
                            }
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // Base map background (soft urban gray/canvas)
            drawRect(Color(0xFFEFF3F1))

            // Park areas (green zones)
            val parkPath = Path().apply {
                moveTo(canvasW * 0.05f, canvasH * 0.1f)
                lineTo(canvasW * 0.45f, canvasH * 0.15f)
                lineTo(canvasW * 0.38f, canvasH * 0.32f)
                lineTo(canvasW * 0.08f, canvasH * 0.28f)
                close()
            }
            drawPath(parkPath, Color(0xFFD8EADF))

            // River / Waterfront curve
            val riverPath = Path().apply {
                moveTo(canvasW * 0.85f, 0f)
                cubicTo(
                    canvasW * 0.88f, canvasH * 0.35f,
                    canvasW * 0.80f, canvasH * 0.65f,
                    canvasW * 0.95f, canvasH
                )
                lineTo(canvasW, canvasH)
                lineTo(canvasW, 0f)
                close()
            }
            drawPath(riverPath, Color(0xFFD6E4EE))

            // Main Avenues & Streets
            val roadColor = Color.White
            val roadOutline = Color(0xFFD1DCD6)

            // Primary avenues
            val avenues = listOf(
                Pair(Offset(0f, canvasH * 0.25f), Offset(canvasW, canvasH * 0.25f)),
                Pair(Offset(0f, canvasH * 0.52f), Offset(canvasW, canvasH * 0.52f)),
                Pair(Offset(0f, canvasH * 0.78f), Offset(canvasW, canvasH * 0.78f)),
                Pair(Offset(canvasW * 0.22f, 0f), Offset(canvasW * 0.22f, canvasH)),
                Pair(Offset(canvasW * 0.55f, 0f), Offset(canvasW * 0.55f, canvasH)),
                Pair(Offset(canvasW * 0.82f, 0f), Offset(canvasW * 0.82f, canvasH))
            )

            avenues.forEach { (start, end) ->
                drawLine(roadOutline, start, end, strokeWidth = 24f, cap = StrokeCap.Round)
                drawLine(roadColor, start, end, strokeWidth = 20f, cap = StrokeCap.Round)
            }

            // User GPS Location (Center)
            val userCenter = Offset(canvasW * 0.50f, canvasH * 0.52f)

            // Radius circle
            val radiusPx = when (selectedRadiusKm) {
                1.0 -> canvasW * 0.25f
                3.0 -> canvasW * 0.42f
                else -> canvasW * 0.60f
            }
            drawCircle(
                color = mapEmerald.copy(alpha = 0.08f),
                radius = radiusPx,
                center = userCenter
            )
            drawCircle(
                color = mapEmerald.copy(alpha = 0.35f),
                radius = radiusPx,
                center = userCenter,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )

            // User pulse beacon
            drawCircle(
                color = mapEmerald.copy(alpha = 0.25f),
                radius = 28f,
                center = userCenter
            )
            drawCircle(
                color = Color.White,
                radius = 16f,
                center = userCenter
            )
            drawCircle(
                color = mapEmerald,
                radius = 10f,
                center = userCenter
            )
        }

        // Overlay Interactive Merchant Pins
        merchantEntries.forEach { entry ->
            val merchant = entry.first
            val pkg = entry.second
            val isSelected = selectedEntry?.first?.id == merchant.id
            val rel = pinOffsets[merchant.id] ?: Offset(0.5f, 0.5f)

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = (width * rel.x) - 42.dp,
                        top = (height * rel.y) - 48.dp
                    )
                    .clickable { selectedEntry = entry }
                    .testTag("map_pin_${merchant.id}")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) 8.dp else 3.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) SaveBiteAmber else Color(0xFFD1DCD6)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = merchant.coverEmoji,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatRupees(pkg.discountedPrice),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else SaveBiteEmerald
                            )
                        }
                    }

                    // Pin pointer triangle
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SaveBiteEmerald else SaveBiteAmber)
                    )
                }
            }
        }

        // Top Radius Selector Controls
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Radius:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 8.dp)
                )

                listOf(1.0, 3.0, 5.0).forEach { km ->
                    val isSelected = selectedRadiusKm == km
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SaveBiteEmerald else Color.Transparent,
                        modifier = Modifier
                            .clickable { selectedRadiusKm = km }
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "${km.toInt()} km",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom Selected Store Card Preview
        selectedEntry?.let { entry ->
            val merchant = entry.first
            val pkg = entry.second

            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { it } + fadeIn(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 28.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = merchant.coverEmoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    val isVeg = !pkg.dietaryTags.any { it.contains("Non", ignoreCase = true) }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IndianDietaryBadge(isVeg = isVeg)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = merchant.businessName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = SaveBiteAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${merchant.rating} (${merchant.reviewCount}) • ${merchant.distanceKm} km away",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { selectedEntry = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss Preview",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = pkg.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = SaveBiteAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = pkg.pickupWindow,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaveBiteAmber
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatRupees(pkg.discountedPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SaveBiteEmerald
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(-${pkg.discountPercent}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaveBiteEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onPackageClick(pkg) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("map_reserve_button")
                        ) {
                            Text("View & Reserve Surprise Bag")
                        }
                    }
                }
            }
        }
    }
}
