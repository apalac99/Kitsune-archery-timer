package com.proyectotiro.archerytimer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.ArrowImpact
import com.proyectotiro.archerytimer.ui.components.InteractiveTarget

@Composable
fun HeatmapScreen(
    impacts: List<ArrowImpact>,
    lang: Lang,
    darkRed: Color
) {
    var selectedFilter by remember { mutableIntStateOf(value = 0) }
    val filteredImpacts = if (selectedFilter == 0) impacts else impacts.filterIndexed { index, _ -> (index % 6) + 1 == selectedFilter }

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == Lang.ES) "ANÁLISIS DE AGRUPACIÓN" else "GROUPING ANALYSIS",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(height = 16.dp))

        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(ratio = 1f).background(color = Color.DarkGray, shape = CircleShape).padding(all = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            InteractiveTarget(onImpact = { _, _, _, _ -> })

            Canvas(modifier = Modifier.fillMaxSize()) {
                filteredImpacts.forEach { impact ->
                    if (impact.x != 0f || impact.y != 0f) {
                        drawCircle(
                            color = if (selectedFilter == 0) Color.Green else darkRed,
                            radius = 8f,
                            center = Offset(x = impact.x * size.width, y = impact.y * size.height)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(height = 24.dp))

        Text(text = if (lang == Lang.ES) "Ver por flecha:" else "View by arrow:", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(height = 8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterBtn(label = if (lang == Lang.ES) "T" else "All", isSelected = selectedFilter == 0, onClick = { selectedFilter = 0 }, darkRed = darkRed)
            (1..6).forEach { num ->
                FilterBtn(label = num.toString(), isSelected = selectedFilter == num, onClick = { selectedFilter = num }, darkRed = darkRed)
            }
        }
    }
}

@Composable
private fun FilterBtn(label: String, isSelected: Boolean, onClick: () -> Unit, darkRed: Color) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) darkRed else Color.DarkGray),
        contentPadding = PaddingValues(all = 0.dp),
        modifier = Modifier.size(size = 45.dp),
        shape = CircleShape
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}