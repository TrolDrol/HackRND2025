package com.az.hackrnd2025.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.getValue

@Composable
fun AdvancedCatLoadingAnimation(
    catPainter: Painter,
    ballPainter: Painter,
    ballSize: Dp = 40.dp,
    catSize: Dp = 200.dp,
    orbitRadius: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Анимация угла вращения
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    // Дополнительная анимация масштаба клубка
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Анимация альфа-канала для мерцания
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Кот в центре
        Image(
            painter = catPainter,
            contentDescription = "Cat",
            modifier = Modifier.size(catSize)
        )

        // Вращающийся клубок
        val radiusPx = with(LocalDensity.current) { orbitRadius.toPx() }
        val radians = Math.toRadians(angle.toDouble())

        val offsetX = (radiusPx * cos(radians)).toFloat()
        val offsetY = (radiusPx * sin(radians)).toFloat()

        Image(
            painter = ballPainter,
            contentDescription = "Loading ball",
            modifier = Modifier
                .size(ballSize)
                .offset(
                    x = with(LocalDensity.current) { offsetX.toDp() },
                    y = with(LocalDensity.current) { offsetY.toDp() }
                )
                .rotate(angle * 2) // Клубок вращается в 2 раза быстрее
                .scale(scale)
                .graphicsLayer {
                    this.alpha = alpha
                }
        )
    }
}