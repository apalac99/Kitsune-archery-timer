package com.proyectotiro.archerytimer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.sqrt

@Composable
fun InteractiveTarget(
    modifier: Modifier = Modifier,
    onImpact: (Int, Boolean, Float, Float) -> Unit
) {
    val gold = Color(0xFFFFD700); val red = Color.Red; val blue = Color(0xFF2196F3); val black = Color.Black; val white = Color.White; val line = Color.Gray
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.aspectRatio(1f).background(Color.DarkGray, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> isDragging = true; dragOffset = offset },
                    onDrag = { change, dragAmount -> change.consume(); dragOffset += dragAmount },
                    onDragEnd = {
                        isDragging = false
                        val targetPos = Offset(dragOffset.x, dragOffset.y - 120f) // Mira desplazada arriba
                        val centerX = size.width / 2; val centerY = size.height / 2
                        val distance = sqrt((targetPos.x - centerX) * (targetPos.x - centerX) + (targetPos.y - centerY) * (targetPos.y - centerY))
                        val ring = (size.width / 2) / 11
                        val res = when {
                            distance <= ring -> 10 to true
                            distance <= ring * 2 -> 10 to false
                            distance <= ring * 3 -> 9 to false
                            distance <= ring * 4 -> 8 to false
                            distance <= ring * 5 -> 7 to false
                            distance <= ring * 6 -> 6 to false
                            distance <= ring * 7 -> 5 to false
                            distance <= ring * 8 -> 4 to false
                            distance <= ring * 9 -> 3 to false
                            distance <= ring * 10 -> 2 to false
                            distance <= ring * 11 -> 1 to false
                            else -> 0 to false
                        }
                        onImpact(res.first, res.second, targetPos.x / size.width, targetPos.y / size.height)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val centerX = size.width / 2; val centerY = size.height / 2; val ring = (size.width / 2) / 11
            val rings = listOf(white, white, black, black, blue, blue, red, red, gold, gold, gold)
            for (i in 11 downTo 1) {
                drawCircle(rings[11 - i], ring * i, Offset(centerX, centerY))
                drawCircle(if (i in 7..8) white else line, ring * i, Offset(centerX, centerY), style = Stroke(1f))
            }
            if (isDragging) {
                val cross = Offset(dragOffset.x, dragOffset.y - 120f)
                drawLine(Color.White, Offset(cross.x, cross.y - 30f), Offset(cross.x, cross.y + 30f), 3f)
                drawLine(Color.White, Offset(cross.x - 30f, cross.y), Offset(cross.x + 30f, cross.y), 3f)
            }
        }
    }
}