package com.proyectotiro.archerytimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherSelector(
    selectedWeather: String, // Recibe cuál está seleccionado
    onWeatherSelected: (String) -> Unit
) {
    val options = listOf("☀️", "☁️", "💨", "🌧️")
    val darkRed = Color(0xFF8B0000)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { icon ->
            Box(
                modifier = Modifier
                    .size(size = 60.dp)
                    .background(
                        color = if (selectedWeather == icon) darkRed else Color.DarkGray,
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .clickable {
                        onWeatherSelected(icon)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 30.sp)
            }
        }
    }
}