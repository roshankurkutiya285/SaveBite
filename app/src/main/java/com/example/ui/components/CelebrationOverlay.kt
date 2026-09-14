package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.SoundHapticsManager
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val color: Color,
    val radius: Float,
    val speedY: Float,
    val speedX: Float,
    val rotationSpeed: Float
)

@Composable
fun CelebrationDialog(
    isVisible: Boolean,
    title: String,
    subtitle: String,
    iconEmoji: String,
    statHighlight: String? = null,
    isBadgeUnlock: Boolean = false,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val context = LocalContext.current

    LaunchedEffect(isVisible) {
        if (isBadgeUnlock) {
            SoundHapticsManager.playAchievementFanfare(context)
        } else {
            SoundHapticsManager.playSuccessChime(context)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            // Confetti Shower Animation Canvas
            ConfettiCanvas()

            // Main Celebration Card
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Avatar Badge
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale = infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "badgePulse"
                    )

                    Box(
                        modifier = Modifier
                            .scale(pulseScale.value)
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                if (isBadgeUnlock) Color(0xFFFEF3C7) else SaveBiteEmerald.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconEmoji, fontSize = 42.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isBadgeUnlock) Color(0xFFD97706) else SaveBiteEmerald
                    ) {
                        Text(
                            text = if (isBadgeUnlock) "✨ BADGE UNLOCKED! ✨" else "🎉 PICKUP CONFIRMED!",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )

                    if (statHighlight != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = statHighlight,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = SaveBiteEmerald,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            SoundHapticsManager.playClick(context)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBadgeUnlock) Color(0xFFD97706) else SaveBiteEmerald
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBadgeUnlock) "Claim Achievement" else "Awesome!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfettiCanvas() {
    val particles = remember {
        val colors = listOf(
            Color(0xFF10B981), // Emerald
            Color(0xFFF59E0B), // Amber
            Color(0xFF3B82F6), // Blue
            Color(0xFFEC4899), // Pink
            Color(0xFF8B5CF6), // Purple
            Color(0xFFFBBF24)  // Gold
        )
        List(40) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.4f,
                color = colors[Random.nextInt(colors.size)],
                radius = Random.nextFloat() * 10f + 6f,
                speedY = Random.nextFloat() * 200f + 150f,
                speedX = (Random.nextFloat() - 0.5f) * 100f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 360f
            )
        }
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = animProgress.value
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { p ->
            val curY = (p.initialY * canvasHeight) + (p.speedY * t * 3.5f)
            val curX = (p.initialX * canvasWidth) + (p.speedX * t) + (kotlin.math.sin(t * 10f + p.initialX * 10f) * 30f)
            val alpha = (1f - (curY / (canvasHeight * 1.1f))).coerceIn(0f, 1f)

            if (curY < canvasHeight + 50f) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius,
                    center = Offset(curX, curY)
                )
            }
        }
    }
}
