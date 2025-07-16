package com.headtech.mynoteplus.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.headtech.mynoteplus.viewmodel.NoteViewModel
import com.headtech.mynoteplus.model.Note
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNoteScreen(
    navController: NavController,
    noteId: String,
    viewModel: NoteViewModel
) {
    val context = LocalContext.current
    var note by remember { mutableStateOf<Note?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(noteId) {
        viewModel.getNoteById(noteId) {
            note = it
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Catatan tidak ditemukan.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Catatan") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Judul: ${note!!.title}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Isi: ${note!!.content}")
            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Button(onClick = {
                    viewModel.removeNote(note!!)
                    Toast.makeText(context, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) {
                    Text("Hapus")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = {
                    navController.navigate("edit_note/${note!!.id}")
                }) {
                    Text("Edit")
                }
            }
        }
    }
}
