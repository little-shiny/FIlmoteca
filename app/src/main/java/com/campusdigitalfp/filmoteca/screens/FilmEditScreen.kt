package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.campusdigitalfp.filmoteca.common.barraSuperior

@Composable
fun filmEditScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                atras = true,
                onBackResult = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("result", Activity.RESULT_CANCELED)
                }
            )
        }
    ){
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
                        text = "Editando película",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(20.dp)
                    )
                    FilledButton(
                        onClick ={
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("editResult","RESULT_OK")
                            navController.popBackStack()
                        },
                        texto = "Guardar",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    FilledButton(
                        onClick = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("editResult", "RESULT_CANCELLED")
                            navController.popBackStack()
                        },
                        texto = "Cancelar",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}


