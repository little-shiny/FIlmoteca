package com.campusdigitalfp.filmoteca.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.campusdigitalfp.filmoteca.R
import java.io.File

/**
 * Construye el modelo correcto para Coil según el contenido de [imageUrl]:
 */
fun imageModel(imageUrl: String): Any? = when {
    imageUrl.isEmpty()        -> null
    imageUrl.startsWith("/")  -> Uri.fromFile(File(imageUrl))   // ruta local absoluta
    else                      -> imageUrl                        // URL remota
}

/**
 * Imagen grande para las pantallas de detalle y edición.
 */
@Composable
fun FilmImageLarge(imageUrl: String, modifier: Modifier = Modifier) {
    val model = imageModel(imageUrl)
    if (model != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            contentDescription = "Imagen de la película",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
        )
    } else {
        Image(
            painter = painterResource(R.drawable.film),
            contentDescription = "Sin imagen",
            modifier = modifier.size(120.dp)
        )
    }
}

/**
 * Miniatura circular para la lista de películas.
 */
@Composable
fun FilmImageThumbnail(imageUrl: String, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    val model = imageModel(imageUrl)
    if (model != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            contentDescription = "Miniatura",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        Image(
            painter = painterResource(R.drawable.film),
            contentDescription = "Sin imagen",
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}