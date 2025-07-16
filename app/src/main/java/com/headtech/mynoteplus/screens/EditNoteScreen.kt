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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.headtech.mynoteplus.model.Note
import com.headtech.mynoteplus.viewmodel.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavController,
    noteId: String,
    viewModel: NoteViewModel
) {

    var note by remember { mutableStateOf<Note?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Panggil getNoteById dari Firebase
    LaunchedEffect(noteId) {
        viewModel.getNoteById(noteId) {
            note = it
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        return
    }

    if (note == null) {
        Text("Catatan tidak ditemukan.")
        return
    }

    var title by remember { mutableStateOf(note!!.title) }
    var content by remember { mutableStateOf(note!!.content) }
    val context = LocalContext.current

    Scaffold(

        topBar = {
            TopAppBar(title = { Text("Edit Catatan") })
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
                label = { Text("Judul") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi Catatan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        Toast.makeText(context, "Isi semua field", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val updatedNote = note!!.copy(
                        title = title,
                        content = content
                    )

                    viewModel.updateNote(updatedNote)
                    Toast.makeText(context, "Catatan diperbarui", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
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