package com.headtech.mynoteplus.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.headtech.mynoteplus.model.FilmNote
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailFilmNoteScreen(
    navController: NavController,
    filmId: String,
    viewModel: FilmNoteViewModel
) {
    val context = LocalContext.current
    var filmNote by remember { mutableStateOf<FilmNote?>(null) }

    // Ambil data dari Firestore
    LaunchedEffect(filmId) {
        viewModel.getFilmNoteById(filmId) {
            filmNote = it
        }
    }

    filmNote?.let { note ->
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Detail Film/Drama") })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text("Judul: ${note.title}", style = MaterialTheme.typography.headlineSmall)
                Text("Tahun: ${note.year}")
                note.media?.let { Text("Media: $it") }
                Text("Episode terakhir: ${note.episodeWatched}")
                Text("Status: ${if (note.isFinished) "✅ Selesai ditonton" else "⏳ Belum selesai"}")

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Button(
                        onClick = {
                            viewModel.deleteFilmNote(note)
                            Toast.makeText(context, "Catatan film dihapus", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            navController.navigate("edit_film_note/${note.id}")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }
                }
            }
        }
    } ?: run {
        // Saat data belum tersedia
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Memuat data...")
        }
    }
}
