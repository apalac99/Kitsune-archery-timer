package com.proyectotiro.archerytimer.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyectotiro.archerytimer.AppMode
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.ui.components.*

@Composable
fun LanguageSelectScreen(onSelect: (Lang) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "SELECT LANGUAGE", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(height = 30.dp))
        Button(onClick = { onSelect(Lang.ES) }, modifier = Modifier.size(width = 200.dp, height = 80.dp)) {
            Text(text = "ESPAÑOL", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(height = 16.dp))
        Button(onClick = { onSelect(Lang.EN) }, modifier = Modifier.size(width = 200.dp, height = 80.dp)) {
            Text(text = "ENGLISH", fontSize = 20.sp)
        }
    }
}

@Composable
fun WelcomeScreen(lang: Lang, onSelect: (AppMode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == Lang.ES) "BIENVENIDO" else "WELCOME",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(height = 20.dp))
        val btnModifier = Modifier.fillMaxWidth(fraction = 0.7f).padding(vertical = 4.dp)

        Button(onClick = { onSelect(AppMode.RC) }, modifier = btnModifier) {
            Text(text = if (lang == Lang.ES) "Clasificación (RC)" else "Classification (RC)")
        }
        Button(onClick = { onSelect(AppMode.RI) }, modifier = btnModifier) {
            Text(text = if (lang == Lang.ES) "Individual (RI)" else "Individual (RI)")
        }
        Button(onClick = { onSelect(AppMode.PLUS_100) }, modifier = btnModifier) {
            Text(text = if (lang == Lang.ES) "Juego +100" else "Game +100")
        }
        Button(onClick = { onSelect(AppMode.MATCH_PLAY) }, modifier = btnModifier) {
            Text(text = "Match Play VS")
        }
        Button(
            onClick = { onSelect(AppMode.SCORE_HISTORY) },
            modifier = btnModifier,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))
        ) {
            Text(text = if (lang == Lang.ES) "PLANILLAS / CHEQUEOS" else "SCORECARDS")
        }
        Button(
            onClick = { onSelect(AppMode.CALENDAR) },
            modifier = btnModifier,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))
        ) {
            Text(text = if (lang == Lang.ES) "CALENDARIO" else "CALENDAR")
        }
    }
}

@Composable
fun TimerScreen(
    end: Int,
    time: Int,
    isPrep: Boolean,
    isShoot: Boolean,
    blink: Boolean,
    lang: Lang,
    darkRed: Color,
    onStart: () -> Unit,
    onReset: () -> Unit,
    onFinishNow: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${if (lang == Lang.ES) "Tanda" else "End"} - $end",
            color = Color.Yellow,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Box(modifier = Modifier.height(height = 180.dp), contentAlignment = Alignment.Center) {
            if (isPrep || isShoot) {
                val displayColor = if (isPrep && blink) Color.Transparent else if (isPrep) Color.Yellow else Color.Green
                Text(
                    text = time.toString(),
                    color = displayColor,
                    fontSize = 160.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isPrep || isShoot) {
                Button(
                    onClick = onFinishNow,
                    modifier = Modifier.size(width = 200.dp, height = 80.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkRed)
                ) {
                    Text(text = if (lang == Lang.ES) "TERMINAR" else "FINISH", fontSize = 24.sp)
                }
            } else {
                Button(
                    onClick = onStart,
                    modifier = Modifier.size(width = 200.dp, height = 80.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(text = if (lang == Lang.ES) "INICIO" else "START", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.height(height = 10.dp))
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(containerColor = darkRed)
                ) {
                    Text(text = if (lang == Lang.ES) "Reiniciar" else "Reset")
                }
            }
        }
    }
}

@Composable
fun Plus100Screen(score: Int, arrows: Int, best: Int, darkRed: Color, lang: Lang, onScore: (Int) -> Unit, onReset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${if (lang == Lang.ES) "Récord" else "Best"}: ${if (best == 0) "-" else best} | ${if (lang == Lang.ES) "Flechas" else "Arrows"}: $arrows",
            color = Color.Cyan
        )
        Text(
            text = score.toString(),
            color = if (score >= 100) Color.Green else Color.White,
            fontSize = 120.sp,
            fontWeight = FontWeight.Black
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TargetBtn(label = "10", value = 3, onClick = onScore)
                TargetBtn(label = "9", value = 2, onClick = onScore)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TargetBtn(label = "8", value = 1, onClick = onScore)
                TargetBtn(label = "<=6", value = -1, onClick = onScore)
            }
        }
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = darkRed)
        ) {
            Text(text = if (lang == Lang.ES) "Reiniciar" else "Reset")
        }
    }
}

