package com.campusdigitalfp.filmoteca.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.campusdigitalfp.filmoteca.models.Film
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FilmRepository maneja las operaciones relacionadas con las películas en Firestore
 * y las imágenes en Firebase Storage.
 * Actúa como capa intermedia entre la BBDD y el ViewModel.
 */
class FilmRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Referencia dinámica a la colección del usuario autenticado
    private fun userFilmsCollection() =
        db.collection("users")
            .document(auth.currentUser?.uid ?: "anonymous")
            .collection("films")

    // Referencia dinámica al bucket de Storage del usuario
    private fun userStorageRef() =
        storage.reference
            .child("users/${auth.currentUser?.uid ?: "anonymous"}/films")

    // ── CRUD Firestore ────────────────────────────────────────────────────────

    /** Agrega una nueva película */
    suspend fun addFilm(film: Film): String {
        val docRef = userFilmsCollection().add(film).await()
        return docRef.id
    }

    /** Recupera todas las películas del usuario */
    suspend fun getFilms(): List<Film> {
        return try {
            val snapshot = userFilmsCollection().get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Film::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("FilmRepository", "Error al obtener películas: ${e.message}")
            emptyList()
        }
    }

    /** Actualiza una película existente */
    suspend fun updateFilm(film: Film) {
        userFilmsCollection().document(film.id).set(film).await()
    }

    /** Elimina una película por su id */
    suspend fun deleteFilm(filmId: String) {
        userFilmsCollection().document(filmId).delete().await()
    }

    /** Elimina varias películas en una sola operación batch */
    suspend fun deleteMultipleFilms(ids: List<String>) {
        val batch = db.batch()
        ids.forEach { id ->
            batch.delete(userFilmsCollection().document(id))
        }
        try {
            batch.commit().await()
            Log.i("FilmRepository", "Películas eliminadas correctamente")
        } catch (e: Exception) {
            Log.e("FilmRepository", "Error al eliminar películas: ${e.message}")
        }
    }

    // ── Imagen ────────────────────────────────────────────────────────────────

    /**
     * Guarda la imagen en el almacenamiento interno de la app (carpeta privada).
     * Devuelve la URI del archivo guardado, o null si hubo error.
     */
    fun saveImageToAppFolder(context: Context, imageUri: Uri): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val directory = File(context.filesDir, "FilmImages")

        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("FilmRepository", "No se pudo crear el directorio de imágenes")
            return null
        }

        val file = File(directory, "IMG_$timeStamp.jpg")
        return try {
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: IOException) {
            Log.e("FilmRepository", "Error al guardar la imagen localmente", e)
            null
        }
    }

    /**
     * Sube la imagen al bucket de Firebase Storage asociado al usuario
     * y devuelve la URL de descarga pública, o null si hubo error.
     *
     * @param localUri  URI del archivo local (obtenida de [saveImageToAppFolder])
     * @param filmId    ID de la película a la que pertenece la imagen
     */
    suspend fun uploadImageToStorage(localUri: Uri, filmId: String): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageRef = userStorageRef().child("$filmId/IMG_$timeStamp.jpg")

            imageRef.putFile(localUri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("FilmRepository", "Error al subir imagen a Storage: ${e.message}")
            null
        }
    }

    /**
     * Actualiza únicamente el campo [imageUrl] del documento Firestore de la película.
     */
    suspend fun updateFilmImageUrl(filmId: String, imageUrl: String) {
        userFilmsCollection().document(filmId)
            .update("imageUrl", imageUrl)
            .await()
    }
}