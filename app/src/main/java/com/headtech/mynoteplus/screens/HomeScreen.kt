package com.headtech.mynoteplus.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel
import com.headtech.mynoteplus.viewmodel.NoteViewModel
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.headtech.mynoteplus.model.Note
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color


@Composable
fun HomeScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    filmNoteViewModel: FilmNoteViewModel
) {
    var selectedBottomTab by remember { mutableStateOf(0) } // 0 = Home, 1 = Profil
    var selectedNoteTab by remember { mutableStateOf(0) } // 0 = Pribadi, 1 = Film

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            noteViewModel.startNoteListener()
            filmNoteViewModel.startFilmNoteListener()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedBottomTab == 0,
                    onClick = { selectedBottomTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedBottomTab == 1,
                    onClick = { selectedBottomTab = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        },
        floatingActionButton = {
            if (selectedBottomTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (selectedNoteTab == 0) {
                            navController.navigate("add_note")
                        } else {
                            navController.navigate("add_film_note")
                        }
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Tambah") },
                    text = { Text(if (selectedNoteTab == 0) "Tambah Catatan" else "Tambah Film") }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (selectedBottomTab) {
                0 -> {
                    // Home Tab dengan sub-tab pribadi/film
                    TabRow(selectedTabIndex = selectedNoteTab) {
                        Tab(selected = selectedNoteTab == 0, onClick = { selectedNoteTab = 0 }) {
                            Text("Pribadi")
                        }
                        Tab(selected = selectedNoteTab == 1, onClick = { selectedNoteTab = 1 }) {
                            Text("Film/Drama")
                        }
                    }
                    when (selectedNoteTab) {
                        0 -> PersonalNotesContent(navController, noteViewModel)
                        1 -> FilmNotesContent(navController, filmNoteViewModel)
                    }
                }

                1 -> {
                    // Profil Screen tampil di sini
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun PersonalNotesContent(navController: NavController, noteViewModel: NoteViewModel) {
    val notes = noteViewModel.notes
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        if (notes.isEmpty()) {
            Text("Belum ada catatan.")
        } else {
            notes.forEach { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("detail_note/${note.id}")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                        Text(note.content.take(40), style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilmNotesContent(navController: NavController, viewModel: FilmNoteViewModel) {
    val filmNotes = viewModel.filmNotes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daftar Film/Drama Kamu",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filmNotes.isEmpty()) {
            Text("Belum ada catatan film.")
        } else {
            filmNotes.forEach { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { navController.navigate("detail_film/${note.id}") }
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (note.isFinished) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🎬 ${note.title}", style = MaterialTheme.typography.titleMedium)
                            Text("Tahun: ${note.year}")
                            if (!note.media.isNullOrEmpty()) {
                                Text("Media: ${note.media}")
                            }
                            Text("Episode terakhir: ${note.episodeWatched}")
                            Text(
                                text = if (note.isFinished) "✅ Selesai" else "⏳ Belum selesai",
                                color = if (note.isFinished) Color(0xFF2E7D32) else Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
