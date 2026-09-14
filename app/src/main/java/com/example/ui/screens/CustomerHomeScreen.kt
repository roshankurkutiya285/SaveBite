package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.model.PackageCategory
import com.example.ui.components.InteractiveSurplusMap
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteBadgeRed
import com.example.ui.theme.SaveBiteBadgeRedBg
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.IndianDietaryBadge
import com.example.util.formatRupees

enum class ExploreViewMode {
    LIST,
    MAP
}

@Composable
fun CustomerHomeScreen(
    packages: List<Pair<FoodPackageEntity, MerchantEntity>>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: PackageCategory?,
    onCategorySelect: (PackageCategory?) -> Unit,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onPackageClick: (FoodPackageEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(ExploreViewMode.LIST) }
    var selectedDietaryTag by remember { mutableStateOf<String?>(null) }

    val filteredPackages = remember(packages, selectedDietaryTag) {
        if (selectedDietaryTag == null) packages
        else packages.filter { (pkg, _) ->
            pkg.dietaryTags.any { it.contains(selectedDietaryTag!!, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_home_screen")
    ) {
        // Location & View Mode Switcher Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = SaveBiteEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Downtown • Within 3 km",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time surplus food rescue",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // View Toggle (List vs Map)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(2.dp)
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (viewMode == ExploreViewMode.LIST) SaveBiteEmerald else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewMode = ExploreViewMode.LIST }
                            .testTag("toggle_list_view")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = "List View",
                            tint = if (viewMode == ExploreViewMode.LIST) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp).size(18.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (viewMode == ExploreViewMode.MAP) SaveBiteEmerald else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewMode = ExploreViewMode.MAP }
                            .testTag("toggle_map_view")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map View",
                            tint = if (viewMode == ExploreViewMode.MAP) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp).size(18.dp)
                        )
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("home_search_input"),
            placeholder = { Text("Search biryani, sweets, tiffins, dhabas...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SaveBiteEmerald,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Dietary & Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null && selectedDietaryTag == null,
                    onClick = {
                        onCategorySelect(null)
                        selectedDietaryTag = null
                    },
                    label = { Text("All Surplus") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaveBiteEmerald,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_all")
                )
            }

            items(PackageCategory.values()) { category ->
                val emoji = when (category) {
                    PackageCategory.BAKERY -> "🥐 Bakery & Chai"
                    PackageCategory.MEALS -> "🍛 Curries & Thalis"
                    PackageCategory.SWEETS -> "🪔 Desi Mithai"
                    PackageCategory.GROCERIES -> "🛒 Desi Ration"
                    PackageCategory.PRODUCE -> "🥑 Sabzi Mandi"
                    PackageCategory.VEGAN -> "🌱 Pure Veg / Jain"
                }
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelect(category) },
                    label = { Text(emoji) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaveBiteEmerald,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Indian dietary tags
            items(listOf("Pure Veg", "Jain Friendly", "Eggless", "Halal", "Non-Veg", "Organic")) { tag ->
                FilterChip(
                    selected = selectedDietaryTag == tag,
                    onClick = {
                        selectedDietaryTag = if (selectedDietaryTag == tag) null else tag
                    },
                    label = { Text(tag) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaveBiteAmber,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // View Mode Branching: Map vs List
        if (viewMode == ExploreViewMode.MAP) {
            InteractiveSurplusMap(
                packagesWithMerchants = filteredPackages,
                onPackageClick = onPackageClick,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Header: Offers Count
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nearby Rescue Offers (${filteredPackages.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pickup today",
                            style = MaterialTheme.typography.labelMedium,
                            color = SaveBiteAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Empty state
                if (filteredPackages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🌱", fontSize = 44.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No surplus offers matching criteria",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try clearing filters to see nearby restaurants, cafes, and bakeries.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Surplus Package Cards
                    items(filteredPackages, key = { it.first.id }) { (pkg, merchant) ->
                        SurplusPackageCard(
                            pkg = pkg,
                            merchant = merchant,
                            isFavorite = favoriteIds.contains(merchant.id),
                            onToggleFavorite = { onToggleFavorite(merchant.id) },
                            onClick = { onPackageClick(pkg) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurplusPackageCard(
    pkg: FoodPackageEntity,
    merchant: MerchantEntity,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("package_card_${pkg.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val isVeg = !pkg.dietaryTags.any { it.contains("Non", ignoreCase = true) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = merchant.coverEmoji,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IndianDietaryBadge(isVeg = isVeg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = merchant.businessName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (pkg.isDonation) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFDE68A)
                            ) {
                                Text(
                                    text = "FREE NGO",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = SaveBiteAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${merchant.rating} (${merchant.reviewCount})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " • ${merchant.distanceKm} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) SaveBiteBadgeRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pkg.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = pkg.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                        style = MaterialTheme.typography.labelMedium,
                        color = SaveBiteAmber,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (pkg.quantityAvailable <= 3 && !pkg.isDonation) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SaveBiteBadgeRedBg
                    ) {
                        Text(
                            text = "Only ${pkg.quantityAvailable} left",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaveBiteBadgeRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Nature,
                        contentDescription = null,
                        tint = SaveBiteEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${pkg.co2SavedKg} kg CO₂ saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaveBiteEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (pkg.isDonation) {
                        Text(
                            text = "Free Donation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaveBiteEmerald
                        )
                    } else {
                        Text(
                            text = formatRupees(pkg.originalPrice),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textDecoration = TextDecoration.LineThrough
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = formatRupees(pkg.discountedPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaveBiteEmerald
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SaveBiteEmerald
                        ) {
                            Text(
                                text = "-${pkg.discountPercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
