package com.campusdigitalfp.filmoteca.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
    esHome: Boolean = false,
    atras: Boolean = true,
    menu: Boolean = false,
    onBackResult: (() -> Unit)? = null, //parámetro opcional que puede existir o no y solo se usa en filEditScreen
    onAddFilm:  (() -> Unit)? = null, // Acción opcional para la accion al pulsar añadir película
    onAbout: (() -> Unit)? = null, // Acción al pulsar acerca de
    // Se utiliza (() -> Unit)? para que la barra sea reutilizable y sean funciones opcionales
    //parametros para la seleccion multiple
    isActionMode: MutableState<Boolean>? = null,
    selectedFilms: MutableList<Int>? = null, // permite coger datos de una lista observable y que puede ser de
    // cualquier elmemento. no especifico film porque podría usarse para otro tipo de listas
    onDeleteSelected: (() -> Unit)? = null





){
    var expanded by remember { mutableStateOf(false) }
    val actionModeActive = isActionMode?.value ==true

    //variable que almacena el color de la barra dependiendo de si se está haciendo una selección múltiple
    val appBarColor =
        if (actionModeActive)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.primaryContainer

    // TopAppBar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarColor
        ),

        // Título
        title = {
            if(actionModeActive && selectedFilms != null){
                //Se muestra el texto al hacer seleccion múltiple
                Text("${selectedFilms.size} películas seleccionadas") // muestra el tamaño de la lista de los elementos
            // seleccionados
            }else{
                if(esHome){
                    Text("Filmoteca") // si no se hace sellección muestra el título si es la pantalla home
                }else{ // si es cualquier otra pantalla muestra el icono para volver al home
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
            }
        },
        // Flecha atrás opcional (solo en pantallas que no sean home)
        navigationIcon = {
            //Cancelar selección
            if(actionModeActive){
                IconButton(onClick = {
                    selectedFilms?.clear()
                    isActionMode?.value = false
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Cancelar selección"
                    )
                }
                //Volver atrás
            } else if (atras && !esHome){
                IconButton( onClick = {
                    onBackResult?.invoke()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            }
        },

        //Acciones parte derecha de la barra superior
        actions = {
            if(actionModeActive){ // Ponemos icono de borrar para borrar las películas seleccionadas
                IconButton(onClick = {onDeleteSelected?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar seleccionados"
                    )
                }
            }

            //Menú derecho
            if (menu) {
                IconButton(
                    //Ocultamos el icono del menú si hay selección múltiple
                    onClick = { if (!actionModeActive) expanded = true },
                    enabled = !actionModeActive
                ) {
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