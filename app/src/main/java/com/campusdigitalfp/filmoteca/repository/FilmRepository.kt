package com.campusdigitalfp.filmoteca.repository

import android.util.Log
import com.campusdigitalfp.filmoteca.models.Film
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * FilmRepository maneja las operaciones relacionadas con las películas en Firestore
 * Actúa como capa intermedia entre la BBDD y el ViewModel
 */
class FilmRepository {
    //Obtención de una instancia de Firestore
    private val db = FirebaseFirestore.getInstance()
    // Referencia a la colección de `films` en firestore:
    private val filmsCollection = db.collection("films")

    /**
     * Método que agrega una nueva película a Firestore
     * se suspende para actuar en el fondo
     */
    suspend fun addFilm(film: Film){
        filmsCollection.add(film).await()
    }

    /**
     * Función que recupera las peliculas en Firestore y las devuleve como una lista
     * usa Await() para esperar a que termine antes de seguir
     * Si hay un error devuelve una lista vacia en luigar de una excepción
     */
    suspend fun getFilms(): List<Film>{
        return try {
            val snapshot = filmsCollection.get().await()

            // Obtiene los resultados de firestore
            snapshot.documents.mapNotNull {
                it.toObject(Film::class.java)?.copy(id = it.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Actualiza una pelicula en firestore
     * usa set para sobreescribir los datos
     */
    suspend fun updateFilm(film: Film){
        filmsCollection.document(film.id).set(film).await()
    }

    /**
     * Elimina un habito en firestore mediante la id
     */
    suspend fun deleteFilm(filmId: String){
        filmsCollection.document(filmId).delete().await()
    }

    /**
     * Escucha los cambios emn la coleccion de firestore para que cuando se detecte algun cambio en firestore se
     * actualice pasandole al callback onUpodate
     */
    fun listenToFilmsUpdates(onUpdate: (List<Film>) -> Unit) {
        filmsCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("HS_error", "Error al obtener películas : ${exception.message}")
                return@addSnapshotListener
            }
            // Convierte las peliculas de firestore a objetos film
            val films = snapshot?.documents?.mapNotNull { it.toObject(Film::class.java)?.copy(id = it.id) } ?: emptyList()
            onUpdate(films) // Llama al callback con la lista actualizada
        }
    }

    /**
     * Agrega multiples peliculas en una operacion con writebach, optimizando el rendimiento
     */
    suspend fun addMultipleFilms(films: List<Film>){
        val batch = db.batch()

        films.forEach{ film ->
            val newDocRef = filmsCollection.document() // Crea un nuevo id para cada pelicula
            batch.set(newDocRef, film.copy(id = newDocRef.id)) // Se guarda con su nueva id
        }
        try {
            batch.commit().await() //ejecuta la operacion
            Log.i("HS_info", "10 peliculas añadidas correctamente a Firestore")
        } catch(e: Exception){
            Log.e("Hs_error", "Error al añadir películas: ${e.message}")
        }
    }

    /**
     * Elimina multiples peliculas en una sola trasnaccion con batch
     */
    suspend fun deleteMultipleFilms(films: List<Film>){
        val batch = db.batch()
        films.forEach{ film ->
            film.id.let{ filmId ->
                batch.delete(filmsCollection.document(filmId))
            }
        }

        try {
            batch.commit().await()
            Log.i("HS_info", "peliculas eliminadas correctamente")
        }catch(e: Exception){
            Log.e("HS_error", "Error al eliminar peliculas: ${e.message}")
        }
    }
}