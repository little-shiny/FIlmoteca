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

    NavHost(navController = navController, startDestination = "FilmListScreen"){
        composable("FilmListScreen"){
            filmListScreen(navController)
        }
        composable(
            route = "filmEdit/{filmIndex}",
            arguments = listOf(
                navArgument("filmIndex"){type = NavType.IntType}
            )
        ){ backStackEntry ->
            val index = backStackEntry.arguments?.getInt("FilmIndex")
            filmEditScreen(navController, index)
        }
        composable("AboutScreen"){
            AboutScreen(navController)
        }
        // Pantalla FilmDataScreen con parámetro
        // **Ahora index para poder navegar por el array de las peliculas**
        composable(
            route = "filmData/{filmIndex}",
            arguments = listOf(
                navArgument("filmIndex"){
                    type = NavType.IntType
                }
            )
        ){ backStackEntry ->
            val index =
                backStackEntry.arguments?.getInt("filmIndex")
            filmDataScreen(navController, index)
        }
    }
}

