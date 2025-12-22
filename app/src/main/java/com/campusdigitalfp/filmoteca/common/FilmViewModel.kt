package com.campusdigitalfp.filmoteca.common

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// Clase que se utiliza para gestionar los cambios al rotar la pantalla y permitir que se muestren bien los datos.
//También permite añadir la película por defecto a la lista y muestra las películas que se generan con FilmDataSource
class FilmViewModel : ViewModel() {

    //lista observable
    private var films = mutableStateListOf<Film>()

    init {
        //copiamos las películas de FilmDataSource
        films.addAll(FilmDataSource.films)
    }

    //función para añadir una película por defecto
    fun addDefaultFilm(): Film {
        val film = Film(
            id = FilmDataSource.films.size,
            title = "Película Nueva",
            director = "",
            year = 2025
        )
        FilmDataSource.films.add(film)
        return film
    }
}
