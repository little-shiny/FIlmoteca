package com.campusdigitalfp.filmoteca.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.campusdigitalfp.filmoteca.FilledButton
import com.campusdigitalfp.filmoteca.abrirPaginaWeb
import com.campusdigitalfp.filmoteca.mandarEmail
import com.campusdigitalfp.filmoteca.showToast

@Composable
fun filmListScreen() {

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
                    .padding(10.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    FilledButton(
                        onClick ={
                            println("todo")
                        },
                        texto = "Ver película A",
                        modifier = Modifier.align(Alignment.CenterHorizontally)

                    )
                    FilledButton(
                        onClick ={
                            println("todo")
                        },
                        texto = "Ver película B",
                        modifier = Modifier.align(Alignment.CenterHorizontally)

                    )
                    FilledButton(
                        onClick ={
                            println("todo")
                        },
                        texto = "Acerca de",
                        modifier = Modifier.align(Alignment.CenterHorizontally)

                    )



                }

            }
        }
    }
}

@Composable
fun FilledButton(
    onClick:  () -> Unit,
    texto: String,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        modifier = modifier
    ){
        Text(
            texto,
            textAlign = TextAlign.Center
        )
    }
}

@Preview (showBackground = true)
@Composable
fun filmListScreenPreview(){
    filmListScreen()
}