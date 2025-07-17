package com.headtech.mynoteplus.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel
import com.headtech.mynoteplus.viewmodel.NoteViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun HomeScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    filmNoteViewModel: FilmNoteViewModel
) {
    var selectedBottomTab by remember { mutableStateOf(0) }
    var selectedNoteTab by remember { mutableStateOf(0) }

    val currentVersion = "1.0"

    // State Firebase config
    var isMaintenance by remember { mutableStateOf(false) }
    var maintenanceMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }
    var updateUrl by remember { mutableStateOf("") }
    var updateChangelog by remember { mutableStateOf("") }

    // Ambil data dari Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        noteViewModel.startNoteListener()
        filmNoteViewModel.startFilmNoteListener()
        db.collection("app_config").document("status").get().addOnSuccessListener { doc ->
            val latestVersion = doc.getString("latest_version") ?: currentVersion
            val url = doc.getString("update_url") ?: ""
            val changelog = doc.getString("update_changelog") ?: ""
            val isMaint = doc.getBoolean("maintenance_mode") ?: false
            val maintMessage = doc.getString("maintenance_message") ?: "Aplikasi sedang dalam perbaikan."

            if (isMaint) {
                isMaintenance = true
                maintenanceMessage = maintMessage
                return@addOnSuccessListener
            }

            if (latestVersion != currentVersion) {
                showBanner = true
                updateUrl = url
                updateChangelog = changelog
            }
        }
    }

    // Tampilkan Maintenance kalau aktif
    if (isMaintenance) {
        MaintenanceScreen(maintenanceMessage)
        return
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

                1 -> ProfileScreen(navController = navController)
            }
        }

        if (showBanner) {
            Banner(
                message = "📢 Versi terbaru tersedia!",
                url = updateUrl,
                changelog = updateChangelog,
                onClose = { showBanner = false }
            )
        }
    }
}

@Composable
fun PersonalNotesContent(navController: NavController, noteViewModel: NoteViewModel) {
    val notes = noteViewModel.notes

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
fun Banner(message: String, url: String, changelog: String, onClose: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = changelog,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Update Sekarang")
                    }
                    TextButton(onClick = onClose) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🛠️ Maintenance Mode",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFBF360C)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
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
