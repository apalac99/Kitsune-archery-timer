package com.proyectotiro.archerytimer.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.ArcheryEvent
import java.util.*

@Composable
fun CalendarPopup(
    events: List<ArcheryEvent>,
    lang: Lang,
    darkRed: Color,
    onDismiss: () -> Unit,
    onAddEvent: (String, String) -> Unit,
    onDeleteEvent: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(text = if(lang == Lang.ES) "EVENTOS" else "EVENTS", color = Color.Black, fontWeight = FontWeight.Bold)
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = darkRed)
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                if (events.isEmpty()) {
                    Text(text = if(lang == Lang.ES) "Sin eventos" else "No events", color = Color.Gray)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(events) { event ->
                            Row(
                                Modifier.fillMaxWidth().border(1.dp, Color.LightGray, MaterialTheme.shapes.small).padding(8.dp),
                                Arrangement.SpaceBetween, Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(text = event.title, color = Color.Black, fontWeight = FontWeight.Bold)
                                    Text(text = event.date, color = darkRed, fontSize = 12.sp)
                                }
                                IconButton(onClick = { onDeleteEvent(event.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = if(lang == Lang.ES) "CERRAR" else "CLOSE", color = darkRed) }
        }
    )

    if (showAddDialog) {
        AddEventDialog(
            lang = lang,
            darkRed = darkRed,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, date ->
                onAddEvent(title, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddEventDialog(
    lang: Lang,
    darkRed: Color,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> date = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if(lang == Lang.ES) "Nuevo Evento" else "New Event", color = Color.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = title, onValueChange = { title = it }, label = { Text(if(lang==Lang.ES) "Nombre" else "Name") })
                Button(
                    onClick = { datePickerDialog.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if(date.isEmpty()) (if(lang==Lang.ES) "Elegir Fecha" else "Pick Date") else date, color = Color.Black)
                }
            }
        },
        confirmButton = {
            Button(onClick = { if(title.isNotEmpty() && date.isNotEmpty()) onConfirm(title, date) }, colors = ButtonDefaults.buttonColors(darkRed)) {
                Text(text = if(lang==Lang.ES) "Añadir" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = if(lang==Lang.ES) "Cancelar" else "Cancel") }
        }
    )
}