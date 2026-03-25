package com.proyectotiro.archerytimer.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyectotiro.archerytimer.Lang
import com.proyectotiro.archerytimer.data.ArcheryEvent

@Composable
fun CalendarScreen(
    events: List<ArcheryEvent>,
    lang: Lang,
    darkRed: Color,
    onAddEvent: (String, String) -> Unit,
    onDeleteEvent: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .padding(all = 16.dp)
        ) {
            Text(
                text = if (lang == Lang.ES) "CALENDARIO DE EVENTOS" else "EVENT CALENDAR",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (events.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (lang == Lang.ES) "No hay eventos programados" else "No scheduled events",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(events) { event ->
                        EventItem(
                            event = event,
                            darkRed = darkRed,
                            onDelete = { onDeleteEvent(event.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddEventDialog(
                lang = lang,
                onDismiss = { showAddDialog = false },
                onConfirm = { title, date ->
                    onAddEvent(title, date)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun EventItem(
    event: ArcheryEvent,
    darkRed: Color,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(text = event.date, color = darkRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = event.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun AddEventDialog(
    lang: Lang,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (lang == Lang.ES) "Nuevo Evento" else "New Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                TextField(value = title, onValueChange = { title = it }, label = { Text(text = if (lang == Lang.ES) "Evento" else "Event") })
                TextField(value = date, onValueChange = { date = it }, label = { Text(text = if (lang == Lang.ES) "Fecha (ej: 15 Mar)" else "Date (eg: Mar 15)") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, date) }) {
                Text(text = if (lang == Lang.ES) "Guardar" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (lang == Lang.ES) "Cancelar" else "Cancel")
            }
        }
    )
}