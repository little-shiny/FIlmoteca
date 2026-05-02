package com.campusdigitalfp.filmoteca.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.campusdigitalfp.filmoteca.models.Film
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FilmRepository maneja las operaciones relacionadas con las películas en Firestore
 * y el almacenamiento LOCAL de imágenes
 * Las imágenes se guardan en filesDir/FilmImages/ y la ruta absoluta se persiste
 * en el campo [Film.imageUrl] de Firestore.
 */
class FilmRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userFilmsCollection() =
        db.collection("users")
            .document(auth.currentUser?.uid ?: "anonymous")
            .collection("films")

    // ── CRUD Firestore ────────────────────────────────────────────────────────

    suspend fun addFilm(film: Film): String {
        val docRef = userFilmsCollection().add(film).await()
        return docRef.id
    }

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

    suspend fun updateFilm(film: Film) {
        userFilmsCollection().document(film.id).set(film).await()
    }

    suspend fun deleteFilm(filmId: String) {
        userFilmsCollection().document(filmId).delete().await()
    }

    suspend fun deleteMultipleFilms(ids: List<String>) {
        val batch = db.batch()
        ids.forEach { id -> batch.delete(userFilmsCollection().document(id)) }
        try {
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("FilmRepository", "Error al eliminar películas: ${e.message}")
        }
    }

    // ── Imagen local ──────────────────────────────────────────────────────────

    /**
     * Copia la imagen capturada por la cámara al almacenamiento interno de la app
     * (filesDir/FilmImages/) y devuelve la ruta absoluta del archivo guardado.
     *
     * La ruta se almacena directamente en Firestore como [Film.imageUrl].
     * Coil la carga con Uri.fromFile() → no se necesita Firebase Storage.
     *
     * @param context  Contexto de la aplicación
     * @param imageUri URI temporal de la imagen capturada (FileProvider content://)
     * @return         Ruta absoluta del archivo guardado, o null si hubo error
     */
    fun saveImageLocally(context: Context, imageUri: Uri): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = File(context.filesDir, "FilmImages")

        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("FilmRepository", "No se pudo crear el directorio FilmImages")
            return null
        }

        val file = File(dir, "IMG_$timeStamp.jpg")
        return try {
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            file.absolutePath          // ← esto es lo que guardamos en Firestore
        } catch (e: IOException) {
            Log.e("FilmRepository", "Error al guardar imagen localmente", e)
            null
        }
    }

    /**
     * Actualiza únicamente el campo [imageUrl] del documento Firestore
     * con la ruta local del archivo.
     */
    suspend fun updateFilmImageUrl(filmId: String, localPath: String) {
        userFilmsCollection()
            .document(filmId)
            .update("imageUrl", localPath)
            .await()
    }
}