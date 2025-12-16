package com.campusdigitalfp.filmoteca.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.common.Film
import com.campusdigitalfp.filmoteca.common.FilmDataSource
import com.campusdigitalfp.filmoteca.common.barraSuperior

@Composable
fun filmListScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                atras = false
            )
        },
        // He añadido un BottomBar para que el boton "acerca de" permanezca anclado pero permita que se visaulice
        // correctamente el último elemento de la LazyColumn
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                FilledButton(
                    onClick ={
                        navController.navigate("AboutScreen")
                    },
                    texto = "Acerca de",
                )
            }
        }
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
                itemsIndexed(FilmDataSource.films){
                    index,
                    film ->
                        FilmCard(
                            film = film,
                            onClick ={
                                navController.navigate("FilmData/$index")
                            }
                        )
                }
            }
        }
    }
}
@Composable
fun FilmCard(film: Film, onClick: () -> Unit){
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)


    ){
        Row (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            //Imagen de la pelicula
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
            // Column con la información
            Column {
                Text(
                    text = film.title ?: "<Sin Título>",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
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

