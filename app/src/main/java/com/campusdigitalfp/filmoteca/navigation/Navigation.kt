package com.campusdigitalfp.filmoteca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.screens.*
import com.campusdigitalfp.filmoteca.viewmodel.AuthViewModel
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel
import com.google.firebase.auth.FirebaseAuth

enum class NavRoutes(val abreviatura: String) {
    LIST("list"),
    ABOUT("about"),
    DETAILSFULL("details/{peliculaId}"),
    DETAILS("details/"),
    EDITFULL("edit/{peliculaId}"),
    EDIT("edit/"),
    NEW("new"),
    LOGIN("login"),
    REGISTER("register")
}

@Composable
fun Navigation(viewModel: FilmViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    // La sesión persiste mientras FirebaseAuth tenga currentUser != null
    val startDestination =
        if (isUserLogged()) NavRoutes.LIST.abreviatura
        else NavRoutes.LOGIN.abreviatura

    NavHost(navController = navController, startDestination = startDestination) {

        composable(NavRoutes.LOGIN.abreviatura) {
            // Si ya hay sesión activa, saltamos directamente a la lista
            if (isUserLogged())
                filmListScreen(navController, viewModel = viewModel)
            else
                loginScreen(navController, authViewModel)
        }

        composable(NavRoutes.REGISTER.abreviatura) {
            registerScreen(navController, authViewModel)
        }

        composable(NavRoutes.LIST.abreviatura) {
            // Recarga las películas del usuario autenticado cada vez que se llega aquí
            viewModel.loadFilms()
            filmListScreen(navController, viewModel = viewModel)
        }

        composable(NavRoutes.ABOUT.abreviatura) {
            AboutScreen(navController)
        }

        composable(NavRoutes.DETAILSFULL.abreviatura) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("peliculaId")
            val films by viewModel.films.collectAsState()
            val film = films.find { it.id == filmId }

            if (isUserLogged()) {
                film?.let { filmDataScreen(navController, it, viewModel) }
            } else {
                loginScreen(navController, authViewModel)
            }
        }

        composable(NavRoutes.EDITFULL.abreviatura) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("peliculaId")
            val films by viewModel.films.collectAsState()
            val film = films.find { it.id == filmId }

            if (isUserLogged()) {
                film?.let { filmEditScreen(navController, it, viewModel) }
            } else {
                loginScreen(navController, authViewModel)
            }
        }

        composable(NavRoutes.NEW.abreviatura) {
            if (isUserLogged()) {
                NewFilmScreen(navController, viewModel)
            } else {
                loginScreen(navController, authViewModel)
            }
        }
    }
}

/**
 * Verifica si hay un usuario autenticado en Firebase.
 * La sesión persiste automáticamente entre reinicios de app gracias a Firebase Auth.
 */
fun isUserLogged(): Boolean = FirebaseAuth.getInstance().currentUser != null