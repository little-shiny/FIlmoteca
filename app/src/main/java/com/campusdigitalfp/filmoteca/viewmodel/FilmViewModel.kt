package com.campusdigitalfp.filmoteca.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.repository.FilmRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilmViewModel : ViewModel() {

    private val repository = FilmRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    /** true mientras se está guardando la imagen */
    private val _imageUploading = MutableStateFlow(false)
    val imageUploading: StateFlow<Boolean> = _imageUploading

    init {
        // No cargamos aquí: Navigation se encarga de llamar loadFilms()
        // una vez que el usuario está autenticado (via LaunchedEffect con uid como clave)
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun loadFilms() {
        if (auth.currentUser == null) return

        viewModelScope.launch {
            try { _films.value = repository.getFilms() }
            catch (_: Exception) { _films.value = emptyList() }
        }
    }

    fun addFilm(film: Film) {
        viewModelScope.launch {
            try {
                val id = repository.addFilm(film)
                _films.value = _films.value + film.copy(id = id)
            } catch (_: Exception) {}
        }
    }

    fun updateFilm(film: Film) {
        viewModelScope.launch {
            try {
                repository.updateFilm(film)
                _films.value = _films.value.map { if (it.id == film.id) film else it }
            } catch (_: Exception) {}
        }
    }

    fun deleteFilm(filmId: String) {
        viewModelScope.launch {
            try {
                repository.deleteFilm(filmId)
                _films.value = _films.value.filter { it.id != filmId }
            } catch (_: Exception) {}
        }
    }

    fun deleteSelectedFilms(films: List<Film>) {
        viewModelScope.launch {
            try {
                repository.deleteMultipleFilms(films.map { it.id })
                val ids = films.map { it.id }.toSet()
                _films.value = _films.value.filter { it.id !in ids }
            } catch (_: Exception) {}
        }
    }

    // ── Películas de ejemplo ──────────────────────────────────────────────────

    /** Añade 10 películas de ejemplo a la colección del usuario actual */
    fun addExampleFilms() {
        val examples = listOf(
            Film(title = "El Padrino", director = "Francis Ford Coppola", year = 1972, genre = "Drama", format = "Blu-Ray", comments = "Una obra maestra del cine."),
            Film(title = "Pulp Fiction", director = "Quentin Tarantino", year = 1994, genre = "Drama", format = "DVD", comments = "Narrativa no lineal y diálogos brillantes."),
            Film(title = "El Señor de los Anillos", director = "Peter Jackson", year = 2001, genre = "Acción", format = "Blu-Ray", comments = "Épica adaptación de Tolkien."),
            Film(title = "Interestelar", director = "Christopher Nolan", year = 2014, genre = "Sci-Fi", format = "Blu-Ray", comments = "Viajes espaciales y relatividad."),
            Film(title = "El Rey León", director = "Roger Allers", year = 1994, genre = "Drama", format = "Online", comments = "Clásico de animación de Disney."),
            Film(title = "Matrix", director = "The Wachowskis", year = 1999, genre = "Sci-Fi", format = "DVD", comments = "¿Qué es real? Filosofía y acción."),
            Film(title = "Titanic", director = "James Cameron", year = 1997, genre = "Drama", format = "Online", comments = "Romance épico y tragedia histórica."),
            Film(title = "Forrest Gump", director = "Robert Zemeckis", year = 1994, genre = "Drama", format = "DVD", comments = "La vida es como una caja de bombones."),
            Film(title = "El Silencio de los Corderos", director = "Jonathan Demme", year = 1991, genre = "Terror", format = "Blu-Ray", comments = "Thriller psicológico perturbador."),
            Film(title = "Gladiator", director = "Ridley Scott", year = 2000, genre = "Acción", format = "Blu-Ray", comments = "Honor y venganza en Roma.")
        )
        viewModelScope.launch {
            try {
                examples.forEach { film ->
                    val id = repository.addFilm(film)
                    _films.value = _films.value + film.copy(id = id)
                }
            } catch (_: Exception) {}
        }
    }

    // ── Imagen local ──────────────────────────────────────────────────────────

    fun saveFilmImageLocally(context: Context, imageUri: Uri, film: Film) {
        viewModelScope.launch {
            _imageUploading.value = true
            try {
                val localPath = repository.saveImageLocally(context, imageUri)
                    ?: return@launch
                repository.updateFilmImageUrl(film.id, localPath)
                val updated = film.copy(imageUrl = localPath)
                _films.value = _films.value.map { if (it.id == film.id) updated else it }
            } catch (_: Exception) {
            } finally {
                _imageUploading.value = false
            }
        }
    }
}