package com.campusdigitalfp.filmoteca.navigation

import com.campusdigitalfp.filmoteca.screens.*

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "FilmListScreen"){
        composable("FilmListScreen"){
            filmListScreen(navController)
        }
        composable("FilmDataScreen"){
            filmDataScreen(navController)
        }
        composable("FilmEditScreen"){
            filmEditScreen(navController)
        }
        composable("AboutScreen"){
            AboutScreen(navController)
        }
    }
}

