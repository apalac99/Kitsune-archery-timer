package com.proyectotiro.archerytimer.logic

import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.Scorecard
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.sqrt

object ArcheryAI {

    fun analyzeScorecard(card: Scorecard, lang: Lang): String {
        val hasTargetData = card.targetMode && card.ends.flatMap { it.arrows }.any { it.x != 0f || it.y != 0f }

        return if (hasTargetData) {
            generateSpatialAnalysis(card = card, lang = lang)
        } else {
            generateNumericalAnalysis(card = card, lang = lang)
        }
    }

    private fun generateSpatialAnalysis(card: Scorecard, lang: Lang): String {
        val allArrows = card.ends.flatMap { it.arrows }.filter { it.x != 0f || it.y != 0f }
        if (allArrows.isEmpty()) return if (lang == Lang.ES) "Datos insuficientes." else "Insufficient data."

        // 1. Cálculo de Centroide (Punto medio del grupo)
        val avgX = allArrows.map { it.x }.average().toFloat()
        val avgY = allArrows.map { it.y }.average().toFloat()

        // 2. Tendencia Horaria (Reloj)
        // Usamos 0.5f, 0.5f como centro teórico de la diana
        val dx = avgX - 0.5f
        val dy = avgY - 0.5f
        var angle = atan2(dy.toDouble(), dx.toDouble()) * (180 / PI)
        angle += 90 // Ajustar para que el 0 esté arriba (las 12)
        if (angle < 0) angle += 360
        val clock = ((angle / 30).toInt() % 12).let { if (it == 0) 12 else it }

        // 3. Dispersión (Consistencia técnica)
        val distances = allArrows.map { arrow ->
            sqrt(((arrow.x - avgX) * (arrow.x - avgX) + (arrow.y - avgY) * (arrow.y - avgY)).toDouble())
        }
        val avgDistance = distances.average()
        val consistency = when {
            avgDistance < 0.05 -> if (lang == Lang.ES) "Excelente" else "Excellent"
            avgDistance < 0.12 -> if (lang == Lang.ES) "Buena" else "Good"
            else -> if (lang == Lang.ES) "Inconsistente" else "Inconsistent"
        }

        // 4. Análisis de Fatiga (Comparar primera mitad vs segunda mitad)
        val half = card.ends.size / 2
        val firstHalfAvg = if (half > 0) card.ends.take(half).flatMap { it.arrows }.map { it.value }.average() else 0.0
        val secondHalfAvg = if (half > 0) card.ends.takeLast(half).flatMap { it.arrows }.map { it.value }.average() else 0.0
        val fatigueDetected = (firstHalfAvg - secondHalfAvg) > 0.5

        // Construcción del reporte
        return buildString {
            append(if (lang == Lang.ES) "--- ANÁLISIS DE DIANA ---\n" else "--- TARGET ANALYSIS ---\n")
            append(if (lang == Lang.ES) "Tendencia: Las $clock en punto\n" else "Tendency: $clock o'clock\n")
            append(if (lang == Lang.ES) "Agrupación: $consistency\n" else "Grouping: $consistency\n")

            if (dx > 0.05 || dx < -0.05 || dy > 0.05 || dy < -0.05) {
                append(if (lang == Lang.ES) "Sugerencia: Ajustar mira " else "Suggestion: Adjust sight ")
                if (dy > 0.05) append(if (lang == Lang.ES) "Abajo " else "Down ") else if (dy < -0.05) append(if (lang == Lang.ES) "Arriba " else "Up ")
                if (dx > 0.05) append(if (lang == Lang.ES) "Derecha" else "Right") else if (dx < -0.05) append(if (lang == Lang.ES) "Izquierda" else "Left")
                append("\n")
            }

            if (fatigueDetected) {
                append(if (lang == Lang.ES) "Nota: Se detecta caída de rendimiento al final (Posible fatiga).\n" else "Note: Performance drop detected at the end (Possible fatigue).\n")
            }
        }
    }

    private fun generateNumericalAnalysis(card: Scorecard, lang: Lang): String {
        val allScores = card.ends.flatMap { it.arrows }.map { it.value }
        if (allScores.isEmpty()) return ""

        val average = allScores.average()
        val lowArrows = allScores.count { it < (average - 2) } // Flechas que se salen del promedio (Flyers)

        val half = card.ends.size / 2
        val firstHalfAvg = if (half > 0) card.ends.take(half).flatMap { it.arrows }.map { it.value }.average() else 0.0
        val secondHalfAvg = if (half > 0) card.ends.takeLast(half).flatMap { it.arrows }.map { it.value }.average() else 0.0
        val fatigueDetected = (firstHalfAvg - secondHalfAvg) > 0.5

        return buildString {
            append(if (lang == Lang.ES) "--- ANÁLISIS NUMÉRICO ---\n" else "--- NUMERICAL ANALYSIS ---\n")
            append(if (lang == Lang.ES) "Estabilidad: " else "Stability: ")
            append(if (lowArrows <= 2) (if (lang == Lang.ES) "Estable\n" else "Stable\n") else (if (lang == Lang.ES) "Errática\n" else "Erratic\n"))

            if (lowArrows > 0) {
                append(if (lang == Lang.ES) "Detectados $lowArrows 'Flyers' (Flechas fuera de patrón).\n" else "Detected $lowArrows 'Flyers' (Arrows out of pattern).\n")
            }

            if (fatigueDetected) {
                append(if (lang == Lang.ES) "Alerta: Tu promedio bajó en la segunda mitad del chequeo.\n" else "Alert: Your average dropped in the second half of the session.\n")
            } else {
                append(if (lang == Lang.ES) "Ritmo: Mantienes el nivel durante todo el chequeo.\n" else "Pace: You maintain the level throughout the session.\n")
            }
        }
    }
}