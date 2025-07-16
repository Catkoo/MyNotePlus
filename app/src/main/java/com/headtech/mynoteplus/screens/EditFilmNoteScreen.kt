package com.headtech.mynoteplus.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel
import com.headtech.mynoteplus.model.FilmNote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFilmNoteScreen(
    navController: NavController,
    filmId: String,
    viewModel: FilmNoteViewModel
) {
    val context = LocalContext.current
    var note by remember { mutableStateOf<FilmNote?>(null) }

    var title by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var media by remember { mutableStateOf("") }
    var episodeWatched by remember { mutableStateOf("0") }

    val statusOptions = listOf("Belum selesai", "Selesai")
    var selectedStatus by remember { mutableStateOf(statusOptions[0]) }
    var expanded by remember { mutableStateOf(false) }

    // Ambil data dari Firestore
    LaunchedEffect(filmId) {
        viewModel.getFilmNoteById(filmId) {
            if (it != null) {
                note = it
                title = it.title
                year = it.year
                media = it.media ?: ""
                episodeWatched = it.episodeWatched.toString()
                selectedStatus = if (it.isFinished) "Selesai" else "Belum selesai"
            } else {
                Toast.makeText(context, "Catatan tidak ditemukan", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
    }

    note?.let {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Edit Film/Drama") })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Tahun") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = media,
                    onValueChange = { media = it },
                    label = { Text("Media (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = episodeWatched,
                    onValueChange = { episodeWatched = it.filter { c -> c.isDigit() } },
                    label = { Text("Episode terakhir ditonton") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedStatus,
                        onValueChange = {},
                        label = { Text("Status") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    selectedStatus = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (title.isBlank() || year.isBlank() || episodeWatched.isBlank()) {
                            Toast.makeText(context, "Harap isi semua field", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val updated = it.copy(
                            title = title,
                            year = year,
                            media = if (media.isBlank()) null else media,
                            episodeWatched = episodeWatched.toIntOrNull() ?: 0,
                            isFinished = selectedStatus == "Selesai"
                        )

                        viewModel.updateFilmNote(updated)
                        Toast.makeText(context, "Catatan diperbarui", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("edit_film_note/${filmId}") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Simpan")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}
