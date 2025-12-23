package com.campusdigitalfp.filmoteca.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.common.Film
import com.campusdigitalfp.filmoteca.common.FilmViewModel
import com.campusdigitalfp.filmoteca.common.barraSuperior
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.mutableStateListOf


@Composable
fun filmListScreen(navController: NavHostController) {

    val viewModel: FilmViewModel = viewModel()
    val isActionMode = remember { mutableStateOf(false) }
    val selectedFilmsIds = remember { mutableStateListOf<Int>() } //cambio a IDS de las películas

    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                esHome = true,
                atras = true,
                menu = true,

                // Action Mode
                isActionMode = isActionMode,
                selectedFilms = selectedFilmsIds,
                onDeleteSelected = {
                    viewModel.deleteFilmsByIds(selectedFilmsIds) //nos movemos por las id
                    selectedFilmsIds.clear()
                    isActionMode.value = false
                },

                onAddFilm = {
                    //Se añade la película por defecto a la lista
                    val newFilm = viewModel.addDefaultFilm()
                    // navegar por el ID de la película en el array
                    navController.navigate("filmEdit/${newFilm.id}")
                },
                onAbout = {
                    navController.navigate("AboutScreen")
                }
            )
        },

    ){innerPadding ->
        Column{
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
            ) {
                items(
                    viewModel.films,
                    key = {film -> film.id} // Id unica para cada película
                ){film ->

                    // Comprobamos si la película actual está seleccionada
                    val isSelected = selectedFilmsIds.contains(film.id)

                    FilmCard(
                        film = film,
                        // Indicamos si este item está seleccionado
                        isSelected = isSelected,
                        // Indicamos si el modo selección múltiple está activo
                        isActionMode = isActionMode.value,

                        // Pulsación corta sobre la tarjeta
                        onClick ={
                            if(isActionMode.value){
                                // Si estamos en modo selección, se selecciona o deselecciona la película
                                if(isSelected){
                                    selectedFilmsIds.remove(film.id)
                                } else {
                                    selectedFilmsIds.add(film.id)
                                }

                                // Si no queda ninguna película seleccionada,
                                // se sale del modo selección múltiple
                                if (selectedFilmsIds.isEmpty()){
                                    isActionMode.value = false
                                }
                            } else {
                                // Si no estamos en modo selección,
                                // se navega a la pantalla de detalle de la película
                                navController.navigate("FilmData/${film.id}")// Navegamos por ID
                            }
                        },

                        // Pulsación larga sobre la tarjeta
                        onLongClick = {
                            // Si el modo selección no está activo,
                            // se activa y se selecciona la película pulsada
                            if(!isActionMode.value) {
                                isActionMode.value = true
                                selectedFilmsIds.add(film.id)
                            }
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilmCard(
    film: Film,
    isSelected: Boolean,
    isActionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
){
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            // La Card seleccionada cambia de color
            containerColor = if(isSelected)
                MaterialTheme.colorScheme.secondaryContainer
           else
                MaterialTheme.colorScheme.surface
        )
    ){
        Row (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            //Imagen de la pelicula o icono del check:
            // Se usa Box para poder cambiar la imagen y el icono. Si estamos en modo seleccion multiple se muestran
            // los iconos del check, si no, se muestra la imagen de la película
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(
                        top = 7.dp,
                        end = 16.dp,
                        bottom = 7.dp
                    )
            ){
                if(isActionMode){
                    if (isSelected){
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Seleccionado",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxSize()

                        )
                    }else{
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "No seleccionado",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    if(film.imageResId !=0){
                        Image(
                            painter = painterResource(id = film.imageResId),
                            contentDescription = film.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(top = 7.dp, end = 16.dp , bottom = 7.dp)
                        )
                    }
                }
            }

            // Column con la información
            Column {
                Text(
                    text = film.title ?: "<Sin Título>",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,

                )
                Text(
                    text = film.director ?: "<Sin Director>",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun filmListScreenPreview(){
    val navController = rememberNavController()
    filmListScreen(
        navController = navController
    )
}

