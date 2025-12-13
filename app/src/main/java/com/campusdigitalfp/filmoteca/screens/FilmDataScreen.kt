package com.campusdigitalfp.filmoteca.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun filmDataScreen(navController: NavHostController, filmName: String?) {
    // Estado para mostrar el resultado de si se ha editado o no
    var editResult by remember { mutableStateOf("") }

    val currentBackStackEntry = navController.currentBackStackEntry
    currentBackStackEntry?.savedStateHandle
        ?.getLiveData<String>("editResult")
        ?.observe(currentBackStackEntry) {
            result ->
                editResult = result
        }



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
                        text = "Película seleccionada: ${filmName ?: "Sin película"}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    if (editResult.isNotEmpty()) {
                        Text("Resultado edición: $editResult",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally))

                    }
                    Spacer(modifier = Modifier.height(20.dp))

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

