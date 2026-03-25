package com.proyectotiro.archerytimer.logic

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object ArcheryLogic {
    fun generateOpponentList(lvl: Int): List<Int> {
        return when (lvl) {
            1 -> List(3) { Random.nextInt(5, 9) }
            2 -> List(3) { Random.nextInt(6, 10) }
            3 -> List(3) { Random.nextInt(7, 11) }
            else -> { val s = Random.nextInt(25, 31); listOf(s/3, s/3, s-(s/3)*2) }
        }
    }

    fun calculateStats(prefs: SharedPreferences, period: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        if (period == "day") return prefs.getInt(sdf.format(cal.time), 0)

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) cal.add(Calendar.DAY_OF_YEAR, -6)
        var total = 0
        repeat(7) {
            total += prefs.getInt(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time), 0)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return total
    }

    fun getMonthlyWeekStats(prefs: SharedPreferences, es: Boolean): List<Pair<String, String>> {
        val cal = Calendar.getInstance(); cal.set(Calendar.DAY_OF_MONTH, 1)
        val list = mutableListOf<Pair<String, String>>()
        repeat(5) { i ->
            var weekTotal = 0
            repeat(7) {
                weekTotal += prefs.getInt(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time), 0)
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            list.add((if(es) "Semana" else "Week") + " ${i+1}" to "$weekTotal")
        }
        return list
    }

    fun getThreeMonthStats(prefs: SharedPreferences): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        repeat(3) { i ->
            val cal = Calendar.getInstance(); cal.add(Calendar.MONTH, -i)
            val monthLabel = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time).uppercase()
            var monthTotal = 0
            val days = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            repeat(days) {
                monthTotal += prefs.getInt(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time), 0)
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            list.add(monthLabel to "$monthTotal")
        }
        return list
    }

    fun getMatchPlayStats(prefs: SharedPreferences, es: Boolean): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        for (lvl in 1..4) {
            val wins = prefs.getInt("match_wins_lvl_$lvl", 0)
            val losses = prefs.getInt("match_loss_lvl_$lvl", 0)
            val label = if(es) "Rival $lvl (G/P)" else "Opponent $lvl (W/L)"
            list.add(label to "$wins / $losses")
        }
        return list
    }
}