package com.proyectotiro.archerytimer.data

// --- MODELO PARA UNA FLECHA INDIVIDUAL ---
data class ArrowImpact(
    val value: Int,
    val isX: Boolean,
    val x: Float = 0f,
    val y: Float = 0f
)

// --- MODELO PARA UNA TANDA (END) ---
data class ArcheryEnd(
    val arrows: List<ArrowImpact>
)

// --- MODELO PARA UN CHEQUEO COMPLETO ---
data class Scorecard(
    val id: Long = System.currentTimeMillis(),
    val date: String,
    val location: String,
    val weatherIcon: String,
    val targetMode: Boolean, // NUEVO: Guarda si se usó diana
    val ends: List<ArcheryEnd>,
    val totalScore: Int,
    val avg: Double,
    val countX: Int,
    val count10: Int,
    val count9: Int
)

// --- MODELO PARA EL CALENDARIO ---
data class ArcheryEvent(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val date: String
)