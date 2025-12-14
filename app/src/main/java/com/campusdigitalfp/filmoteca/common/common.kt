package com.campusdigitalfp.filmoteca.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

// Funciones composables compartidas en toda la app

// Barra superior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun barraSuperior(
    navController: NavHostController,
    atras: Boolean = true,
    onBackResult: (() -> Unit)? = null //parámetro opcional que puede existir o no y solo se usa en filEditScreen

){
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
        }
    )
}