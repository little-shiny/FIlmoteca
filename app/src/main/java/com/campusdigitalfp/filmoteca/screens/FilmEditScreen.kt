package com.campusdigitalfp.filmoteca.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.common.barraSuperior
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Pantalla para editar una película exisxtente
 *
 * Permite al usuario modificar el título y la descripción de la película guardada en firestore
 *
 * @param navController Controlador de navegación para cambiar de pantalla
 * @param film Objeto que contiene los datos de la película seleccionada
 * @param viewModel ViewModel que maneja la actualizacion de la pelicula en firestore
 */
@Composable
fun filmEditScreen(navController: NavHostController, film: Film, viewModel: FilmViewModel) {
    // Colores de los botones
    val cancelButtonBackground = Color(0xFFEEEEEE)
    val cancelButtonText = Color(0xFF757575)
    val saveButtonBackground = Color(0xFFA5D6A7)
    val saveButtonText = Color(0xFF388E3C)

    // Estados que almacenan los valores ingresados por el usuario
    var title by remember { mutableStateOf(film.title) }
    var comments by remember { mutableStateOf(film.comments) }

    FilmotecaTheme {
        Scaffold (
            // Agrega la barra superior
            topBar = { barraSuperior(navController = navController) }
        ){ paddingValues ->
            Column (
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                // Título de la pantalla de edición
                Text(
                    text = "Editando película: $title",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // Campo de entrada para el título
                TextField(
                    value = title,
                    onValueChange = { newText -> title = newText },
                    label = { Text("Título de la película") },
                    placeholder = { Text("Ejemplo: Pulp Fuction") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences
                    )
                )
                // Campo de texto para la descripcion/comentario
                TextField(
                    value = comments,
                    onValueChange = { newText -> comments = newText },
                    label = { Text("Descripción de la película") },
                    placeholder = { Text("Ejemplo: Emocionante y atractiva") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción (cancelar y guardar cambios
                Row {
                    // Botón de cancelar y volver sin guardar cambios
                    Button(
                        onClick = {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "key_result",
                                "Operación cancelada"
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

                    // Boton para guardar en la bd
                    Button(
                        onClick = {
                            if (title.isNotBlank() && comments.isNotBlank()) {
                                viewModel.addFilm(Film(title = title, comments = comments))
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "key_result", "Película " +
                                            "creada con éxito"
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
