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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.barraSuperior
@Composable
fun filmDataScreen(navController: NavHostController, filmName: String?) {
    // Estado para mostrar el resultado de si se ha editado o no
    val editResult = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("result")
    val context = LocalContext.current

    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
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
            //Imagen y título
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.film),
                    contentDescription = "Imagen de la película",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(120.dp)
                )
                Column {
                    Text(
                        text = filmName ?: "Sin película",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = "Director:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Peter Jackson:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Año:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "2001:",
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
                text = "Género:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(20.dp)
            )
            Text(
                text = "Formato:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            FilledButton(
                onClick = {
                    abrirEnIMDB("https://www.imdb.com/es-es/title/tt0120737/?ref_=ext_shr_lnk" , context)
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
                        navController.navigate("FilmEditScreen")
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

@Preview(showBackground = true)
@Composable
fun FilmDataScreenPreview() {
    val navController = rememberNavController()

    filmDataScreen(
        navController = navController,
        filmName = "El Señor de los Anillos"
    )
}


