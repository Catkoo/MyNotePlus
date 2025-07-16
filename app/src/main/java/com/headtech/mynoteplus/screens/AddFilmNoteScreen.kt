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
import com.google.firebase.auth.FirebaseAuth
import com.headtech.mynoteplus.model.FilmNote
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilmNoteScreen(
    navController: NavController,
    viewModel: FilmNoteViewModel
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    var title by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var media by remember { mutableStateOf("") }
    var lastEpisode by remember { mutableStateOf("1") }

    val statusOptions = listOf("Belum selesai", "Selesai")
    var selectedStatus by remember { mutableStateOf(statusOptions[0]) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tambah Film/Drama") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Film/Drama") },
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
                value = lastEpisode,
                onValueChange = { lastEpisode = it.filter { c -> c.isDigit() } },
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
                    if (title.isBlank() || year.isBlank() || lastEpisode.isBlank()) {
                        Toast.makeText(context, "Judul, Tahun & Episode wajib diisi", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val id = java.util.UUID.randomUUID().toString()
                    val note = FilmNote(
                        id = id,
                        title = title,
                        year = year,
                        media = if (media.isBlank()) null else media,
                        episodeWatched = lastEpisode.toIntOrNull() ?: 0,
                        isFinished = selectedStatus == "Selesai",
                        ownerUid = currentUser?.uid ?: "anonymous"
                    )
                    viewModel.addFilmNote(note)
                    Toast.makeText(context, "Catatan film ditambahkan", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = "Simpan")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan")
            }
        }
    }
}
