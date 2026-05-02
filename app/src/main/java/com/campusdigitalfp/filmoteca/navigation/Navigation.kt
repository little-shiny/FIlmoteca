package com.campusdigitalfp.filmoteca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.screens.*
import com.campusdigitalfp.filmoteca.viewmodel.AuthViewModel
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Enum NavRoutes define las rutas de navegación para la app
 * cada constante representa una pantalla y su abreviatura para la navegacion
 */
enum class NavRoutes(val abreviatura: String){
    LIST("list"),// Lista de peliculas
    ABOUT("about"), //acerca de
    DETAILSFULL("details/{peliculaId}"), // Detalles de una pelicula con la id dinamica
    DETAILS("details/"), // Base para la ruta de detalles
    EDITFULL("edit/{peliculaId}"), // edicion de una pelicula con id dinamica
    EDIT("edit/"), //Base para la ruta de edicion
    NEW("new"), //pantalla de nuevo habito
    LOGIN("login") // Pantalla de inicio de sesión
}

/**
 * Navigation administra la navegacion de la app definiendo la rutas y vinculandolas con las pantallas que corresponden
 * @param viewModel FilmViewModel proporciona los datos de las peliculas a las pantallas
 * @param authViewModel AuthViewModel que proporciona los datos de autenticación
 */
@Composable
fun Navigation(viewModel: FilmViewModel, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    // Define la pantalla de inicio dependiendo si el usuario está autenticado o no
    val startDestination =
        if(isUserLogged())
            "list"
        else
            "login"


    NavHost(navController = navController, startDestination = startDestination){

        // Ruta para la pantalla del login
        composable("login"){
            if(isUserLogged())
                filmListScreen(navController, viewModel = FilmViewModel())
            else
                loginScreen(navController, authViewModel)
        }

        // Ruta para la pantalla de registro
        composable("register"){
            registerScreen(navController, authViewModel)
        }

        // Ruta para la pantalla principal de la lista de peliculas
        composable(NavRoutes.LIST.abreviatura){
            filmListScreen(navController)
        }
        // Ruta para la pantalla de about
        composable(NavRoutes.ABOUT.abreviatura){
            AboutScreen(navController)
        }

        /**
         * Ruta para ver los detalles de una película en específico.
         * Extrae la ID de la pelicula desde los argumentos de la navegacion y la busca en la lista de peliculas
         * Si la peliocula existe muestra su pantalla, si no no hace nada
         */
        composable(NavRoutes.DETAILSFULL.abreviatura){backStackEntry ->
            // obtiene la id de la pelicula desde la url
            val filmId = backStackEntry.arguments?.getString("peliculaId")
            // obtiene la lista de peliculas en tiempo real
            val films by viewModel.films.collectAsState()
            // Busca la pelicula con la id obtenida
            val film = films.find{it.id == filmId}

            film?.let{
                if(isUserLogged())
                    filmListScreen(navController, it, FilmViewModel)
                else
                    loginScreen(navController, authViewModel)
            }
        }

        /**
         * Ruta para la pantalla de edición de una pelicula, obtiene el id de la pelicula y la busca en la lista.
         * si la pelicula exixste se muestra en la pantalla editfilmscreen pasando el viewmodel
         */
        composable(NavRoutes.EDITFULL.abreviatura){backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId")
            val films by viewModel.films.collectAsState()
            val film = films.find{ it.id == filmId}

            film?.let{
                if(isUserLogged()){
                    filmEditScreen(navController, it, FilmViewModel)
                }else{
                    loginScreen(navController, authViewModel)
                }
            }
        }

        // Ruta para la pantalla de creación de una nueva pelicula
        composable(NavRoutes.NEW.abreviatura){
            if(isUserLogged()){
                NewFilmScreen(navController, authViewModel)
            }
        }
    }
}

/**
 * Función que verifica si hay un usuario autenticado en Firebase Auth
 * @return true si hay un usuario autenticado
 */
fun isUserLogged():Boolean{
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    return currentUser != null
}

