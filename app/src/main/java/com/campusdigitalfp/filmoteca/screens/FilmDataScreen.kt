package com.campusdigitalfp.filmoteca.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.common.FilmImageLarge
import com.campusdigitalfp.filmoteca.common.barraSuperior
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

@Composable
fun filmDataScreen(
    navController: NavHostController,
    film: Film,
    viewModel: FilmViewModel = viewModel()
) {
    val films by viewModel.films.collectAsState()
    val currentFilm = films.find { it.id == film.id } ?: film

    FilmotecaTheme {
        Scaffold(
            topBar = { barraSuperior(navController = navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen de la película (local o vacía)
                FilmImageLarge(
                    imageUrl = currentFilm.imageUrl,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = currentFilm.title.ifEmpty { "<Sin Título>" },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = currentFilm.comments,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )

                // Captura de foto
                CameraCapture(film = currentFilm, viewModel = viewModel)

                Button(
                    onClick = { navController.navigate(NavRoutes.EDIT.abreviatura + currentFilm.id) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) { Text("Editar") }

                Button(
                    onClick = {
                        viewModel.deleteFilm(currentFilm.id)
                        navController.navigate(NavRoutes.LIST.abreviatura)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) { Text("Eliminar") }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Volver") }
            }
        }
    }
}