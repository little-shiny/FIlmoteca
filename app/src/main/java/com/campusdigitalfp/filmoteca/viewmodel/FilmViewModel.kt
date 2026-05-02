package com.campusdigitalfp.filmoteca.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.repository.FilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilmViewModel : ViewModel() {

    private val repository = FilmRepository()

    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    /** true mientras se está guardando la imagen */
    private val _imageUploading = MutableStateFlow(false)
    val imageUploading: StateFlow<Boolean> = _imageUploading

    init { loadFilms() }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun loadFilms() {
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

    // ── Imagen local ──────────────────────────────────────────────────────────

    /**
     * Guarda la imagen capturada en el almacenamiento interno de la app
     * y actualiza el campo imageUrl de la película en Firestore con la
     * ruta absoluta local
     */
    fun saveFilmImageLocally(context: Context, imageUri: Uri, film: Film) {
        viewModelScope.launch {
            _imageUploading.value = true
            try {
                // 1. Copiar al almacenamiento interno
                val localPath = repository.saveImageLocally(context, imageUri)
                    ?: return@launch

                // 2. Persistir la ruta en Firestore
                repository.updateFilmImageUrl(film.id, localPath)

                // 3. Actualizar el estado local para refrescar la UI
                val updated = film.copy(imageUrl = localPath)
                _films.value = _films.value.map { if (it.id == film.id) updated else it }

            } catch (_: Exception) {
            } finally {
                _imageUploading.value = false
            }
        }
    }
}