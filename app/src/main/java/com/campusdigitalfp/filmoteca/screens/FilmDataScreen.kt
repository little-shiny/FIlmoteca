package com.campusdigitalfp.filmoteca.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.barraSuperior
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Pantalla de detalle de una película.
 *
 * Muestra la imagen (si existe), el título, los comentarios y los botones
 * para editar, eliminar y volver. También permite capturar una nueva foto
 * desde la cámara y asociarla a la película.
 */
@Composable
fun filmDataScreen(
    navController: NavHostController,
    film: Film,
    viewModel: FilmViewModel = viewModel()
) {
    // Observamos el estado actual de la película para que la imagen
    // se actualice automáticamente tras la subida.
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // ── Imagen de la película ─────────────────────────────────────
                if (currentFilm.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentFilm.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de la película",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(bottom = 12.dp)
                    )
                } else {
                    // Imagen de reserva mientras no hay foto capturada
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.film),
                        contentDescription = "Sin imagen",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 12.dp)
                    )
                }

                // ── Título ────────────────────────────────────────────────────
                Text(
                    text = currentFilm.title.ifEmpty { "<Sin Título>" },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                // ── Comentarios ───────────────────────────────────────────────
                Text(
                    text = currentFilm.comments,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )

                // ── Captura de foto ───────────────────────────────────────────
                CameraCapture(film = currentFilm, viewModel = viewModel)

                // ── Botón Editar ──────────────────────────────────────────────
                Button(
                    onClick = { navController.navigate(NavRoutes.EDIT.abreviatura + currentFilm.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Editar")
                }

                // ── Botón Eliminar ────────────────────────────────────────────
                Button(
                    onClick = {
                        viewModel.deleteFilm(currentFilm.id)
                        navController.navigate(NavRoutes.LIST.abreviatura)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Eliminar")
                }

                // ── Botón Volver ──────────────────────────────────────────────
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