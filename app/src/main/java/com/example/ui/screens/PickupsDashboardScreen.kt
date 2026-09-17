package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.local.entity.OrderEntity

@Composable
fun PickupsDashboardScreen(
    orders: List<OrderEntity>,
    onSimulateRedeem: (String) -> Unit = {},
    onCancelOrder: (String) -> Unit,
    onSubmitFeedback: (orderId: String, rating: Int, reviewText: String, reviewTags: String) -> Unit = { _, _, _, _ -> },
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerPickupsScreen(
        orders = orders,
        onCancelOrder = onCancelOrder,
        onSubmitFeedback = onSubmitFeedback,
        onExploreClick = onExploreClick,
        modifier = modifier
    )
}
