package com.campusdigitalfp.filmoteca.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.FilmImageThumbnail

import com.campusdigitalfp.filmoteca.common.barraSuperior
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.navigation.NavRoutes
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel

@Composable
fun filmListScreen(navController: NavHostController, viewModel: FilmViewModel = viewModel()) {

    val isActionMode = remember { mutableStateOf(false) }
    val selectedFilms = remember { mutableStateListOf<Film>() }
    val films by viewModel.films.collectAsState()

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
                ViewFilmList(films, navController, isActionMode, selectedFilms)
            }
        }
    }
}

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
        // Miniatura: check si seleccionada, imagen local o icono por defecto
        if (isSelected) {
            Image(
                painter = painterResource(R.drawable.baseline_check_24),
                contentDescription = "Seleccionada",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } else {
            FilmImageThumbnail(
                imageUrl = film.imageUrl,
                modifier = Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        Spacer(Modifier.width(10.dp))

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
            Spacer(Modifier.height(2.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)
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


@Composable
fun ViewFilmList(
    films: List<Film>,
    navController: NavHostController,
    isActionMode: MutableState<Boolean>,
    selectedFilms: MutableList<Film>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        items(films) { film ->
            ViewFilm(
                film = film,
                onClick = {
                    if (isActionMode.value) {
                        if (selectedFilms.contains(film)) {
                            selectedFilms.remove(film)
                            if (selectedFilms.isEmpty()) isActionMode.value = false
                        } else selectedFilms.add(film)
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