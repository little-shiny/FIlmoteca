package com.campusdigitalfp.filmoteca.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.barraSuperior

/**
 * Pantalla "Acerca de" con información del autor y un vídeo "How To" que
 * explica cómo usar la aplicación Filmoteca.
 *
 * El vídeo se reproduce con ExoPlayer embebido en Jetpack Compose
 * mediante [AndroidView].
 */
@Composable
fun AboutScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                atras = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Tarjeta de autor ──────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
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

                    Row(
                        modifier = Modifier.padding(top = 10.dp),
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
                                mandarEmail(
                                    context,
                                    "cgarciaquintero@campusdigitalfp.es",
                                    context.getString(R.string.incidencia_con_filmoteca)
                                )
                            },
                            texto = "Obtener soporte"
                        )
                    }

                    FilledButton(
                        onClick = { navController.popBackStack() },
                        texto = "Volver",
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            }

            // ── Vídeo "How To" ────────────────────────────────────────────────
            Text(
                text = "¿Cómo usar Filmoteca?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            Text(
                text = "Mira esta guía rápida para aprender a gestionar tu colección de películas:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Reemplaza la URL por la del vídeo "How To" real de tu proyecto.
            // Puede ser un vídeo en Firebase Storage, YouTube (HLS) o cualquier
            // URL directa de fichero MP4 accesible desde Internet.
            VideoItem(
                videoUrl = "todo"
            )
        }
    }
}

// ── Reproductor de vídeo con ExoPlayer ───────────────────────────────────────

/**
 * Composable que reproduce un vídeo MP4 (local o remoto) usando ExoPlayer.
 *
 * Libera los recursos del reproductor automáticamente al salir de la composición
 * mediante [DisposableEffect].
 *
 * @param videoUrl URL del vídeo a reproducir.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoItem(videoUrl: String) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            playWhenReady = false // No autoplay; el usuario decide cuándo reproducir
        }
    }

    // Libera el reproductor al salir de la composición
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true  // Muestra controles de reproducción
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

// ── Helpers reutilizables ─────────────────────────────────────────────────────

fun abrirPaginaWeb(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    context.startActivity(intent)
}

fun mandarEmail(context: Context, email: String, asunto: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, asunto)
    }
    context.startActivity(intent)
}

@Composable
fun FilledButton(
    onClick: () -> Unit,
    texto: String,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Text(texto, textAlign = TextAlign.Center)
    }
}