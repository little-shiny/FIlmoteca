package com.campusdigitalfp.filmoteca

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AboutScreen();
        }
    }
}

@Composable
fun AboutScreen() {

    val context = LocalContext.current

    Scaffold { innerPadding ->
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
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Creada por Cristina García",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                    Image(
                        painter = painterResource(id = R.drawable.perfil),
                        contentDescription = "Icono de perfil",
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(120.dp)
                    )

                    //Botones horizontales
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilledButton(
                            onClick = {
                                showToast(
                                    context = context,
                                    message = "Funcionalidad sin implementar"
                                )
                            },
                            texto = "Ir al sitio web"
                        )
                        FilledButton(
                            onClick = {
                                showToast(
                                    context = context,
                                    message = "Funcionalidad sin implementar"
                                )
                            },
                            texto = "Obtener soporte"
                        )
                    }
                    FilledButton(
                        onClick = {
                            showToast(
                                context = context,
                                message = "Funcionalidad sin implementar"
                            )
                        },
                        texto = "Volver",
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

fun showToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
        Text(texto)
    }
 }

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}