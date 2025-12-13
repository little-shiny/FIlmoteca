package com.campusdigitalfp.filmoteca.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun filmDataScreen(navController: NavHostController, filmName: String?) {

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta principal
            androidx.compose.material3.Card(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
            ) {

                Column(
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Película seleccionada: ",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                    )
                    Text(
                        text = filmName ?: "Sin película",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(20.dp)
                    )
                    FilledButton(
                        onClick ={
                            navController.navigate("filmData/pelicula relacionada")
                        },
                        texto = "Ver película relacionada",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    FilledButton(
                        onClick ={
                            navController.navigate("FilmEditScreen")
                        },
                        texto = "Editar película",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    FilledButton(
                        onClick ={
                            navController.navigate("FilmListScreen"){
                                popUpTo("FilmListScreen"){ inclusive = true }
                            }
                        },
                        texto = "Volver a la principal",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

