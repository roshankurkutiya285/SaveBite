package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.model.PackageCategory
import com.example.ui.components.InteractiveSurplusMap
import com.example.ui.components.SurplusPackageCardSkeleton
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteBadgeRed
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
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var maxBudgetFilter by remember { mutableStateOf<Double?>(null) }
    var isVegModeOn by remember { mutableStateOf(false) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }

    val filteredPackages = remember(packages, selectedDietaryTag, showOnlyFavorites, favoriteIds, maxBudgetFilter, isVegModeOn, selectedCategoryName) {
        var list = packages
        if (isVegModeOn) {
            list = list.filter { (pkg, _) ->
                !pkg.dietaryTags.any { tag -> tag.contains("Non", ignoreCase = true) }
            }
        }
        if (showOnlyFavorites) {
            list = list.filter { favoriteIds.contains(it.second.id) }
        }
        if (maxBudgetFilter != null) {
            list = list.filter { it.first.discountedPrice <= maxBudgetFilter!! }
        }
        if (selectedCategoryName != null && selectedCategoryName != "All") {
            list = list.filter { (pkg, _) ->
                pkg.title.contains(selectedCategoryName!!, ignoreCase = true) ||
                pkg.category.name.contains(selectedCategoryName!!, ignoreCase = true) ||
                pkg.description.contains(selectedCategoryName!!, ignoreCase = true)
            }
        }
        if (selectedDietaryTag != null) {
            list = list.filter { (pkg, _) ->
                pkg.dietaryTags.any { it.contains(selectedDietaryTag!!, ignoreCase = true) }
            }
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_home_screen")
    ) {
        // Top Section Container with Soft Warm Swiggy/Zomato Tint
        Surface(
            color = Color(0xFFFFF3EE),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                // Location & View Switcher Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SaveBiteBadgeRed.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = SaveBiteBadgeRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Connaught Place",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Location",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Central Delhi, DL • Within 3.0 km",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // View Switcher (List vs Map)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (viewMode == ExploreViewMode.LIST) SaveBiteEmerald else Color.Transparent,
                                modifier = Modifier
                                    .clickable { viewMode = ExploreViewMode.LIST }
                                    .testTag("toggle_list_view")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatListBulleted,
                                    contentDescription = "List View",
                                    tint = if (viewMode == ExploreViewMode.LIST) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).size(16.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (viewMode == ExploreViewMode.MAP) SaveBiteEmerald else Color.Transparent,
                                modifier = Modifier
                                    .clickable { viewMode = ExploreViewMode.MAP }
                                    .testTag("toggle_map_view")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Map View",
                                    tint = if (viewMode == ExploreViewMode.MAP) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Top Search Bar + VEG MODE Toggle Row (Exact Screenshot Style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // White Search Box (Left)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = SaveBiteBadgeRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchChange,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search \"light meals\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = SaveBiteBadgeRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // VEG MODE Toggle Switch (Right - Screenshot Style)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "VEG",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isVegModeOn) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "MODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isVegModeOn) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Switch(
                            checked = isVegModeOn,
                            onCheckedChange = { isVegModeOn = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF16A34A),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
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
                // 1. ENJOY WELCOME BENEFITS Hero Banner Card (Exact Screenshot Style)
                item {
                    WelcomeBenefitsBannerCard(
                        onApplyWelcomeBenefit = {
                            maxBudgetFilter = 200.0
                        }
                    )
                }

                // 2. Food Category Food Plate Row with Active Red Underline (Screenshot Style)
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val categories = listOf(
                            Pair("All", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=200&q=80"),
                            Pair("Sandwich", "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=200&q=80"),
                            Pair("Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=200&q=80"),
                            Pair("Fries", "https://images.unsplash.com/photo-1576107232684-1279f390859f?auto=format&fit=crop&w=200&q=80"),
                            Pair("Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=200&q=80"),
                            Pair("Curries", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&w=200&q=80"),
                            Pair("Biryani", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=200&q=80"),
                            Pair("Mithai", "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=200&q=80")
                        )

                        items(categories) { (name, imgUrl) ->
                            val isSelected = (selectedCategoryName == name) || (selectedCategoryName == null && name == "All")
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedCategoryName = if (name == "All") null else name
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = imgUrl,
                                        contentDescription = name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // Active Red Underline Indicator
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(2.5.dp)
                                        .background(if (isSelected) SaveBiteBadgeRed else Color.Transparent)
                                )
                            }
                        }
                    }
                }

                // 3. Swiggy Pill Filters Row (Filters ▼, Under ₹200, Schedule ▼, Pure Veg)
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, Color.LightGray),
                                modifier = Modifier.clickable {
                                    showOnlyFavorites = false
                                    maxBudgetFilter = null
                                    isVegModeOn = false
                                    selectedCategoryName = null
                                    onCategorySelect(null)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = "Filters", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Filters", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (maxBudgetFilter != null) SaveBiteEmerald else Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, if (maxBudgetFilter != null) SaveBiteEmerald else Color.LightGray),
                                modifier = Modifier.clickable {
                                    maxBudgetFilter = if (maxBudgetFilter == null) 200.0 else null
                                }
                            ) {
                                Text(
                                    text = "Under ₹200",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (maxBudgetFilter != null) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, Color.LightGray)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Schedule", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isVegModeOn) Color(0xFF16A34A) else Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, if (isVegModeOn) Color(0xFF16A34A) else Color.LightGray),
                                modifier = Modifier.clickable {
                                    isVegModeOn = !isVegModeOn
                                }
                            ) {
                                Text(
                                    text = "Pure Veg",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isVegModeOn) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (showOnlyFavorites) SaveBiteBadgeRed else Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, if (showOnlyFavorites) SaveBiteBadgeRed else Color.LightGray),
                                modifier = Modifier.clickable {
                                    showOnlyFavorites = !showOnlyFavorites
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Liked",
                                        tint = if (showOnlyFavorites) Color.White else SaveBiteBadgeRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Liked (${favoriteIds.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (showOnlyFavorites) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Food Donation Advertisement Banner
                item {
                    FoodDonationBannerCard(
                        onExploreDonations = {
                            showOnlyFavorites = false
                            isVegModeOn = true
                            onCategorySelect(PackageCategory.VEGAN)
                        }
                    )
                }

                // Section Header: RECOMMENDED FOR YOU (Exact Screenshot Style)
                item {
                    Text(
                        text = "RECOMMENDED FOR YOU",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Empty state or Skeleton Loading state
                if (filteredPackages.isEmpty()) {
                    if (packages.isEmpty() && searchQuery.isBlank() && selectedCategory == null) {
                        items(3) {
                            SurplusPackageCardSkeleton()
                        }
                    } else {
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
                                        textAlign = TextAlign.Center
                                    )
                                }
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
fun WelcomeBenefitsBannerCard(
    onApplyWelcomeBenefit: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onApplyWelcomeBenefit)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ENJOY WELCOME BENEFITS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = SaveBiteBadgeRed,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "on first 3 orders • Tap to apply ₹100 OFF",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Gift Step Boxes Row (Exact Screenshot Style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gift 1 (Active/Unlocked)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, SaveBiteEmerald),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SaveBiteEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎁", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹100 OFF",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "+ Up to 10% Cashback\nwith FREE delivery",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 11.sp
                        )
                    }
                }

                // Gift 2 (Locked)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "50% OFF",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Unlocks on\nOrder #2",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Gift 3 (Locked)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "FREE Meal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Unlocks on\nOrder #3",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Carousel dots
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val isVeg = !pkg.dietaryTags.any { it.contains("Non", ignoreCase = true) }

            // Food Image Header with Overlay Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                if (pkg.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = pkg.imageUrl,
                        contentDescription = pkg.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = merchant.coverEmoji, fontSize = 48.sp)
                    }
                }

                // Gradient Overlay for Text Readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )

                // Top-Left Badge: Discount Pill or Free NGO Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    if (pkg.isDonation) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFDE68A)
                        ) {
                            Text(
                                text = "FREE NGO MEAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF78350F),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaveBiteEmerald
                        ) {
                            Text(
                                text = "-${pkg.discountPercent}% OFF",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Top-Right Badge: Favorite Heart Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) SaveBiteBadgeRed else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom-Left Overlay: Urgency Indicator
                if (pkg.quantityAvailable <= 3 && !pkg.isDonation) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaveBiteBadgeRed
                        ) {
                            Text(
                                text = "🔥 Only ${pkg.quantityAvailable} left",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Card Body Details
            Column(modifier = Modifier.padding(14.dp)) {
                // Merchant Store Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = merchant.coverEmoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        IndianDietaryBadge(isVeg = isVeg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = merchant.businessName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = SaveBiteAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = merchant.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " • ${merchant.distanceKm} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title & Description
                Text(
                    text = pkg.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = pkg.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Meta & Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
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
                                color = SaveBiteAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SaveBiteEmerald.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nature,
                                contentDescription = null,
                                tint = SaveBiteEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${pkg.co2SavedKg} kg CO₂ saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = SaveBiteEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Pricing
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (pkg.isDonation) {
                            Text(
                                text = "FREE MEAL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = SaveBiteEmerald
                            )
                        } else {
                            Text(
                                text = formatRupees(pkg.originalPrice),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                textDecoration = TextDecoration.LineThrough
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = formatRupees(pkg.discountedPrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = SaveBiteEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodDonationBannerCard(
    onExploreDonations: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onExploreDonations)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD97706)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFB45309)) {
                        Text(
                            text = "NGO PARTNERSHIP",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Robin Hood Army",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Feed the Needy • Free Surplus Food Rescue",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F)
                )
                Text(
                    text = "Over 1,200+ free surplus meal boxes distributed to local shelters this week across India.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF92400E),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
