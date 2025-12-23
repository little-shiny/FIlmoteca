package com.campusdigitalfp.filmoteca.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.R

// Funciones composables compartidas en toda la app

// Barra superior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun barraSuperior(
    navController: NavHostController,
    esHome : Boolean = false,
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
        // Título que se muestra solo si la pantalla es home
        title = {
            if(esHome){
                Text(text = "Filmoteca")
            }else{
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate("FilmListScreen"){
                            //al pulsar el icono volvemos a home
                            popUpTo(0)
                        }
                    }
                ){
                    Icon(
                        painter = painterResource(id =R.drawable.film),
                        contentDescription = "Home",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }
        },
        // Flecha atrás opcional (solo en pantallas que no sean home)
        navigationIcon = {
            if (atras && !esHome) {
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