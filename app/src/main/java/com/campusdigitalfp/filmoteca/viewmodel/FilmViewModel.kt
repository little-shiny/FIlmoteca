package com.campusdigitalfp.filmoteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.repository.FilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel que gestiona la logica de la app relacionada con las películas
 * Se comunica con el repositorio para poder realizar las operaciones en firestore
 */
class FilmViewModel: ViewModel() {
    // Instancia del repo
    private val repository = FilmRepository()

    // `_films`almacena la lista de peliculas para poder modificarse en una lista
    private val _films = MutableStateFlow<List<Film>>(emptyList())

    //`films`expone las peliculas a la ui sin permitir modificaciones directas
    val films: StateFlow<List<Film>> get() = _films

    // Se ejecuta al inicializar el viewmodel y activa le escucha de los cambios en firestore
    init{
        listenToFilms()
    }

    /**
     * Escucha los cambios en Firestore en tiempo real y se actualiza en la ui
     */
    private fun listenToFilms(){
        repository.listenToFilmsUpdates { updatedFilms ->
            _films.value = updatedFilms
        }
    }

    /**
     * Recupera la lista de peliculas desde fs y actualiza _films. Evita bloquear la ui
     */
    private fun fetchFilms(){
        viewModelScope.launch{
            _films.value = repository.getFilms()
        }
    }

    /**
     * Agrega una nueva pelicula a firestore y actualiza la lista de peliculas
     */
    fun addFilm(film: Film){
        viewModelScope.launch{
            repository.addFilm(film)
            fetchFilms() // recarga la lista
        }
    }

    /**
     * Actualiza una pelicula en fs y recarga la lista de peliculas
     */
    fun updateFilm(film: Film){
        viewModelScope.launch {
            repository.updateFilm(film)
            fetchFilms()
        }
    }

    /**
     * Elimina una pelicula en firestore por su id y actualiza la lista
     */
    fun deleteFilm(filmId: String){
        viewModelScope.launch{
            repository.deleteFilm(filmId)
            fetchFilms()
        }
    }

    /**
     * Agrega una lista de 10 películas de ejemplo a firestore
     */
    fun addExampleFilms(){
        val films = listOf(
            Film(title = "Harry Potter y la piedra filosofal",
                director = "Chris Columbus",
                year = 2001, imageResId = 1,
                format = "DVD",
                genre = "Action", imbdUrl = "http://www.imdb.com/title/tt0241527",
                comments = "Una aventura mágica en Hogwarts."
            ),
            Film(
                title = "El Señor de los Anillos: La Comunidad del Anillo",
                director = "Peter Jackson",
                year = 2001,
                imageResId = 2,
                format = "Blu-ray",
                genre = "Fantasy",
                imbdUrl = "http://www.imdb.com/title/tt0120737",
                comments = "Una épica aventura en la Tierra Media."
            ),

            Film(
                title = "Inception",
                director = "Christopher Nolan",
                year = 2010,
                imageResId = 3,
                format = "Digital",
                genre = "Sci-Fi",
                imbdUrl = "http://www.imdb.com/title/tt1375666",
                comments = "Un viaje dentro de los sueños y la mente."
            ),

            Film(
                title = "Titanic",
                director = "James Cameron",
                year = 1997,
                imageResId = 4,
                format = "DVD",
                genre = "Romance",
                imbdUrl = "http://www.imdb.com/title/tt0120338",
                comments = "Una historia de amor en medio de una tragedia."
            ),

            Film(
                title = "The Dark Knight",
                director = "Christopher Nolan",
                year = 2008,
                imageResId = 5,
                format = "Blu-ray",
                genre = "Action",
                imbdUrl = "http://www.imdb.com/title/tt0468569",
                comments = "Batman enfrenta al Joker en Gotham."
            ),

            Film(
                title = "Forrest Gump",
                director = "Robert Zemeckis",
                year = 1994,
                imageResId = 6,
                format = "DVD",
                genre = "Drama",
                imbdUrl = "http://www.imdb.com/title/tt0109830",
                comments = "La vida extraordinaria de un hombre sencillo."
            ),

            Film(
                title = "The Matrix",
                director = "Lana Wachowski, Lilly Wachowski",
                year = 1999,
                imageResId = 7,
                format = "Digital",
                genre = "Sci-Fi",
                imbdUrl = "http://www.imdb.com/title/tt0133093",
                comments = "Una realidad simulada que oculta la verdad."
            ),

            Film(
                title = "Gladiator",
                director = "Ridley Scott",
                year = 2000,
                imageResId = 8,
                format = "Blu-ray",
                genre = "Action",
                imbdUrl = "http://www.imdb.com/title/tt0172495",
                comments = "Venganza y honor en la antigua Roma."
            ),

            Film(
                title = "Avatar",
                director = "James Cameron",
                year = 2009,
                imageResId = 9,
                format = "Digital",
                genre = "Sci-Fi",
                imbdUrl = "http://www.imdb.com/title/tt0499549",
                comments = "Un mundo alienígena lleno de vida y conflictos."
            ),

            Film(
                title = "Jurassic Park",
                director = "Steven Spielberg",
                year = 1993,
                imageResId = 10,
                format = "DVD",
                genre = "Adventure",
                imbdUrl = "http://www.imdb.com/title/tt0107290",
                comments = "Dinosaurios traídos de vuelta a la vida."
            ),

            Film(
                title = "The Shawshank Redemption",
                director = "Frank Darabont",
                year = 1994,
                imageResId = 11,
                format = "Blu-ray",
                genre = "Drama",
                imbdUrl = "http://www.imdb.com/title/tt0111161",
                comments = "Esperanza y amistad dentro de una prisión."
            )
        )
    }
}
