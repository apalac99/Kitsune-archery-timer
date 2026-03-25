package com.proyectotiro.archerytimer.ui

import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.reflect.TypeToken
import com.proyectotiro.archerytimer.R
import com.proyectotiro.archerytimer.AppMode
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.*
import com.proyectotiro.archerytimer.logic.*
import com.proyectotiro.archerytimer.ui.screens.*
import com.proyectotiro.archerytimer.ui.components.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@Composable
fun ArcheryAppMain() {
    val context = LocalContext.current
    val prefs = remember { PreferenceManager(context = context) }
    val whistle = remember { WhistleSystem(context = context) }
    val donationManager = remember { DonationManager(context = context) } // Nuevo: Gestor de donaciones
    val darkRed = Color(0xFF8B0000)

    val savedLang = prefs.getString(key = "app_lang")
    var currentLang by remember { mutableStateOf(value = if (savedLang == "EN") Lang.EN else Lang.ES) }
    var currentMode by remember { mutableStateOf(value = if (savedLang == null) AppMode.LANG_SELECT else AppMode.WELCOME) }

    var isTestMode by remember { mutableStateOf(value = false) }
    var showInfoDialog by remember { mutableStateOf(value = false) }
    var showDonationDialog by remember { mutableStateOf(value = false) } // Nuevo: Estado de ventana donación
    var showSettingsDialog by remember { mutableStateOf(value = false) }
    var showStatsDialog by remember { mutableStateOf(value = false) }
    var showCalendarDialog by remember { mutableStateOf(value = false) }
    var showConfirmReset100 by remember { mutableStateOf(value = false) }
    var showConfirmResetRanking by remember { mutableStateOf(value = false) }

    var timeRC by remember { mutableIntStateOf(value = prefs.getInt(key = "time_rc", default = 180)) }
    var timeRI by remember { mutableIntStateOf(value = prefs.getInt(key = "time_ri", default = 90)) }
    var timeLeft by remember { mutableIntStateOf(value = 10) }
    var roundCount by remember { mutableIntStateOf(value = 0) }
    var isPrepPhase by remember { mutableStateOf(value = false) }
    var isShootPhase by remember { mutableStateOf(value = false) }
    var isBlinking by remember { mutableStateOf(value = true) }
    var timer: CountDownTimer? by remember { mutableStateOf(value = null) }

    var score100 by remember { mutableIntStateOf(value = 0) }
    var arrows100 by remember { mutableIntStateOf(value = 0) }
    var bestArrows100 by remember { mutableIntStateOf(value = prefs.getInt(key = "best_100", default = 0)) }

    var mmrRank by remember { mutableIntStateOf(value = prefs.getInt(key = "mmr_rank", default = 0)) }
    var opponentLevel by remember { mutableIntStateOf(value = 1) }
    var playerSets by remember { mutableIntStateOf(value = 0) }
    var opponentSets by remember { mutableIntStateOf(value = 0) }
    var opponentArrows by remember { mutableStateOf(value = listOf(0, 0, 0)) }
    var playerArrowsInput by remember { mutableStateOf(value = listOf<Int>()) }
    var matchStatus by remember { mutableStateOf(value = if (currentLang == Lang.ES) "En espera" else "Waiting") }

    var scorecards by remember { mutableStateOf(value = prefs.getList(key = "list_scorecards", typeToken = object : TypeToken<List<Scorecard>>() {})) }
    var activeEnds by remember { mutableStateOf(value = listOf<ArcheryEnd>()) }
    var currentArrows by remember { mutableStateOf(value = listOf<ArrowImpact>()) }
    var scorecardWeather by remember { mutableStateOf(value = "") }
    var scorecardLocation by remember { mutableStateOf(value = "") }
    var scorecardTimerOption by remember { mutableStateOf(value = "") }
    var scorecardTargetOption by remember { mutableStateOf(value = "") }
    var isScoringSetupComplete by remember { mutableStateOf(value = false) }
    var isTimerEnabled by remember { mutableStateOf(value = true) }
    var events by remember { mutableStateOf(value = prefs.getList(key = "list_events", typeToken = object : TypeToken<List<ArcheryEvent>>() {})) }

    var arrowsToday by remember { mutableIntStateOf(value = ArcheryLogic.calculateStats(prefs = prefs.getPrefs(), period = "day")) }
    var arrowsWeek by remember { mutableIntStateOf(value = ArcheryLogic.calculateStats(prefs = prefs.getPrefs(), period = "week")) }

    fun stopEverything() {
        timer?.cancel(); timer = null; isShootPhase = false; isPrepPhase = false; timeLeft = 10
    }

    fun registerArrows(count: Int) {
        if (isTestMode) return
        val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.saveInt(key = key, value = prefs.getInt(key = key, default = 0) + count)
        arrowsToday = ArcheryLogic.calculateStats(prefs = prefs.getPrefs(), period = "day")
        arrowsWeek = ArcheryLogic.calculateStats(prefs = prefs.getPrefs(), period = "week")
    }

    fun startTimer(seconds: Int, isPrep: Boolean, shootTime: Int, onFinish: () -> Unit) {
        timer?.cancel(); isPrepPhase = isPrep; isShootPhase = !isPrep; timeLeft = seconds
        if (isShootPhase) whistle.playWhistle()
        val multiplier = if (isTestMode) 333L else 1000L
        timer = object : CountDownTimer((seconds * multiplier), 50) {
            override fun onTick(ms: Long) {
                val sec = ceil(ms.toDouble() / multiplier).toInt()
                if (sec != timeLeft) { timeLeft = sec; if (isShootPhase && timeLeft in 1..5) whistle.playToneShort() }
            }
            override fun onFinish() {
                timeLeft = 0; whistle.playWhistle(); isShootPhase = false
                if (isPrepPhase) startTimer(shootTime, false, shootTime, onFinish) else onFinish()
            }
        }.start()
    }

    LaunchedEffect(key1 = isPrepPhase) { while(isPrepPhase) { isBlinking = !isBlinking; delay(timeMillis = 500) } }

    Scaffold(
        topBar = {
            if (currentMode != AppMode.LANG_SELECT) {
                Column(modifier = Modifier.fillMaxWidth().background(color = Color.Black).statusBarsPadding()) {
                    if (isTestMode) Box(modifier = Modifier.fillMaxWidth().background(color = Color(0xFFFF9800)).padding(all = 2.dp), contentAlignment = Alignment.Center) {
                        Text(text = if(currentLang == Lang.ES) "PRUEBA" else "TEST", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), contentAlignment = Alignment.Center) {
                        Text(text = "${if(currentLang==Lang.ES) "Hoy" else "Today"}: $arrowsToday | ${if(currentLang==Lang.ES) "Sem" else "Week"}: $arrowsWeek", color = Color.Gray, fontSize = 14.sp)
                    }
                    val titleText = when(currentMode) {
                        AppMode.RC -> if(currentLang == Lang.ES) "RC - Ronda de clasificación" else "RC - Classification Round"
                        AppMode.RI -> if(currentLang == Lang.ES) "RI - Ronda individual" else "RI - Individual Round"
                        AppMode.MATCH_PLAY -> if(currentLang == Lang.ES) "Ranking: $mmrRank" else "Rank: $mmrRank"
                        AppMode.SCORE_HISTORY -> if(currentLang == Lang.ES) "Libro de Resultados" else "Scorebook"
                        AppMode.SCORE_ACTIVE -> if(currentLang == Lang.ES) "Chequeo Activo" else "Active Scoring"
                        else -> currentMode.name.replace("_", " ")
                    }
                    Text(text = titleText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), textAlign = TextAlign.Center)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row { // Agrupación izquierda (Info + Donación)
                            IconButton(onClick = { showInfoDialog = true }, modifier = Modifier.size(size = 40.dp)) { Text(text = "ⓘ", color = Color.White, fontSize = 20.sp) }
                            IconButton(onClick = { showDonationDialog = true }, modifier = Modifier.size(size = 40.dp)) { Text(text = "❤️", fontSize = 20.sp) }
                        }
                        Row { // Agrupación derecha (Estadísticas + Calendario + Ajustes)
                            IconButton(onClick = { showStatsDialog = true }, modifier = Modifier.size(size = 40.dp)) { Text(text = "📊", color = Color.White, fontSize = 25.sp) }
                            IconButton(onClick = { showCalendarDialog = true }, modifier = Modifier.size(size = 40.dp)) { Text(text = "📅", color = Color.White, fontSize = 25.sp) }
                            IconButton(onClick = { showSettingsDialog = true }, modifier = Modifier.size(size = 40.dp)) { Text(text = "⚙️", color = Color.White, fontSize = 25.sp) }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentMode != AppMode.WELCOME && currentMode != AppMode.LANG_SELECT && currentMode != AppMode.SCORE_ACTIVE) {
                BottomAppBar(containerColor = Color(0xFF1A1A1A)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { stopEverything(); currentMode = AppMode.RC }) { Text(text = "RC") }
                        Button(onClick = { stopEverything(); currentMode = AppMode.RI }) { Text(text = "RI") }
                        Button(onClick = { stopEverything(); currentMode = AppMode.PLUS_100 }) { Text(text = "+100") }
                        Button(onClick = { stopEverything(); currentMode = AppMode.MATCH_PLAY }) { Text(text = "VS") }
                        Button(onClick = { stopEverything(); currentMode = AppMode.SCORE_HISTORY }) { Text(text = "📓") }
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.padding(paddingValues = padding).fillMaxSize()) {
            when (currentMode) {
                AppMode.LANG_SELECT -> LanguageSelectScreen(onSelect = { lang -> currentLang = lang; prefs.saveString(key = "app_lang", value = lang.name); currentMode = AppMode.WELCOME })
                AppMode.WELCOME -> WelcomeScreen(lang = currentLang, onSelect = { mode -> if (mode == AppMode.CALENDAR) showCalendarDialog = true else currentMode = mode })
                AppMode.RC -> TimerScreen(end = roundCount, time = timeLeft, isPrep = isPrepPhase, isShoot = isShootPhase, blink = isBlinking, lang = currentLang, darkRed = darkRed, onStart = { roundCount++; startTimer(10, true, timeRC) { registerArrows(6) } }, onReset = { stopEverything(); roundCount = 0 }, onFinishNow = { timer?.onFinish() })
                AppMode.RI -> TimerScreen(end = roundCount, time = timeLeft, isPrep = isPrepPhase, isShoot = isShootPhase, blink = isBlinking, lang = currentLang, darkRed = darkRed, onStart = { roundCount++; startTimer(10, true, timeRI) { registerArrows(3) } }, onReset = { stopEverything(); roundCount = 0 }, onFinishNow = { timer?.onFinish() })
                AppMode.PLUS_100 -> Plus100Screen(score = score100, arrows = arrows100, best = bestArrows100, darkRed = darkRed, lang = currentLang, onScore = { pts -> arrows100++; score100 += pts; registerArrows(1); if (score100 >= 100) { whistle.playWhistle(); if (!isTestMode && (bestArrows100 == 0 || arrows100 < bestArrows100)) { bestArrows100 = arrows100; prefs.saveInt(key = "best_100", value = bestArrows100) } } }, onReset = { score100 = 0; arrows100 = 0 })
                AppMode.MATCH_PLAY -> MatchPlayScreen(mmr = mmrRank, lvl = opponentLevel, pSets = playerSets, oSets = opponentSets, oArrows = opponentArrows, pArrows = playerArrowsInput, status = matchStatus, time = timeLeft, isPrep = isPrepPhase, isShoot = isShootPhase, blink = isBlinking, lang = currentLang, darkRed = darkRed, onStartTurn = { matchStatus = if (currentLang == Lang.ES) "Tira Tú" else "Your Turn"; opponentArrows = ArcheryLogic.generateOpponentList(lvl = opponentLevel); startTimer(10, true, timeRI) { matchStatus = if (currentLang == Lang.ES) "Anotar" else "Score"; playerArrowsInput = emptyList() } }, onArrowClick = { arrow -> if (playerArrowsInput.size < 3) { val newList = playerArrowsInput.toMutableList(); newList.add(arrow); playerArrowsInput = newList; if (playerArrowsInput.size == 3) { val pSum = playerArrowsInput.sum(); val oSum = opponentArrows.sum(); registerArrows(3); if (pSum > oSum) playerSets += 2 else if (pSum == oSum) { playerSets += 1; opponentSets += 1 } else opponentSets += 2; if (playerSets >= 6 || opponentSets >= 6) { matchStatus = if (playerSets >= 6) (if (currentLang == Lang.ES) "¡GANASTE!" else "WON!") else (if (currentLang == Lang.ES) "PERDISTE" else "LOST"); if (!isTestMode) { if (playerSets >= 6) { mmrRank += opponentLevel; val curW = prefs.getInt(key = "match_wins_lvl_$opponentLevel", default = 0); prefs.saveInt(key = "match_wins_lvl_$opponentLevel", value = curW + 1) } else { mmrRank = (mmrRank - 1).coerceAtLeast(0); val curL = prefs.getInt(key = "match_loss_lvl_$opponentLevel", default = 0); prefs.saveInt(key = "match_loss_lvl_$opponentLevel", value = curL + 1); opponentLevel = 1 }; prefs.saveInt(key = "mmr_rank", value = mmrRank) } } else matchStatus = if (currentLang == Lang.ES) "Siguiente Set" else "Next Set" } } }, onNextMatch = { if (opponentSets >= 6) opponentLevel = 1 else if (opponentLevel < 4) opponentLevel++; playerSets = 0; opponentSets = 0; matchStatus = if (currentLang == Lang.ES) "En espera" else "Waiting" }, onFinishNow = { timer?.onFinish() })
                AppMode.SCORE_HISTORY -> ScoreHistoryScreen(scorecards = scorecards, lang = currentLang, darkRed = darkRed, onNewScorecard = { stopEverything(); currentMode = AppMode.SCORE_ACTIVE; scorecardWeather = ""; scorecardLocation = ""; scorecardTimerOption = ""; scorecardTargetOption = ""; activeEnds = emptyList(); currentArrows = emptyList(); isScoringSetupComplete = false }, onDeleteScorecard = { id -> scorecards = scorecards.filter { it.id != id }; prefs.saveList(key = "list_scorecards", list = scorecards) })
                AppMode.SCORE_ACTIVE -> ActiveScoringScreen(lang = currentLang, darkRed = darkRed, scorecardEnds = activeEnds, currentActiveArrows = currentArrows, weatherSelected = scorecardWeather, locationText = scorecardLocation, timerOption = scorecardTimerOption, targetOption = scorecardTargetOption, isTimerRunning = (isShootPhase || isPrepPhase), isSetupComplete = isScoringSetupComplete, isTimerEnabled = isTimerEnabled, onWeatherSelect = { scorecardWeather = it }, onLocationChange = { scorecardLocation = it }, onTimerOptionSelect = { scorecardTimerOption = it; isTimerEnabled = (it == "CON") }, onTargetOptionSelect = { scorecardTargetOption = it }, onStartClick = { isScoringSetupComplete = true; if(isTimerEnabled) startTimer(10, true, timeRC) {} }, onToggleTimer = { isTimerEnabled = !isTimerEnabled; if(!isTimerEnabled) stopEverything() }, onArrowClick = { v, x, xP, yP -> if (currentArrows.size < 6) currentArrows = currentArrows + ArrowImpact(value = v, isX = x, x = xP, y = yP) }, onDeleteLastArrow = { if (currentArrows.isNotEmpty()) currentArrows = currentArrows.dropLast(1) }, onNextEnd = { activeEnds = activeEnds + ArcheryEnd(arrows = currentArrows); currentArrows = emptyList(); registerArrows(6); if(isTimerEnabled) startTimer(10, true, timeRC) {} }, onFinishCheck = { finalCard -> scorecards = scorecards + finalCard; prefs.saveList(key = "list_scorecards", list = scorecards); registerArrows(count = 6); stopEverything(); currentMode = AppMode.SCORE_HISTORY }, onGoHome = { if (activeEnds.isNotEmpty() || currentArrows.isNotEmpty()) { val finalEnds = if (currentArrows.isNotEmpty()) activeEnds + ArcheryEnd(arrows = currentArrows) else activeEnds; val partialCard = Scorecard(date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()), location = scorecardLocation, weatherIcon = scorecardWeather, targetMode = scorecardTargetOption == "CON", ends = finalEnds, totalScore = ScorecardCerebro.calculateTotalScore(finalEnds), avg = ScorecardCerebro.calculateAverage(finalEnds), countX = ScorecardCerebro.countValue(finalEnds, 10, true), count10 = ScorecardCerebro.countValue(finalEnds, 10, false), count9 = ScorecardCerebro.countValue(finalEnds, 9, false)); scorecards = scorecards + partialCard; prefs.saveList(key = "list_scorecards", list = scorecards) }; stopEverything(); currentMode = AppMode.WELCOME }, timerDisplay = { TimerScreen(end = activeEnds.size + 1, time = timeLeft, isPrep = isPrepPhase, isShoot = isShootPhase, blink = isBlinking, lang = currentLang, darkRed = darkRed, onStart = {}, onReset = { stopEverything() }, onFinishNow = { timer?.onFinish() }) })
                AppMode.CALENDAR -> WelcomeScreen(lang = currentLang, onSelect = { currentMode = it })
            }
        }
    }

    if (showDonationDialog) DonationPopup(lang = currentLang, darkRed = darkRed, onDismiss = { showDonationDialog = false }, onDonate = { donationManager.startDonationProcess(lang = currentLang) { success -> if (success) showDonationDialog = false } })

    if (showCalendarDialog) CalendarPopup(events = events, lang = currentLang, darkRed = darkRed, onDismiss = { showCalendarDialog = false }, onAddEvent = { t, d -> events = events + ArcheryEvent(title = t, date = d); prefs.saveList(key = "list_events", list = events) }, onDeleteEvent = { id -> events = events.filter { it.id != id }; prefs.saveList(key = "list_events", list = events) })

    if (showInfoDialog) AlertDialog(onDismissRequest = { showInfoDialog = false }, title = { Text(text = if (currentLang == Lang.ES) "Información" else "Information", color = Color.Black) }, text = { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) { Image(painter = painterResource(id = R.drawable.logo_club), contentDescription = null, modifier = Modifier.size(size = 100.dp)); Spacer(modifier = Modifier.height(height = 8.dp)); Text(text = "Desarrollo: Alejandro Palacios\nArquero Master 50+ Recurvo \nClub Kitsune\nLa Ceja Antioquia - Colombia", color = Color.Black, textAlign = TextAlign.Center) } }, confirmButton = { Button(onClick = { showInfoDialog = false }) { Text(text = if (currentLang == Lang.ES) "Cerrar" else "Close") } })

    if (showSettingsDialog) AlertDialog(onDismissRequest = { showSettingsDialog = false }, title = { Text(text = if (currentLang == Lang.ES) "Ajustes" else "Settings", color = Color.Black) }, text = { Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) { Row(verticalAlignment = Alignment.CenterVertically) { Text(text = if (currentLang == Lang.ES) "Modo Prueba" else "Test Mode", color = Color.Black); Switch(checked = isTestMode, onCheckedChange = { isTestMode = it }) }; HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)); Text(text = if (currentLang == Lang.ES) "Idioma" else "Language", color = Color.Black, fontWeight = FontWeight.Bold); Row { Button(onClick = { currentLang = Lang.ES; prefs.saveString(key = "app_lang", value = "ES") }, colors = ButtonDefaults.buttonColors(containerColor = if (currentLang == Lang.ES) darkRed else Color.Gray)) { Text(text = "ES") }; Spacer(modifier = Modifier.width(width = 8.dp)); Button(onClick = { currentLang = Lang.EN; prefs.saveString(key = "app_lang", value = "EN") }, colors = ButtonDefaults.buttonColors(containerColor = if (currentLang == Lang.EN) darkRed else Color.Gray)) { Text(text = "EN") } }; HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)); Text(text = "${if (currentLang == Lang.ES) "Tiempo RC" else "RC Time"}: $timeRC s", color = Color.Black); Slider(value = timeRC.toFloat(), onValueChange = { timeRC = it.toInt() }, valueRange = 30f..300f); Text(text = "${if (currentLang == Lang.ES) "Tiempo RI" else "RI Time"}: $timeRI s", color = Color.Black); Slider(value = timeRI.toFloat(), onValueChange = { timeRI = it.toInt() }, valueRange = 30f..300f); Button(onClick = { prefs.saveInt(key = "time_rc", value = timeRC); prefs.saveInt(key = "time_ri", value = timeRI) }) { Text(text = if (currentLang == Lang.ES) "Guardar" else "Save") }; HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)); Button(onClick = { showConfirmReset100 = true }, colors = ButtonDefaults.buttonColors(containerColor = darkRed), modifier = Modifier.fillMaxWidth()) { Text(text = if (currentLang == Lang.ES) "Reset Récord +100" else "Reset +100 Best", color = Color.White) }; Spacer(modifier = Modifier.height(height = 8.dp)); Button(onClick = { showConfirmResetRanking = true }, colors = ButtonDefaults.buttonColors(containerColor = darkRed), modifier = Modifier.fillMaxWidth()) { Text(text = if (currentLang == Lang.ES) "Reset Ranking VS" else "Reset VS Rank", color = Color.White) } } }, confirmButton = { Button(onClick = { showSettingsDialog = false }) { Text(text = if (currentLang == Lang.ES) "Salir" else "Exit") } })

    if (showStatsDialog) {
        var input by remember { mutableStateOf(value = "") }; AlertDialog(onDismissRequest = { showStatsDialog = false }, title = { Text(text = if (currentLang == Lang.ES) "Estadísticas" else "Statistics", color = Color.Black, fontWeight = FontWeight.Bold) }, text = { Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) { Text(text = if (currentLang == Lang.ES) "PRÁCTICA LIBRE" else "FREE PRACTICE", color = darkRed, fontWeight = FontWeight.Bold); Row(verticalAlignment = Alignment.CenterVertically) { TextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(weight = 1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), placeholder = { Text(text = "0") }); Button(onClick = { val add = input.toIntOrNull() ?: 0; registerArrows(count = add); input = "" }, modifier = Modifier.padding(start = 8.dp)) { Text(text = "+") } }; Spacer(modifier = Modifier.height(height = 16.dp)); Text(text = if (currentLang == Lang.ES) "HISTÓRICO DEL MES" else "MONTH HISTORY", color = darkRed, fontWeight = FontWeight.Bold); StatTable(data = ArcheryLogic.getThreeMonthStats(prefs = prefs.getPrefs()), darkRed = darkRed); Spacer(modifier = Modifier.height(height = 20.dp)); Text(text = "MATCH PLAY VS", color = darkRed, fontWeight = FontWeight.Bold); StatTable(data = ArcheryLogic.getMatchPlayStats(prefs = prefs.getPrefs(), es = currentLang == Lang.ES), darkRed = darkRed); Spacer(modifier = Modifier.height(height = 20.dp)); Text(text = if (currentLang == Lang.ES) "SEMANAS DEL MES" else "MONTH WEEKS", color = darkRed, fontWeight = FontWeight.Bold); StatTable(data = ArcheryLogic.getMonthlyWeekStats(prefs = prefs.getPrefs(), es = currentLang == Lang.ES), darkRed = darkRed) } }, confirmButton = { Button(onClick = { showStatsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = darkRed)) { Text(text = "OK", color = Color.White) } })
    }

    if (showConfirmReset100) AlertDialog(onDismissRequest = { showConfirmReset100 = false }, title = { Text(text = if (currentLang == Lang.ES) "Confirmar Reset" else "Confirm Reset", color = Color.Black) }, text = { Text(text = if (currentLang == Lang.ES) "¿Seguro de borrar el récord?" else "Sure to delete record?", color = Color.Black) }, confirmButton = { Button(onClick = { prefs.saveInt(key = "best_100", value = 0); bestArrows100 = 0; showConfirmReset100 = false }, colors = ButtonDefaults.buttonColors(containerColor = darkRed)) { Text(text = if (currentLang == Lang.ES) "Borrar" else "Delete") } }, dismissButton = { TextButton(onClick = { showConfirmReset100 = false }) { Text(text = if (currentLang == Lang.ES) "Cancelar" else "Cancel") } })

    if (showConfirmResetRanking) AlertDialog(onDismissRequest = { showConfirmResetRanking = false }, title = { Text(text = if (currentLang == Lang.ES) "Confirmar Reset" else "Confirm Reset", color = Color.Black) }, text = { Text(text = if (currentLang == Lang.ES) "¿Seguro de reiniciar todo?" else "Sure to reset all?", color = Color.Black) }, confirmButton = { Button(onClick = { mmrRank = 0; prefs.saveInt(key = "mmr_rank", value = 0); for (i in 1..4) { prefs.saveInt(key = "match_wins_lvl_$i", value = 0); prefs.saveInt(key = "match_loss_lvl_$i", value = 0) }; showConfirmResetRanking = false }, colors = ButtonDefaults.buttonColors(containerColor = darkRed)) { Text(text = if (currentLang == Lang.ES) "Reiniciar" else "Reset") } }, dismissButton = { TextButton(onClick = { showConfirmResetRanking = false }) { Text(text = if (currentLang == Lang.ES) "Cancelar" else "Cancel") } })
}