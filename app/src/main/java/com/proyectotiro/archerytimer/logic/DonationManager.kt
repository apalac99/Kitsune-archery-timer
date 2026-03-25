package com.proyectotiro.archerytimer.logic

import android.content.Context
import android.widget.Toast
import com.proyectotiro.archerytimer.Lang

class DonationManager(private val context: Context) {

    /**
     * Inicia el proceso de donación.
     * En el futuro, aquí se integrará BillingClient de Google Play.
     */
    fun startDonationProcess(lang: Lang, onFinish: (Boolean) -> Unit) {
        // Simulamos una pequeña espera de procesamiento
        val msg = if (lang == Lang.ES)
            "Conectando con Google Play Store (Simulación)..."
        else
            "Connecting to Google Play Store (Simulation)..."

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        // Simulamos éxito tras 2 segundos
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val successMsg = if (lang == Lang.ES)
                "¡Gracias por tu apoyo, arquero!"
            else
                "Thanks for your support, archer!"

            Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
            onFinish(true)
        }, 2000)
    }
}