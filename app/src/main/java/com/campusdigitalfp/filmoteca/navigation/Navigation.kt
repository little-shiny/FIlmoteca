package com.campusdigitalfp.filmoteca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    // Observamos el usuario desde AuthViewModel (reactivo, no estático)
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination =
        if (currentUser != null) NavRoutes.LIST.abreviatura
        else NavRoutes.LOGIN.abreviatura

    NavHost(navController = navController, startDestination = startDestination) {

        composable(NavRoutes.LOGIN.abreviatura) {

            loginScreen(navController, authViewModel)
        }

        composable(NavRoutes.REGISTER.abreviatura) {
            registerScreen(navController, authViewModel)
        }

        composable(NavRoutes.LIST.abreviatura) {
            // Clave = uid del usuario. Si el uid cambia (login/logout/cambio de cuenta)
            // el efecto se re-ejecuta y recarga las películas del nuevo usuario.
            LaunchedEffect(currentUser?.uid) {
                if (currentUser != null) {
                    viewModel.loadFilms()
                }
            }
            filmListScreen(navController, viewModel = viewModel)
        }

        composable(NavRoutes.ABOUT.abreviatura) {
            AboutScreen(navController)
        }

        composable(NavRoutes.DETAILSFULL.abreviatura) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("peliculaId")
            val films by viewModel.films.collectAsState()
            val film = films.find { it.id == filmId }

            if (currentUser != null) {
                film?.let { filmDataScreen(navController, it, viewModel) }
            } else {
                loginScreen(navController, authViewModel)
            }
        }

        composable(NavRoutes.EDITFULL.abreviatura) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("peliculaId")
            val films by viewModel.films.collectAsState()
            val film = films.find { it.id == filmId }

            if (currentUser != null) {
                film?.let { filmEditScreen(navController, it, viewModel) }
            } else {
                loginScreen(navController, authViewModel)
            }
        }

        composable(NavRoutes.NEW.abreviatura) {
            if (currentUser != null) {
                NewFilmScreen(navController, viewModel)
            } else {
                loginScreen(navController, authViewModel)
            }
        }
    }
}

fun isUserLogged(): Boolean = FirebaseAuth.getInstance().currentUser != null