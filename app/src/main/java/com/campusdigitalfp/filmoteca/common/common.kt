package com.campusdigitalfp.filmoteca.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController

// Funciones composables compartidas en toda la app

// Barra superior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun barraSuperior(
    navController: NavHostController,
    atras: Boolean = true,
    menu: Boolean = false,
    onBackResult: (() -> Unit)? = null, //parámetro opcional que puede existir o no y solo se usa en filEditScreen
    onAddFilm:  (() -> Unit)? = null, // Acción opcional para la accion al pulsar añadir película
    onAbout: (() -> Unit)? = null // Acción al pulsar acerca de
    // Se utiliza (() -> Unit)? para que la barra sea reutilizable y sean funciones opcionales


){
    var expanded by remember { mutableStateOf(false) }

    // TopAppBar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = "Filmoteca")
        },
        navigationIcon = {
            if (atras) {
                IconButton(onClick = {
                    onBackResult?.invoke()
                    navController.popBackStack()
                }
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            }
        },
        actions = {
            if(menu){
                IconButton(onClick = {expanded = true}){
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menú"
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                DropdownMenuItem(
                    text = {Text("Acerca de")},
                    onClick = {
                        expanded = false
                        onAbout?.invoke()
                    }
                )
                DropdownMenuItem(
                    text = {Text("Añadir película ")},
                    onClick = {
                        expanded = false
                        onAddFilm?.invoke()
                    }
                )
            }
        }
    )
}