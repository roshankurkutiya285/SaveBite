package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import kotlin.math.abs

@Composable
fun PickupQrCard(
    orderNumber: String,
    pickupPin: String,
    qrPayload: String,
    merchantName: String,
    pickupWindow: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pickup_qr_card"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "READY FOR PICKUP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46),
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = orderNumber,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // High-contrast QR matrix canvas
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                DynamicQrCanvas(
                    payload = qrPayload,
                    modifier = Modifier.size(176.dp)
                )

                // Center shield emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SaveBiteEmerald)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "SaveBite Verified",
                        tint = SaveBiteAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6-digit confirmation pin
            Text(
                text = "PICKUP PIN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                letterSpacing = 1.5.sp
            )

            Text(
                text = pickupPin,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = SaveBiteEmerald,
                letterSpacing = 4.sp,
                modifier = Modifier.testTag("pickup_pin_text")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Show this QR code or 6-digit PIN to store staff at checkout.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Store & Pickup Window banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = merchantName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pickupWindow,
                            style = MaterialTheme.typography.bodySmall,
                            color = SaveBiteAmber
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = SaveBiteEmerald
                    )
                }
            }
        }
    }
}

/**
 * Deterministically renders a stylized QR pattern based on payload hash
 */
@Composable
fun DynamicQrCanvas(
    payload: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val gridSize = 19
        val cellSize = size.width / gridSize
        val hash = abs(payload.hashCode())

        // Background
        drawRect(Color.White)

        // Corner finder patterns (Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(0f, 0f, cellSize, 7)
        drawFinderPattern((gridSize - 7) * cellSize, 0f, cellSize, 7)
        drawFinderPattern(0f, (gridSize - 7) * cellSize, cellSize, 7)

        // Fill inner data modules deterministically
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                // Skip finder patterns areas
                val inTopLeft = row < 8 && col < 8
                val inTopRight = row < 8 && col >= gridSize - 8
                val inBottomLeft = row >= gridSize - 8 && col < 8
                val inCenter = row in 8..10 && col in 8..10

                if (inTopLeft || inTopRight || inBottomLeft || inCenter) continue

                // Deterministic pseudo-random based on coordinate & payload hash
                val bitVal = ((hash + (row * 31) + (col * 17) + (row * col)) % 7) > 2
                if (bitVal) {
                    drawRoundRect(
                        color = Color(0xFF135A42),
                        topLeft = Offset(col * cellSize + 0.8f, row * cellSize + 0.8f),
                        size = Size(cellSize - 1.6f, cellSize - 1.6f),
                        cornerRadius = CornerRadius(2.5f, 2.5f)
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    cellSize: Float,
    modules: Int
) {
    val totalSize = cellSize * modules
    // Outer black box
    drawRoundRect(
        color = Color(0xFF135A42),
        topLeft = Offset(x, y),
        size = Size(totalSize, totalSize),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Inner white gap
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(x + cellSize, y + cellSize),
        size = Size(totalSize - 2 * cellSize, totalSize - 2 * cellSize),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Center black square
    drawRoundRect(
        color = Color(0xFF135A42),
        topLeft = Offset(x + 2 * cellSize, y + 2 * cellSize),
        size = Size(totalSize - 4 * cellSize, totalSize - 4 * cellSize),
        cornerRadius = CornerRadius(4f, 4f)
    )
}
