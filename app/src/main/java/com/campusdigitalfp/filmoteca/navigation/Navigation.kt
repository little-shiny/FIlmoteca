package com.campusdigitalfp.filmoteca.navigation

import com.campusdigitalfp.filmoteca.screens.*

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "MainScreen"){
        composable("MainScreen"){
            MainScreen(navController)
        }
        composable("FilmListScreen"){
            filmListScreen(navController)
        }
        composable("FilmEditScreen"){
            filmEditScreen(navController)
        }
        composable("AboutScreen"){
            AboutScreen(navController)
        }
        // Pantalla FilmDataScreen con parámetro
        composable(
            route = "filmData/{filmName}",
            arguments = listOf(
                navArgument("filmName"){
                    type = NavType.StringType
                }
            )
        ){ backStackEntry ->
            val filmName =
                backStackEntry.arguments?.getString("filmName")
            filmDataScreen(navController, filmName)
        }
    }
}

