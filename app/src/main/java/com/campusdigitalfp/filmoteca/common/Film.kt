package com.campusdigitalfp.filmoteca.common

data class Film(
    var id: Int = 0,
    var imageResId: Int = 0, // Propiedades de la clase
    var title: String? = null,
    var director: String? = null,
    var year: Int = 0,
    var genre: Int = 0,
    var format: Int = 0,
    var imdbUrl: String? = null,
    var comments: String? = null
) {
    //Funcion implementada para traducir el indice Int del genero y formato a una string
    fun genreToString(): String {
        return when (genre) {
            GENRE_ACTION -> "Acción"
            GENRE_COMEDY -> "Comedia"
            GENRE_DRAMA -> "Drama"
            GENRE_SCIFI -> "Ciencia ficción"
            GENRE_HORROR -> "Terror"
            else -> "Desconocido"
        }
    }
    //lo mismo con el formato
    fun formatToString(): String {
        return when (format) {
            FORMAT_DIGITAL -> "Digital"
            FORMAT_BLURAY -> "Blu-Ray"
            FORMAT_DVD -> "DVD"
            else -> "Desconocido"
        }
    }

    override fun toString(): String {
        // Al convertir a cadena mostramos su título
        return title ?: "<Sin título>"
    }

    companion object {
        const val FORMAT_DVD = 0 // Formatos
        const val FORMAT_BLURAY = 1
        const val FORMAT_DIGITAL = 2
        const val GENRE_ACTION = 0 // Géneros
        const val GENRE_COMEDY = 1
        const val GENRE_DRAMA = 2
        const val GENRE_SCIFI = 3
        const val GENRE_HORROR = 4
    }
}