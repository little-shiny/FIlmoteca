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

    /** Estado de carga de imagen (null = sin carga, true = cargando, false = terminado) */
    private val _imageUploading = MutableStateFlow(false)
    val imageUploading: StateFlow<Boolean> = _imageUploading

    init {
        loadFilms()
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /** Carga todas las películas del usuario desde Firestore */
    fun loadFilms() {
        viewModelScope.launch {
            try {
                _films.value = repository.getFilms()
            } catch (e: Exception) {
                _films.value = emptyList()
            }
        }
    }

    /** Añade una nueva película */
    fun addFilm(film: Film) {
        viewModelScope.launch {
            try {
                val id = repository.addFilm(film)
                _films.value = _films.value + film.copy(id = id)
            } catch (_: Exception) {}
        }
    }

    /** Actualiza una película existente */
    fun updateFilm(film: Film) {
        viewModelScope.launch {
            try {
                repository.updateFilm(film)
                _films.value = _films.value.map { if (it.id == film.id) film else it }
            } catch (_: Exception) {}
        }
    }

    /** Elimina una película por su id */
    fun deleteFilm(filmId: String) {
        viewModelScope.launch {
            try {
                repository.deleteFilm(filmId)
                _films.value = _films.value.filter { it.id != filmId }
            } catch (_: Exception) {}
        }
    }

    /** Elimina varias películas seleccionadas */
    fun deleteSelectedFilms(films: List<Film>) {
        viewModelScope.launch {
            try {
                repository.deleteMultipleFilms(films.map { it.id })
                val ids = films.map { it.id }.toSet()
                _films.value = _films.value.filter { it.id !in ids }
            } catch (_: Exception) {}
        }
    }

    // ── Imagen ────────────────────────────────────────────────────────────────

    /**
     * Guarda la imagen localmente, la sube a Firebase Storage y actualiza
     * el campo [imageUrl] del documento Firestore de la película.
     *
     * @param context   Contexto de la aplicación
     * @param imageUri  URI de la imagen capturada por la cámara
     * @param film      Película cuya imagen se actualizará
     */
    fun uploadFilmImage(context: Context, imageUri: Uri, film: Film) {
        viewModelScope.launch {
            _imageUploading.value = true
            try {
                // Guardar localmente en el almacenamiento interno de la app
                val localUri = repository.saveImageToAppFolder(context, imageUri)
                    ?: return@launch

                // Subir a Firebase Storage y obtener la URL de descarga
                val downloadUrl = repository.uploadImageToStorage(localUri, film.id)
                    ?: return@launch

                // Actualizar el campo imageUrl en Firestore
                repository.updateFilmImageUrl(film.id, downloadUrl)

                //  Actualizar el estado local
                val updatedFilm = film.copy(imageUrl = downloadUrl)
                _films.value = _films.value.map { if (it.id == film.id) updatedFilm else it }

            } catch (_: Exception) {
            } finally {
                _imageUploading.value = false
            }
        }
    }
}