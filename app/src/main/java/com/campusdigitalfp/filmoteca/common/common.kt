package com.campusdigitalfp.filmoteca.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.viewmodel.AuthViewModel
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Componente `BarraSuperior` que representa la barra superior de la app
 * incluye navegación, menú desplegable y opciones para gestionar las peliculas
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun barraSuperior(
    navController: NavHostController,
    atras: Boolean = true,
    isActionMode: MutableState<Boolean> = mutableStateOf(false),
    selectedFilms: MutableList<Film> = mutableListOf(),
    viewModel: FilmViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
){
    var expanded by remember { mutableStateOf(false) } // estado del menú desplegable

    TopAppBar(
        title = {
            Text(text = "Filmoteca")
        },
        navigationIcon = {
            if (atras) {
                // Muestra el botón de retroceso
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            }
        },
        actions = {
            if (!atras) {
                // si atras es false muestra el menú desplegable
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menú desplegable"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Opción para añadir una nueva película
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            navController.navigate(NavRoutes.NEW.abreviatura)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Añadir película",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = { Text("Nuevo") }
                    )

                    // Opción para añadir 10 películas de ejemplo
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            viewModel.addExampleFilms()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Añadir 10 películas",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = { Text("Añadir 10 películas") }
                    )

                    // Opción para ir a la pantalla "Acerca de"
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            navController.navigate(NavRoutes.ABOUT.abreviatura)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Acerca de",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = { Text("Acerca de") }
                    )

                    // Separador visual antes de cerrar sesión
                    HorizontalDivider()

                    // Opción para cerrar sesión
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            authViewModel.logout()
                            navController.navigate(NavRoutes.LOGIN.abreviatura) {
                                // Limpia toda la pila de navegación para que no se pueda volver atrás
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cerrar sesión",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        text = {
                            Text(
                                text = "Cerrar sesión",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }

                // Opción para eliminar las películas seleccionadas, solo visible en modo selección múltiple
                if (isActionMode.value) {
                    IconButton(
                        onClick = {
                            viewModel.deleteSelectedFilms(selectedFilms)
                            selectedFilms.clear()
                            isActionMode.value = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Borrar seleccionadas",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    )
}