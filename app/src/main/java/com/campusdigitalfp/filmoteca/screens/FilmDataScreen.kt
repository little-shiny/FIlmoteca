package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.FilmDataSource
import com.campusdigitalfp.filmoteca.common.barraSuperior
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Pantalla que muestra los detalles de una película
 *
 * Permite ver al usuario la información, editarla o eliminarla
 *
 * @param navController Controlador de navegación
 * @param film Objeto que contiene los datos de la pelicula
 * @param viewModel ViewModel para gestionar las películas
 */
@Composable
fun filmDataScreen(
    navController: NavHostController,
    film: Film,
    viewModel: FilmViewModel = viewModel()
) {
    FilmotecaTheme {
        Scaffold(
            topBar = { barraSuperior(navController = navController,) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Título de la película
                Text(
                    text = film.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 9.dp),
                    textAlign = TextAlign.Center
                )
                //descripción de la película
                Text(
                    text = film.comments,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )

                //Botón para editar la película
                Button(
                    onClick = { navController.navigate(NavRoutes.EDIT.abreviatura + film.id) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(text = "Editar")
                }

                // Botón para eliminar la película
                Button(
                    onClick = {
                        film.id.let { viewModel.deleteFilm(it) }
                        navController.navigate(NavRoutes.LIST.abreviatura)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(text = "Eliminar")
                }
                // Botón para volver atrás
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Volver")
                }
            }
        }
    }
}





