package com.campusdigitalfp.filmoteca.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import com.campusdigitalfp.filmoteca.common.barraSuperior
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Pantalla principal que muestra la lista de películas almacenadas en Firestore
 *
 * @param navController Controlador de navegación para cambiar entre pantallas
 * @param viewModel ViewModel que gestiona los hábitos y su estado
 */
@Composable
fun filmListScreen(navController: NavHostController, viewModel: FilmViewModel = viewModel()) {

    val isActionMode = remember { mutableStateOf(false) }
    val selectedFilms = remember { mutableStateListOf<Film>() } //cambio a IDS de las películas
    val films by viewModel.films.collectAsState() // estado de las peliculas observadas

    // Manejo de los mensajes temporales a traves del NavController
    navController.currentBackStackEntry?.savedStateHandle?.let{
        val context = LocalContext.current

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        val result = savedStateHandle?.get<String>("key_result")

        LaunchedEffect(result) {
            result?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                savedStateHandle.remove<String>("key_result")
            }
        }
    }
    FilmotecaTheme {
        Scaffold(
            topBar = {
                barraSuperior(
                    navController = navController,
                    atras = false,
                    isActionMode = isActionMode,
                    selectedFilms = selectedFilms,
                )
            }
        ){paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ViewFilmList(
                    films,
                    navController,
                    isActionMode,
                    selectedFilms
                )
            }
        }
    }
}

/**
 * Vista que representa una sola película en la lista
 *
 * @param film objeto que contiene la informacion de la pelicula
 * @param onLongClick Accion al hacer pulsacion larga
 * @param isSelected Indica si la pelicula está seleccionada en la seleccion multiple
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewFilm(
    film: Film,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean
){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .combinedClickable (onClick = onClick, onLongClick = onLongClick)
    ) {
        Image(
            painter = painterResource(if(isSelected) R.drawable.baseline_check_24 else R.drawable.film),
            contentDescription = "Icono película",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember{ mutableStateOf(false) } // controla si se expande la descripcion
        val surfaceColor by animateColorAsState(
            if(isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                label= "AnimacionDeColor"
        )

        Column (modifier = Modifier.clickable { isExpanded= !isExpanded }) {
            Text(
                text = film.title,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)
            ){
                Text(
                    text = film.comments,
                    modifier = Modifier.padding(4.dp),
                    maxLines = if(isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Lista de peliculas representadas en un LazyColumn
 *
 * @param films Lista de peliculas a mostrar
 * @param navController Controlador de navegacion para cambiar entre pantallas
 * @param isActionMode Indica si la seleccion multiple está activa
 * @param selectedFilms Lista de películas seleccionadas en modo selección múltiple
 */
@Composable
fun ViewFilmList(
    films: List<Film>,
    navController: NavHostController,
    isActionMode: MutableState<Boolean>,
    selectedFilms: MutableList<Film>
){
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        items(films){ film ->
            ViewFilm(
                film,
                onClick = {
                    if (isActionMode.value){
                        // maneja la seleccion y deseleccion en modo multiple
                        if (selectedFilms.contains(film)){
                            selectedFilms.remove(film)
                            if (selectedFilms.isEmpty()){
                                isActionMode.value = false
                            }
                        }else{
                            selectedFilms.add(film)
                        }
                    }else {
                        // Navega a la pantalla de detalles de la película seleccionada
                        navController.navigate(NavRoutes.DETAILS.abreviatura + film.id)
                    }
                },
                onLongClick = {
                    // Activa el modo seleccion múltiple y agrega la película seleccionada
                    isActionMode.value = true
                    selectedFilms.add(film)
                },
                isSelected = selectedFilms.contains(film)
            )

        }
    }
}