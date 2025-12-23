package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.FilmDataSource
import com.campusdigitalfp.filmoteca.common.barraSuperior
@Composable
fun filmDataScreen(navController: NavHostController, filmId: Int?) {
    // Estado para mostrar el resultado de si se ha editado o no
    val editResult = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("result")
    val context = LocalContext.current

    val film = FilmDataSource.films.firstOrNull { it.id == filmId }

    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                esHome = false,
                atras = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ){
            // Si no se encuentran los datos o no se pueden leer
            if(film == null){
                Text("Película no encontrada")
                return@Scaffold
            }

            // Imagen que no da error al modificar la lista de peliculas
            val imageRes = if(film.imageResId != 0)
                film.imageResId else R.drawable.film // Imagen por defecto si no hay

            //Imagen y título
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Imagen de la película",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(120.dp)
                )
                Column {
                    //Titulo
                    Text(
                        text = film.title ?: "Sin título",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = "Director:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    //Autor
                    Text(
                        text = film.director ?: "Desconocido",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Año:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    //Año
                    Text(
                        text = film.year.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }

            Spacer(modifier = Modifier.height(20.dp))
            if (editResult == Activity.RESULT_CANCELED) {
                Text(
                    "Edición cancelada",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Text(
                text = "Género: ${film.genreToString()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(10.dp)
            )
            Text(
                text = "Formato: ${film.formatToString()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            FilledButton(
                onClick = {
                    abrirEnIMDB(film.imdbUrl ?: "", context)
                },
                texto = "Ver en IMDB",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),

            ){
                FilledButton(
                    onClick = {
                        navController.navigate("FilmEdit/$filmId")
                    },
                    texto = "Editar",
                    modifier = Modifier.weight(1f)

                )
                FilledButton(
                    onClick = {
                        navController.navigate("FilmListScreen") {
                            popUpTo("FilmListScreen") { inclusive = true }
                        }
                    },
                    texto = "Volver",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun abrirEnIMDB(url: String, context: Context){
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}




