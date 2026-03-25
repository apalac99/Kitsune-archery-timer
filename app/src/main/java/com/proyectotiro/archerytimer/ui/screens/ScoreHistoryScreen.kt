package com.proyectotiro.archerytimer.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.Scorecard
import com.proyectotiro.archerytimer.logic.ArcheryAI
import com.proyectotiro.archerytimer.ui.components.ArrowBox
import com.proyectotiro.archerytimer.ui.components.HeatmapDisplay

@Composable
fun ScoreHistoryScreen(
    scorecards: List<Scorecard>,
    lang: Lang,
    darkRed: Color,
    onNewScorecard: () -> Unit,
    onDeleteScorecard: (Long) -> Unit
) {
    var selectedCard by remember { mutableStateOf<Scorecard?>(value = null) }
    var aiAnalysisCard by remember { mutableStateOf<Scorecard?>(value = null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewScorecard,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .padding(all = 16.dp)
        ) {
            Text(
                text = if (lang == Lang.ES) "LIBRO DE RESULTADOS" else "SCOREBOOK",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (scorecards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = if (lang == Lang.ES) "No hay chequeos guardados" else "No saved scores", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                    items(scorecards) { card ->
                        Card(
                            onClick = { selectedCard = card },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(all = 16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(text = card.weatherIcon, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(width = 12.dp))
                                    Column {
                                        Text(text = card.location, color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = card.date, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                        Text(
                                            text = "Total: ${card.totalScore} | Avg: ${"%.2f".format(card.avg)}",
                                            color = Color.Yellow,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Botón de IA (🤖) - Nuevo
                                    IconButton(onClick = { aiAnalysisCard = card }) {
                                        Text(text = "🤖", fontSize = 20.sp)
                                    }

                                    // ICONO DE DIANA: Solo si se usó el modo diana
                                    if (card.targetMode) {
                                        Text(text = "🎯", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                    }

                                    IconButton(onClick = { onDeleteScorecard(card.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- VENTANA DE DETALLE CON MAPA DE CALOR (EXISTENTE) ---
        selectedCard?.let { card ->
            AlertDialog(
                onDismissRequest = { selectedCard = null },
                title = { Text(text = "${card.location} - ${card.date}", color = Color.Black) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp).verticalScroll(state = rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        card.ends.forEachIndexed { index, end ->
                            Row(
                                modifier = Modifier.fillMaxWidth().background(color = Color.LightGray, shape = RoundedCornerShape(size = 4.dp)).padding(all = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${index + 1}", color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.width(width = 20.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    end.arrows.forEach { arrow -> ArrowBox(score = arrow.value, isX = arrow.isX, size = 24.dp) }
                                }
                                Text(text = end.arrows.sumOf { it.value }.toString(), color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }

                        if (card.targetMode) {
                            Spacer(modifier = Modifier.height(height = 16.dp))
                            Text(
                                text = if(lang == Lang.ES) "ANÁLISIS DE AGRUPACIÓN" else "GROUPING ANALYSIS",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            val allArrows = card.ends.flatMap { it.arrows }
                            HeatmapDisplay(
                                modifier = Modifier.fillMaxWidth().height(height = 250.dp).padding(all = 8.dp),
                                impacts = allArrows,
                                dotColor = darkRed
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedCard = null }, colors = ButtonDefaults.buttonColors(containerColor = darkRed)) {
                        Text(text = if (lang == Lang.ES) "CERRAR" else "CLOSE", color = Color.White)
                    }
                }
            )
        }

        // --- VENTANA DE ANÁLISIS IA (NUEVA) ---
        aiAnalysisCard?.let { card ->
            AlertDialog(
                onDismissRequest = { aiAnalysisCard = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖 ", fontSize = 24.sp)
                        Text(text = if (lang == Lang.ES) "Análisis Técnico" else "Technical Analysis", color = Color.Black)
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth().verticalScroll(state = rememberScrollState())) {
                        Text(
                            text = ArcheryAI.analyzeScorecard(card = card, lang = lang),
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { aiAnalysisCard = null },
                        colors = ButtonDefaults.buttonColors(containerColor = darkRed)
                    ) {
                        Text(text = "OK", color = Color.White)
                    }
                }
            )
        }
    }
}