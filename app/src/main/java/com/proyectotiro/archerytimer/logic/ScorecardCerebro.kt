package com.proyectotiro.archerytimer.logic

import com.proyectotiro.archerytimer.data.ArcheryEnd
import com.proyectotiro.archerytimer.data.Scorecard
import java.text.SimpleDateFormat
import java.util.*

object ScorecardCerebro {

    fun calculateTotalScore(ends: List<ArcheryEnd>): Int {
        return ends.sumOf { end -> end.arrows.sumOf { it.value } }
    }

    fun calculateAverage(ends: List<ArcheryEnd>): Double {
        val totalArrows = ends.sumOf { it.arrows.size }
        if (totalArrows == 0) return 0.0
        return calculateTotalScore(ends = ends).toDouble() / totalArrows
    }

    fun countValue(ends: List<ArcheryEnd>, value: Int, onlyX: Boolean): Int {
        var count = 0
        ends.forEach { end ->
            count += end.arrows.count {
                if (onlyX) it.isX else (it.value == value && !it.isX)
            }
        }
        return count
    }
}