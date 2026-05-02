package com.campusdigitalfp.filmoteca.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.campusdigitalfp.filmoteca.R

/**
 * Devuelve el drawable dinámicamente usando el nombre
 * almacenado en Firebase.
 *
 * Ejemplo:
 * imageUrl = "matrix"
 * -> R.drawable.matrix
 */
fun getImageResId(
    context: android.content.Context,
    imageUrl: String
): Int {

    if (imageUrl.isEmpty()) {
        return R.drawable.film
    }

    val resId = context.resources.getIdentifier(
        imageUrl,
        "drawable",
        context.packageName
    )

    return if (resId != 0) resId else R.drawable.film
}

/**
 * Imagen grande para detalle/edición.
 */
@Composable
fun FilmImageLarge(
    imageUrl: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val imageResId = getImageResId(
        context,
        imageUrl
    )

    Image(
        painter = painterResource(imageResId),
        contentDescription = "Imagen de la película",
        contentScale = ContentScale.Crop,

        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

/**
 * Miniatura circular para listas.
 */
@Composable
fun FilmImageThumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {

    val context = LocalContext.current

    val imageResId = getImageResId(
        context,
        imageUrl
    )

    Image(
        painter = painterResource(imageResId),
        contentDescription = "Miniatura",
        contentScale = ContentScale.Crop,

        modifier = modifier
            .size(size)
            .clip(CircleShape)
    )
}