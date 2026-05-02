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

/**
 * Enum NavRoutes define las rutas de navegación para la app
 * cada constante representa una pantalla y su abreviatura para la navegacion
 */
enum class NavRoutes(val abreviatura: String) {
    LIST("list"),
    ABOUT("about"),
    DETAILSFULL("details/{filmId}"),
    DETAILS("details/"),
    EDITFULL("edit/{filmId}"),
    EDIT("edit/"),
    NEW("new"),
    LOGIN("login")
}

/**
 * Navigation administra la navegacion de la app definiendo las rutas y vinculandolas con las pantallas
 * @param viewModel FilmViewModel proporciona los datos de las peliculas a las pantallas
 * @param authViewModel AuthViewModel que proporciona los datos de autenticación
 */
@Composable
fun Navigation(viewModel: FilmViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val startDestination =
        if (isUserLogged()) NavRoutes.LIST.abreviatura
        else NavRoutes.LOGIN.abreviatura

    NavHost(navController = navController, startDestination = startDestination) {

        // Ruta para la pantalla del login
        composable(NavRoutes.LOGIN.abreviatura) {
            if (isUserLogged())
            // Pasamos el viewModel recibido, no una instancia nueva
                filmListScreen(navController, viewModel = viewModel)
            else
                loginScreen(navController, authViewModel)
        }

        // Ruta para la pantalla de registro
        composable("register") {
            registerScreen(navController, authViewModel)
        }

        // Ruta para la pantalla principal de la lista de peliculas
        composable(NavRoutes.LIST.abreviatura) {
            // Pasamos el viewModel recibido
            filmListScreen(navController, viewModel = viewModel)
        }

        // Ruta para la pantalla de about
        composable(NavRoutes.ABOUT.abreviatura) {
            AboutScreen(navController)
        }

        /**
         * Ruta para ver los detalles de una película.
         * Extrae la ID desde los argumentos y la busca en la lista del viewModel.
         */
        composable(NavRoutes.DETAILSFULL.abreviatura) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId")
            val films by viewModel.films.collectAsState()
            val film = films.find { it.id == filmId }

            if (isUserLogged()) {
                film?.let {
                    // Pasamos la película encontrada y la instancia del viewModel recibida
                    filmDataScreen(navController, it, viewModel)
                }
            } else {
                loginScreen(navController, authViewModel)
            }
        }

        /**
         * Ruta para la pantalla de edición de una pelicula.
         */
        composable(NavRoutes.EDITFULL.abreviatura) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId") // Corregido de "filmId"
            val films by viewModel.films.collectAsState()
            val film = films.find { it.id == filmId }

            if (isUserLogged()) {
                film?.let {
                    filmEditScreen(navController, it, viewModel)
                }
            } else {
                loginScreen(navController, authViewModel)
            }
        }

        // Ruta para la pantalla de creación de una nueva pelicula
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
 * Función que verifica si hay un usuario autenticado en Firebase Auth
 * @return true si hay un usuario autenticado
 */
fun isUserLogged(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
}