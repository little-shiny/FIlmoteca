package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.common.FilmDataSource
import com.campusdigitalfp.filmoteca.common.barraSuperior

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController, atras = true, onBackResult = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("result", Activity.RESULT_CANCELED)
                })
        }) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = FilmDataSource.films) { film ->
                Text(
                    text = film.title ?: "<Sin Título>", // manejo del null en los campos
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun mainScreenPreview(){
    val navController = rememberNavController()

    MainScreen(
        navController = navController
    )
}
