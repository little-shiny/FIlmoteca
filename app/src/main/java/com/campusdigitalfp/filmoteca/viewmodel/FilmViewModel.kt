package com.campusdigitalfp.filmoteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusdigitalfp.filmoteca.models.Film
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FilmViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    // Referencia a la colección del usuario actual:
    // /users/{uid}/films  →  cada usuario tiene sus propias películas
    private fun userFilmsCollection() =
        db.collection("users")
            .document(auth.currentUser?.uid ?: "anonymous")
            .collection("films")

    init {
        loadFilms()
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /** Carga todas las películas del usuario autenticado desde Firestore */
    fun loadFilms() {
        viewModelScope.launch {
            try {
                val snapshot = userFilmsCollection().get().await()
                _films.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Film::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                _films.value = emptyList()
            }
        }
    }

    /** Añade una película nueva a la colección del usuario */
    fun addFilm(film: Film) {
        viewModelScope.launch {
            try {
                val docRef = userFilmsCollection().add(film).await()
                // Actualiza la lista local añadiendo la película con su id de Firestore
                _films.value = _films.value + film.copy(id = docRef.id)
            } catch (e: Exception) {
                // Manejo de error: se puede exponer con otro StateFlow si se necesita
            }
        }
    }

    /** Actualiza una película existente */
    fun updateFilm(film: Film) {
        viewModelScope.launch {
            try {
                userFilmsCollection().document(film.id).set(film).await()
                _films.value = _films.value.map { if (it.id == film.id) film else it }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    /** Elimina una película por su id */
    fun deleteFilm(filmId: String) {
        viewModelScope.launch {
            try {
                userFilmsCollection().document(filmId).delete().await()
                _films.value = _films.value.filter { it.id != filmId }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    /** Elimina varias películas a la vez */
    fun deleteFilmsByIds(ids: List<String>) {
        viewModelScope.launch {
            try {
                ids.forEach { id ->
                    userFilmsCollection().document(id).delete().await()
                }
                _films.value = _films.value.filter { it.id !in ids }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }
}