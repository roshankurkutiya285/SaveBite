package com.example.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val IndianVegGreen = Color(0xFF0F8A43)
val IndianVegGreenBg = Color(0xFFE8F5E9)
val IndianNonVegRed = Color(0xFFB91C1C)
val IndianNonVegRedBg = Color(0xFFFEE2E2)
val IndianSaffron = Color(0xFFE05322)
val IndianMarigold = Color(0xFFD97706)

fun formatRupees(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹${amount.toInt()}"
    } else {
        "₹${"%.0f".format(amount)}"
    }
}

/**
 * Iconic Indian Food Safety (FSSAI) style Green/Red Veg & Non-Veg mark.
 */
@Composable
fun IndianDietaryBadge(
    isVeg: Boolean = true,
    showLabel: Boolean = false,
    size: Dp = 15.dp,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isVeg) IndianVegGreen else IndianNonVegRed
    val dotColor = if (isVeg) IndianVegGreen else IndianNonVegRed

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .border(width = 1.6.dp, color = borderColor, shape = RoundedCornerShape(3.dp))
                .padding(2.5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(size * 0.46f)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }

        if (showLabel) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isVeg) "Pure Veg" else "Non-Veg",
                color = borderColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
