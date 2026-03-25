package com.proyectotiro.archerytimer.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.ArcheryEnd
import com.proyectotiro.archerytimer.data.ArrowImpact
import com.proyectotiro.archerytimer.data.Scorecard
import com.proyectotiro.archerytimer.logic.ScorecardCerebro
import com.proyectotiro.archerytimer.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActiveScoringScreen(
    lang: Lang,
    darkRed: Color,
    scorecardEnds: List<ArcheryEnd>,
    currentActiveArrows: List<ArrowImpact>,
    weatherSelected: String,
    locationText: String,
    timerOption: String,
    targetOption: String,
    isTimerRunning: Boolean,
    isSetupComplete: Boolean,
    isTimerEnabled: Boolean,
    onWeatherSelect: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onTimerOptionSelect: (String) -> Unit,
    onTargetOptionSelect: (String) -> Unit,
    onStartClick: () -> Unit,
    onToggleTimer: () -> Unit,
    onArrowClick: (Int, Boolean, Float, Float) -> Unit,
    onDeleteLastArrow: () -> Unit,
    onNextEnd: () -> Unit,
    onFinishCheck: (Scorecard) -> Unit,
    onGoHome: () -> Unit,
    timerDisplay: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val totalScore = scorecardEnds.sumOf { end -> end.arrows.sumOf { it.value } } + currentActiveArrows.sumOf { it.value }
    val totalArrowsShot = (scorecardEnds.size * 6) + currentActiveArrows.size

    LaunchedEffect(key1 = scorecardEnds.size, key2 = currentActiveArrows.size) {
        if (scorecardEnds.size >= 1 || currentActiveArrows.size == 6) {
            scrollState.animateScrollTo(value = scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 12.dp).pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column {
                Text(text = "Total: $totalScore ($totalArrowsShot 🏹)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(text = "Avg: ${"%.2f".format(totalScore.toDouble() / (if(totalArrowsShot > 0) totalArrowsShot else 1))}", color = Color.White, fontSize = 18.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleTimer) { Text(text = if (isTimerEnabled) "⌛" else "⏳", fontSize = 20.sp) }
                IconButton(onClick = onGoHome) { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.White) }
                Text(text = weatherSelected, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(height = 10.dp))

        if (!isSetupComplete) {
            Column(modifier = Modifier.weight(weight = 1f).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(height = 10.dp))
                Text(text = if(lang == Lang.ES) "SELECCIONAR CLIMA" else "SELECT WEATHER", color = Color.White, fontWeight = FontWeight.Bold)
                WeatherSelector(selectedWeather = weatherSelected, onWeatherSelected = { onWeatherSelect(it); focusManager.clearFocus() })

                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(text = if(lang == Lang.ES) "LUGAR / CLUB" else "LOCATION", color = Color.White, fontWeight = FontWeight.Bold)
                TextField(
                    value = locationText,
                    onValueChange = onLocationChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.DarkGray, unfocusedContainerColor = Color.DarkGray, focusedTextColor = Color.White),
                    shape = RoundedCornerShape(size = 8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(text = if(lang == Lang.ES) "RELOJ" else "TIMER", color = Color.White, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { onTimerOptionSelect("CON") }, colors = ButtonDefaults.buttonColors(containerColor = if(timerOption=="CON") darkRed else Color.DarkGray)) {
                        Text(if(lang == Lang.ES) "RELOJ ON" else "TIMER ON")
                    }
                    Button(onClick = { onTimerOptionSelect("SIN") }, colors = ButtonDefaults.buttonColors(containerColor = if(timerOption=="SIN") darkRed else Color.DarkGray)) {
                        Text(if(lang == Lang.ES) "RELOJ OFF" else "TIMER OFF")
                    }
                }

                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(text = if(lang == Lang.ES) "DIANA" else "TARGET", color = Color.White, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { onTargetOptionSelect("CON") }, colors = ButtonDefaults.buttonColors(containerColor = if(targetOption=="CON") darkRed else Color.DarkGray)) {
                        Text(if(lang == Lang.ES) "DIANA ON" else "TARGET ON")
                    }
                    Button(onClick = { onTargetOptionSelect("SIN") }, colors = ButtonDefaults.buttonColors(containerColor = if(targetOption=="SIN") darkRed else Color.DarkGray)) {
                        Text(if(lang == Lang.ES) "DIANA OFF" else "TARGET OFF")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                if (weatherSelected.isNotEmpty() && timerOption.isNotEmpty() && targetOption.isNotEmpty()) {
                    Button(onClick = onStartClick, modifier = Modifier.size(width = 180.dp, height = 50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                        Text(text = if(lang == Lang.ES) "INICIAR" else "START")
                    }
                }
            }
        } else if (isTimerRunning && isTimerEnabled) {
            Box(modifier = Modifier.weight(weight = 1f), contentAlignment = Alignment.Center) { timerDisplay() }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(weight = 1f).fillMaxWidth()) {
                    Column(modifier = Modifier.verticalScroll(state = scrollState), verticalArrangement = Arrangement.Top) {
                        scorecardEnds.forEachIndexed { idx, end ->
                            Row(modifier = Modifier.fillMaxWidth().background(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(size = 8.dp)).padding(all = 4.dp).padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "${idx + 1}", color = Color.Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.width(width = 20.dp))
                                Row { end.arrows.forEach { ArrowBox(score = it.value, isX = it.isX, size = 26.dp) } }
                                Text(text = end.arrows.sumOf { it.value }.toString(), color = Color.White, fontSize = 14.sp)
                            }
                        }
                        if (scorecardEnds.size < 12) {
                            Row(modifier = Modifier.fillMaxWidth().border(width = 1.dp, color = Color.Yellow, shape = RoundedCornerShape(size = 8.dp)).padding(all = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "${scorecardEnds.size + 1}", color = Color.Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.width(width = 20.dp))
                                Row { (0..5).forEach { i -> if (i < currentActiveArrows.size) ArrowBox(score = currentActiveArrows[i].value, isX = currentActiveArrows[i].isX, size = 28.dp) else Box(modifier = Modifier.padding(all = 1.dp).size(size = 28.dp).background(color = Color.DarkGray, shape = CircleShape)) } }
                                Text(text = currentActiveArrows.sumOf { it.value }.toString(), color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(height = 60.dp))
                    }
                }

                Column(modifier = Modifier.wrapContentHeight().fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (currentActiveArrows.size == 6 && scorecardEnds.size < 12) {
                        Button(
                            onClick = {
                                if(scorecardEnds.size < 11) {
                                    onNextEnd()
                                } else {
                                    val finalEnds = scorecardEnds + ArcheryEnd(arrows = currentActiveArrows)
                                    val finalCard = Scorecard(
                                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                        location = locationText,
                                        weatherIcon = weatherSelected,
                                        targetMode = targetOption == "CON",
                                        ends = finalEnds,
                                        totalScore = ScorecardCerebro.calculateTotalScore(finalEnds),
                                        avg = ScorecardCerebro.calculateAverage(finalEnds),
                                        countX = ScorecardCerebro.countValue(finalEnds, 10, true),
                                        count10 = ScorecardCerebro.countValue(finalEnds, 10, false),
                                        count9 = ScorecardCerebro.countValue(finalEnds, 9, false)
                                    )
                                    onFinishCheck(finalCard)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.5f).height(height = 45.dp).padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if(scorecardEnds.size < 11) Color(0xFF2E7D32) else darkRed)
                        ) {
                            Text(text = if(scorecardEnds.size < 11) (if(lang == Lang.ES) "SIG. TANDA" else "NEXT END") else (if(lang == Lang.ES) "TERMINAR" else "FINISH"))
                        }
                    }

                    Box(modifier = Modifier.size(size = 260.dp).padding(all = 4.dp)) {
                        if (targetOption == "CON") {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.weight(1f)) {
                                    InteractiveTarget(modifier = Modifier.fillMaxSize(), onImpact = { v, x, xP, yP -> if(currentActiveArrows.size < 6) onArrowClick(v, x, xP, yP) })
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        currentActiveArrows.forEach { if(it.x != 0f) drawCircle(color = Color.Green, radius = 6f, center = Offset(x = it.x * size.width, y = it.y * size.height)) }
                                    }
                                }
                            }
                        } else {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    TargetBtn(label = "X", value = 10, onClick = { onArrowClick(10, true, 0.5f, 0.5f) }, size = 40.dp)
                                    Spacer(Modifier.width(2.dp))
                                    (10 downTo 7).forEach { TargetBtn(label = it.toString(), value = it, onClick = { onArrowClick(it, false, 0f, 0f) }, size = 40.dp); Spacer(Modifier.width(2.dp)) }
                                }
                                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    (6 downTo 2).forEach { TargetBtn(label = it.toString(), value = it, onClick = { onArrowClick(it, false, 0f, 0f) }, size = 40.dp); Spacer(Modifier.width(2.dp)) }
                                }
                                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    TargetBtn(label = "1", value = 1, onClick = { onArrowClick(1, false, 0f, 0f) }, size = 40.dp)
                                    Spacer(Modifier.width(2.dp))
                                    TargetBtn(label = "M", value = 0, onClick = { onArrowClick(0, false, 0f, 0f) }, size = 40.dp)
                                }
                            }
                        }
                    }

                    IconButton(onClick = onDeleteLastArrow, modifier = Modifier.padding(top = 4.dp).background(color = Color.DarkGray, shape = CircleShape).size(size = 40.dp)) {
                        Text(text = "◀", color = Color.White)
                    }
                }
            }
        }
    }
}