package com.headtech.mynoteplus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.headtech.mynoteplus.navigation.MyAppNavHost
import androidx.lifecycle.viewmodel.compose.viewModel
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel
import com.headtech.mynoteplus.viewmodel.NoteViewModel

@Composable
fun MyNotePlusApp() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val noteViewModel: NoteViewModel = viewModel()
    val filmNoteViewModel: FilmNoteViewModel = viewModel()

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    MyAppNavHost(navController = navController, noteViewModel = noteViewModel, filmNoteViewModel = filmNoteViewModel)

}



