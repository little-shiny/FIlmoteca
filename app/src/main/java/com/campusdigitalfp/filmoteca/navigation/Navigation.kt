package com.campusdigitalfp.filmoteca.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list"){
        //todo
    }
}

