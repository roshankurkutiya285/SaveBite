package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.OrderStatus
import com.example.data.model.UserRole
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.SoundHapticsManager
import com.example.util.formatRupees

@Composable
fun ImpactScreen(
    currentUser: UserEntity,
    customerOrders: List<OrderEntity>,
    currentColorPalette: AppColorPalette,
    currentThemeMode: AppThemeMode,
    onSelectColorPalette: (AppColorPalette) -> Unit,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    onBadgeClick: (badgeName: String, desc: String, emoji: String, isUnlocked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val completedOrders = customerOrders.filter { it.status == OrderStatus.COMPLETED }
    val totalMealsRescued = completedOrders.sumOf { it.quantity }
    val totalCo2Saved = completedOrders.sumOf { it.co2SavedKg }
    val totalMoneySaved = completedOrders.sumOf { it.totalSavings }
    val treesEquivalent = (totalCo2Saved / 21.0).coerceAtLeast(if (totalMealsRescued > 0) 0.1 else 0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("impact_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Mission Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SaveBiteEmerald,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nature,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SaveBite Impact",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Save Food. Save Money. Save the Planet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Over 1/3 of all food produced globally is thrown away. Rescuing surplus inventory directly avoids landfill methane emissions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Native Share Button
                    Button(
                        onClick = {
                            val shareText = """
                                🇮🇳 My SaveBite Indian Food Rescue Impact:
                                • ${totalMealsRescued} surplus meals rescued
                                • ${"%.1f".format(totalCo2Saved)} kg CO₂ prevented from landfill
                                • ${formatRupees(totalMoneySaved)} saved on authentic delicious food
                                Join the mission to eliminate food waste with SaveBite!
                            """.trimIndent()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share your food rescue impact"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = SaveBiteEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share My Impact", color = SaveBiteEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 7-Day Rescue Streak Tracker
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (totalMealsRescued > 0) "3-Day Food Rescue Streak!" else "Start Your Rescue Streak",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFED7AA)
                        ) {
                            Text(
                                text = if (totalMealsRescued > 0) "🔥 ACTIVE" else "READY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEachIndexed { index, day ->
                            val isCompleted = index < 3 && totalMealsRescued > 0
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompleted) Color(0xFFF97316) else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isCompleted) "✓" else "${index + 1}",
                                        color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4-Card Impact Grid
        item {
            Text(
                text = "Lifetime Food Rescue Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImpactMetricCard(
                    title = "Meals Rescued",
                    value = "$totalMealsRescued",
                    unit = "bags",
                    icon = Icons.Default.Restaurant,
                    tint = SaveBiteEmerald,
                    modifier = Modifier.weight(1f)
                )

                ImpactMetricCard(
                    title = "CO₂ Prevented",
                    value = "%.1f".format(totalCo2Saved),
                    unit = "kg",
                    icon = Icons.Default.Co2,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImpactMetricCard(
                    title = "Money Saved",
                    value = formatRupees(totalMoneySaved),
                    unit = "INR",
                    icon = Icons.Default.MonetizationOn,
                    tint = SaveBiteAmber,
                    modifier = Modifier.weight(1f)
                )

                ImpactMetricCard(
                    title = "Tree Equivalent",
                    value = "%.1f".format(treesEquivalent),
                    unit = "trees/yr",
                    icon = Icons.Default.Forest,
                    tint = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Gamified Eco Badges
        item {
            Text(
                text = "Rescue Achievements & Badges",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EcoBadgeRow(
                    badgeName = "Annadata Starter",
                    description = "Rescue your very first surplus meal from a local dhaba, bakery, or kitchen",
                    isUnlocked = totalMealsRescued >= 1,
                    progress = (totalMealsRescued / 1f).coerceIn(0f, 1f),
                    iconEmoji = "🌱",
                    onClick = {
                        onBadgeClick("Annadata Starter", "Rescue your very first surplus meal from a local dhaba, bakery, or kitchen", "🌱", totalMealsRescued >= 1)
                    }
                )

                EcoBadgeRow(
                    badgeName = "Bhojan Rakshak",
                    description = "Rescue 5 surplus meals from entering landfill waste",
                    isUnlocked = totalMealsRescued >= 5,
                    progress = (totalMealsRescued / 5f).coerceIn(0f, 1f),
                    iconEmoji = "🛡️",
                    onClick = {
                        onBadgeClick("Bhojan Rakshak", "Rescue 5 surplus meals from entering landfill waste", "🛡️", totalMealsRescued >= 5)
                    }
                )

                EcoBadgeRow(
                    badgeName = "Green India Hero",
                    description = "Prevent at least 15 kg of greenhouse emissions across Indian cities",
                    isUnlocked = totalCo2Saved >= 15.0,
                    progress = (totalCo2Saved.toFloat() / 15f).coerceIn(0f, 1f),
                    iconEmoji = "🌍",
                    onClick = {
                        onBadgeClick("Green India Hero", "Prevent at least 15 kg of greenhouse emissions across Indian cities", "🌍", totalCo2Saved >= 15.0)
                    }
                )

                EcoBadgeRow(
                    badgeName = "Desi Flavours Champion",
                    description = "Support 3 different sweet shops, south tiffins, and dhabas",
                    isUnlocked = completedOrders.map { it.merchantId }.distinct().size >= 3,
                    progress = (completedOrders.map { it.merchantId }.distinct().size / 3f).coerceIn(0f, 1f),
                    iconEmoji = "🪔",
                    onClick = {
                        onBadgeClick("Desi Flavours Champion", "Support 3 different sweet shops, south tiffins, and dhabas", "🪔", completedOrders.map { it.merchantId }.distinct().size >= 3)
                    }
                )
            }
        }

        // Active Profile & Multi-Stakeholder Switcher
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Active Session & Multi-Stakeholder Switcher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Logged in as: ${currentUser.name} (${currentUser.role.name})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SaveBiteEmerald,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Switch Persona for Demo & Testing:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSwitchRole(UserRole.CUSTOMER) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentUser.role == UserRole.CUSTOMER) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Consumer",
                                color = if (currentUser.role == UserRole.CUSTOMER) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Button(
                            onClick = { onSwitchRole(UserRole.BAKERY) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentUser.role == UserRole.BAKERY || currentUser.role == UserRole.RESTAURANT) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Merchant",
                                color = if (currentUser.role == UserRole.BAKERY || currentUser.role == UserRole.RESTAURANT) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Button(
                            onClick = { onSwitchRole(UserRole.NGO) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentUser.role == UserRole.NGO) SaveBiteEmerald else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Charity NGO",
                                color = if (currentUser.role == UserRole.NGO) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        // Theme & Appearance Customization
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Theme & Appearance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Personalize colors and display mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Display Mode",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.entries.forEach { mode ->
                            val isSelected = currentThemeMode == mode
                            Button(
                                onClick = {
                                    SoundHapticsManager.playClick(context)
                                    onSelectThemeMode(mode)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    val icon = when (mode) {
                                        AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                                        AppThemeMode.DARK -> Icons.Default.DarkMode
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Color Palette",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppColorPalette.entries.forEach { palette ->
                            val isSelected = currentColorPalette == palette
                            Card(
                                onClick = {
                                    SoundHapticsManager.playClick(context)
                                    onSelectColorPalette(palette)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Palette preview dots
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy((-4).dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(palette.primaryColor)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(palette.secondaryColor)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = palette.displayName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = palette.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
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

@Composable
fun EcoBadgeRow(
    badgeName: String,
    description: String,
    isUnlocked: Boolean,
    progress: Float,
    iconEmoji: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) SaveBiteEmerald.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isUnlocked) SaveBiteEmerald.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badgeName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (isUnlocked) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SaveBiteEmerald
                        ) {
                            Text(
                                text = "UNLOCKED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (!isUnlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SaveBiteEmerald,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
fun ImpactMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
