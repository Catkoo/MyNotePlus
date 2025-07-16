package com.headtech.mynoteplus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.headtech.mynoteplus.screens.*
import com.headtech.mynoteplus.viewmodel.FilmNoteViewModel
import com.headtech.mynoteplus.viewmodel.NoteViewModel

@Composable
fun MyAppNavHost(navController: NavHostController, noteViewModel: NoteViewModel, filmNoteViewModel: FilmNoteViewModel) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController, noteViewModel, filmNoteViewModel) }
        composable("profile") { ProfileScreen(navController) }
        composable("add_note") { AddNoteScreen(navController, noteViewModel) }
        composable("add_film_note") { AddFilmNoteScreen(navController, filmNoteViewModel) }
        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            EditNoteScreen(navController, noteId, noteViewModel)
        }
        composable("detail_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            DetailNoteScreen(navController, noteId, noteViewModel)
        }
        composable("detail_film/{filmId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("filmId") ?: return@composable
            DetailFilmNoteScreen(navController, id, filmNoteViewModel)
        }
        composable("edit_film_note/{filmId}") { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId") ?: ""
            EditFilmNoteScreen(navController, filmId, filmNoteViewModel)
        }

    }
}
