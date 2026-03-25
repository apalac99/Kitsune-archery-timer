package com.proyectotiro.archerytimer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.proyectotiro.archerytimer.data.ArrowImpact

@Composable
fun HeatmapDisplay(
    modifier: Modifier = Modifier,
    impacts: List<ArrowImpact>,
    dotColor: Color = Color.Green
) {
    val gold = Color(0xFFFFD700)
    val red = Color.Red
    val blue = Color(0xFF2196F3)
    val black = Color.Black
    val white = Color.White
    val line = Color.Gray

    Box(
        modifier = modifier
            .aspectRatio(ratio = 1f)
            .background(color = Color.DarkGray, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val totalRadius = size.width / 2
            val ringWidth = totalRadius / 11

            val rings = listOf(
                white, white, black, black, blue, blue, red, red, gold, gold, gold
            )

            // 1. Dibujar la Diana de fondo
            for (i in 11 downTo 1) {
                drawCircle(
                    color = rings[11 - i],
                    radius = ringWidth * i,
                    center = Offset(x = centerX, y = centerY)
                )
                drawCircle(
                    color = if (i in 7..8) white else line,
                    radius = ringWidth * i,
                    center = Offset(x = centerX, y = centerY),
                    style = Stroke(width = 1f)
                )
            }

            // 2. Dibujar los impactos (Puntos del mapa de calor)
            impacts.forEach { impact ->
                if (impact.value > 0 || impact.isX) {
                    drawCircle(
                        color = dotColor,
                        radius = 6f, // Tamaño del impacto
                        center = Offset(
                            x = impact.x * size.width,
                            y = impact.y * size.height
                        )
                    )
                }
            }
        }
    }
}