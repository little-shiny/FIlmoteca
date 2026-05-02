package com.campusdigitalfp.filmoteca.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.campusdigitalfp.filmoteca.common.barraSuperior
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Pantalla de edición de una película existente.
 *
 * Permite al usuario modificar el título y la descripción, ver la imagen
 * actual de la película y capturar una nueva desde la cámara.
 */
@Composable
fun filmEditScreen(navController: NavHostController, film: Film, viewModel: FilmViewModel) {

    // Colores de los botones de acción
    val cancelButtonBackground = Color(0xFFEEEEEE)
    val cancelButtonText = Color(0xFF757575)
    val saveButtonBackground = Color(0xFFA5D6A7)
    val saveButtonText = Color(0xFF388E3C)

    // Observamos la lista para que la imagen se actualice en tiempo real
    val films by viewModel.films.collectAsState()
    val currentFilm = films.find { it.id == film.id } ?: film

    var title by remember { mutableStateOf(currentFilm.title) }
    var comments by remember { mutableStateOf(currentFilm.comments) }

    FilmotecaTheme {
        Scaffold(
            topBar = { barraSuperior(navController = navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // ── Título de pantalla ────────────────────────────────────────
                Text(
                    text = "Editando: ${title.ifEmpty { "<Sin título>" }}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ── Imagen actual de la película ──────────────────────────────
                if (currentFilm.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentFilm.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen actual de la película",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Imagen actual. Pulsa el botón de abajo para cambiarla.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // ── Captura de nueva imagen ───────────────────────────────────
                CameraCapture(film = currentFilm, viewModel = viewModel)

                // ── Campo título ──────────────────────────────────────────────
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la película") },
                    placeholder = { Text("Ejemplo: Pulp Fiction") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )

                // ── Campo descripción ─────────────────────────────────────────
                TextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Descripción de la película") },
                    placeholder = { Text("Ejemplo: Emocionante y atractiva") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )

                // ── Botones Cancelar / Guardar ────────────────────────────────
                Row {
                    Button(
                        onClick = {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "key_result", "Edición cancelada"
                            )
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = cancelButtonBackground),
                        border = BorderStroke(1.dp, cancelButtonText)
                    ) {
                        Text(text = "Cancelar", color = cancelButtonText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val updated = currentFilm.copy(
                                    title = title,
                                    comments = comments
                                )
                                viewModel.updateFilm(updated)
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "key_result", "Película actualizada correctamente"
                                )
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = saveButtonBackground),
                        border = BorderStroke(1.dp, saveButtonBackground)
                    ) {
                        Text(text = "Guardar", color = saveButtonText)
                    }
                }
            }
        }
    }
}