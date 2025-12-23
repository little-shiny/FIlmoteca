package com.campusdigitalfp.filmoteca.common

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.campusdigitalfp.filmoteca.R

// Clase que gestiona los cambios al rotar la pantalla y permite añadir películas
class FilmViewModel : ViewModel() {

    // Lista observable para que Compose reconozca cambios
    val films = mutableStateListOf<Film>().apply {
        addAll(FilmDataSource.films) // copia inicial
    }

    /* Contador global de ID
    -------------------------
    Funciona de la siguiente manera:
        - maxOfOrNull { it.id } -> busca el valor maximo de id de las peliculas de la lista actual
        - ?: 0 -> se usa para que si es null el lado izquierdo se use el derecho, es decir, si no hay peliculas en la
            lista actual se usa el cero
        - +1 -> suma 1 al id máximo que se encuentre, entonces siempre va a ser unico porque es mayor que cualquiera
            que exista ya

        Implementado con chatgpt ya que no sabía muy bien como hacerlo y crasheaba todo el rato por
        IndexOutOfBoundException
     */
    private var nextId = (FilmDataSource.films.maxOfOrNull { it.id } ?: 0) + 1

    // Función para añadir una película por defecto
    fun addDefaultFilm(): Film {
        val film = Film(
            id = nextId++, // Id único siempre
            title = "Película Nueva",
            director = "",
            year = 2025,
            imageResId = R.drawable.film
        )
        films.add(film)               // añadimos a la lista observable
        FilmDataSource.films.add(film) // sincronizamos con FilmDataSource
        return film
    }

    // Función para eliminar varias películas por IDs
    fun deleteFilmsByIds(ids: List<Int>) {
        films.removeAll { it.id in ids }          // elimina de la lista observable
        FilmDataSource.films.removeAll { it.id in ids } // elimina también de FilmDataSource
    }
}