@Composable
fun MatchPlayScreen(
    mmr: Int,
    lvl: Int,
    pSets: Int,
    oSets: Int,
    oArrows: List<Int>,
    pArrows: List<Int>,
    status: String,
    time: Int,
    isPrep: Boolean,
    isShoot: Boolean,
    blink: Boolean,
    lang: Lang,
    darkRed: Color,
    onStartTurn: () -> Unit,
    onArrowClick: (Int) -> Unit,
    onNextMatch: () -> Unit,
    onFinishNow: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (lang == Lang.ES) "TÚ" else "YOU", color = Color.White)
                Text(text = pSets.toString(), fontSize = 50.sp, color = Color.Green, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (lang == Lang.ES) "RIVAL" else "RIVAL", color = Color.White)
                Text(text = oSets.toString(), fontSize = 50.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${if (lang == Lang.ES) "Rival" else "Opponent"} $lvl/4", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                oArrows.forEachIndexed { index, arrow ->
                    if (index < pArrows.size || pSets >= 6 || oSets >= 6 || status == (if (lang == Lang.ES) "Siguiente Set" else "Next Set")) {
                        ArrowBox(score = arrow)
                    } else {
                        Box(modifier = Modifier.padding(all = 2.dp).size(size = 40.dp).background(color = Color.DarkGray, shape = RoundedCornerShape(size = 4.dp)))
                    }
                }
                if (pArrows.size == 3 || pSets >= 6 || oSets >= 6 || status == (if (lang == Lang.ES) "Siguiente Set" else "Next Set")) {
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Box(modifier = Modifier.background(color = Color.LightGray, shape = RoundedCornerShape(size = 4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(text = oArrows.sum().toString(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (lang == Lang.ES) "Tú" else "You", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                (0..2).forEach { index ->
                    if (index < pArrows.size) {
                        ArrowBox(score = pArrows[index])
                    } else {
                        Box(modifier = Modifier.padding(all = 2.dp).size(size = 40.dp).background(color = Color.DarkGray, shape = RoundedCornerShape(size = 4.dp)))
                    }
                }
                if (pArrows.size == 3) {
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Box(modifier = Modifier.background(color = Color.LightGray, shape = RoundedCornerShape(size = 4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(text = pArrows.sum().toString(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        if (isShoot || isPrep) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = time.toString(),
                    color = if (isPrep && blink) Color.Transparent else if (isPrep) Color.Yellow else Color.Green,
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Black
                )
                Button(onClick = onFinishNow, colors = ButtonDefaults.buttonColors(containerColor = darkRed)) {
                    Text(text = if (lang == Lang.ES) "TERMINAR" else "FINISH")
                }
            }
        } else if (status == (if (lang == Lang.ES) "Anotar" else "Score")) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) { listOf(10, 9, 8, 7, 6).forEach { TargetBtn(label = it.toString(), value = it, onClick = onArrowClick, size = 60.dp) } }
                Row(verticalAlignment = Alignment.CenterVertically) { listOf(5, 4, 3, 2, 1).forEach { TargetBtn(label = it.toString(), value = it, onClick = onArrowClick, size = 60.dp) } }
                TargetBtn(label = "M", value = 0, onClick = onArrowClick, size = 70.dp)
            }
        } else Text(text = status, color = Color.Yellow, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        if (pSets >= 6 || oSets >= 6) {
            Button(onClick = onNextMatch, modifier = Modifier.fillMaxWidth()) {
                Text(text = if (lang == Lang.ES) "SIGUIENTE / FINALIZAR" else "NEXT / FINISH")
            }
        } else if (status == (if (lang == Lang.ES) "En espera" else "Waiting") || status == (if (lang == Lang.ES) "Siguiente Set" else "Next Set")) {
            Button(
                onClick = onStartTurn,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(text = if (lang == Lang.ES) "INICIAR TURNO" else "START TURN")
            }
        }
    }
}