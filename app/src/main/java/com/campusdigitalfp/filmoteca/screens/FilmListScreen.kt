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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

/**
 * Pantalla principal que muestra la lista de películas.
 * Cada elemento muestra la miniatura si la película tiene imagen,
 * o el icono predeterminado en caso contrario.
 */
@Composable
fun filmListScreen(navController: NavHostController, viewModel: FilmViewModel = viewModel()) {

    val isActionMode = remember { mutableStateOf(false) }
    val selectedFilms = remember { mutableStateListOf<Film>() }
    val films by viewModel.films.collectAsState()

    // Mensajes temporales via NavController
    navController.currentBackStackEntry?.savedStateHandle?.let {
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
        ) { paddingValues ->
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
 * Fila individual de película en la lista.
 * Muestra la miniatura (desde URL o icono de reserva), el título y los comentarios.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewFilm(
    film: Film,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Miniatura: icono de seleccionado, imagen de Firebase o icono predeterminado
        when {
            isSelected -> {
                Image(
                    painter = painterResource(R.drawable.baseline_check_24),
                    contentDescription = "Seleccionada",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
            film.imageUrl.isNotEmpty() -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(film.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Portada de ${film.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
            else -> {
                Image(
                    painter = painterResource(R.drawable.film),
                    contentDescription = "Icono película",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            label = "AnimacionDeColor"
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = film.title.ifEmpty { "<Sin Título>" },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = film.comments,
                    modifier = Modifier.padding(4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * LazyColumn con la lista completa de películas.
 */
@Composable
fun ViewFilmList(
    films: List<Film>,
    navController: NavHostController,
    isActionMode: MutableState<Boolean>,
    selectedFilms: MutableList<Film>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(films) { film ->
            ViewFilm(
                film = film,
                onClick = {
                    if (isActionMode.value) {
                        if (selectedFilms.contains(film)) {
                            selectedFilms.remove(film)
                            if (selectedFilms.isEmpty()) isActionMode.value = false
                        } else {
                            selectedFilms.add(film)
                        }
                    } else {
                        navController.navigate(NavRoutes.DETAILS.abreviatura + film.id)
                    }
                },
                onLongClick = {
                    isActionMode.value = true
                    selectedFilms.add(film)
                },
                isSelected = selectedFilms.contains(film)
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}