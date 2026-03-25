package com.proyectotiro.archerytimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.proyectotiro.archerytimer.Lang

@Composable
fun DonationPopup(
    lang: Lang,
    darkRed: Color,
    onDismiss: () -> Unit,
    onDonate: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(width = 2.dp, color = Color.Yellow, shape = RoundedCornerShape(size = 16.dp)),
            shape = RoundedCornerShape(size = 16.dp),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier.padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título con icono
                Text(
                    text = "❤️",
                    fontSize = 40.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (lang == Lang.ES) "REGÁLAME UNA FLECHA" else "BUY ME AN ARROW",
                    color = darkRed,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(height = 16.dp))

                // Mensaje Personalizado
                Text(
                    text = if (lang == Lang.ES) {
                        "Kitsune Archery Timer es una herramienta desarrollada por Alejandro Palacios (Arquero Master - Club Kitsune, La Ceja - Colombia) para la comunidad. Sin publicidad y 100% gratuita.\n\nTu apoyo ayuda a mantener y mejorar este proyecto.\n\n¡Gracias por disparar conmigo!"
                    } else {
                        "Kitsune Archery Timer is a tool developed by Alejandro Palacios (Recurve Archer in La Ceja - Colombia) for the community. Ad-free and 100% free.\n\nYour support helps maintain and improve this project.\n\nThanks for shooting with me!"
                    },
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(height = 24.dp))

                // Botón de Donación (1 USD)
                Button(
                    onClick = onDonate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Verde éxito
                    shape = RoundedCornerShape(size = 8.dp)
                ) {
                    Text(
                        text = if (lang == Lang.ES) "Donar 1 USD" else "Donate 1 USD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(height = 12.dp))

                // Botón de Cierre
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == Lang.ES) "CERRAR" else "CLOSE",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}