package com.campusdigitalfp.filmoteca

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AboutScreen()
        }
    }
}

@Composable
fun AboutScreen() {

    val context = LocalContext.current

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
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Creada por Cristina García",
                        style = MaterialTheme.typography.titleLarge
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
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        FilledButton(
                            onClick = {
                                abrirPaginaWeb("https://shinyartplanet.etsy.com", context = context)
                            },
                            texto = "Ir al sitio web"
                        )
                        FilledButton(
                            onClick = {
                                mandarEmail(context, "cgarciaquintero@campusdigitalfp.es", context.getString(R.string
                                    .incidencia_con_filmoteca))
                            },
                            texto = "Obtener soporte",

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
                            .padding(top = 15.dp)
                    )
                }
            }
        }
    }
}

fun showToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun abrirPaginaWeb(url: String, context: Context){
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}

fun mandarEmail(context : Context, email : String, asunto: String){
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, asunto)
    }
    context.startActivity(intent)
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

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}