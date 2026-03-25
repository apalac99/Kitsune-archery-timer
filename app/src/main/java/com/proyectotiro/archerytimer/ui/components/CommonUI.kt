package com.proyectotiro.archerytimer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TargetBtn(label: String, value: Int, onClick: (Int) -> Unit, size: androidx.compose.ui.unit.Dp = 60.dp) {
    val colors = when (label) {
        "X", "10", "9" -> Color.Yellow to Color.Black
        "8", "7" -> Color.Red to Color.White
        "6", "5", "<=6" -> Color(0xFF1976D2) to Color.White
        "4", "3" -> Color.Black to Color.White
        "2", "1" -> Color.White to Color.Black
        "◀" -> Color.DarkGray to Color.White
        else -> Color.DarkGray to Color.White
    }
    Button(
        onClick = { onClick(value) },
        colors = ButtonDefaults.buttonColors(containerColor = colors.first),
        modifier = Modifier.padding(2.dp).size(size),
        shape = CircleShape,
        border = if(label=="3" || label=="4" || label=="<=6") BorderStroke(1.dp, Color.White) else null,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = label, color = colors.second, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
    }
}

@Composable
fun ArrowBox(score: Int, isX: Boolean = false, size: androidx.compose.ui.unit.Dp = 35.dp) {
    val colors = when (score) {
        10 -> Color.Yellow to Color.Black
        9 -> Color.Yellow to Color.Black
        8, 7 -> Color.Red to Color.White
        6, 5 -> Color(0xFF1976D2) to Color.White
        4, 3 -> Color.Black to Color.White
        2, 1 -> Color.White to Color.Black
        else -> Color.DarkGray to Color.White
    }
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(size)
            .background(colors.first, CircleShape)
            .border(if (score == 3 || score == 4) BorderStroke(1.dp, Color.White) else BorderStroke(0.dp, Color.Transparent), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // CORRECCIÓN: Muestra "X" si isX es true
        val textToShow = if (isX) "X" else if (score == 0) "M" else score.toString()
        Text(text = textToShow, color = colors.second, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.45).sp)
    }
}

@Composable
fun StatTable(data: List<Pair<String, String>>, darkRed: Color) {
    Column(Modifier.fillMaxWidth().border(1.dp, Color.LightGray)) {
        data.forEach { (key, value) ->
            Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceBetween) {
                Text(text = key, color = Color.Black, fontWeight = FontWeight.Medium)
                Text(text = value, color = darkRed, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = Color.LightGray)
        }
    }
}