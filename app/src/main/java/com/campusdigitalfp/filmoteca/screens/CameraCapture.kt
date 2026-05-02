package com.campusdigitalfp.filmoteca.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.campusdigitalfp.filmoteca.models.Film
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable que gestiona la captura de imágenes desde la cámara para una película.
 *
 * orden de acciones:
 * ccomprueba el permiso de cámara.
 * Si no está concedido, lo solicita.
 * Crea un archivo temporal y lanza la cámara.
 * Al capturar, llama a [FilmViewModel.uploadFilmImage] para guardar localmente
 *    y subir a Firebase Storage, actualizando el documento Firestore de la película.
 *
 * @param film      Película cuya imagen se va a capturar/actualizar.
 * @param viewModel ViewModel que gestiona la lógica de películas e imágenes.
 */
@Composable
fun CameraCapture(
    film: Film,
    viewModel: FilmViewModel
) {
    val context = LocalContext.current

    // URI temporal de la imagen capturada por la cámara
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // ¿Tiene permiso de cámara?
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador para solicitar el permiso de cámara en tiempo de ejecución
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Lanzador de la cámara que al terminar, sube la imagen si la captura fue exitosa
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.uploadFilmImage(context, imageUri!!, film)
        }
    }

    // Función que crea el archivo temporal y devuelve su URI segura (FileProvider)
    fun createImageFile(): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            Log.e("CameraCapture", "Error al crear el archivo de imagen", e)
            null
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    val isUploading by viewModel.imageUploading.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUploading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Subiendo imagen…", style = MaterialTheme.typography.bodySmall)
            }
        }

        Button(
            onClick = {
                if (!hasCameraPermission) {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    createImageFile()?.let { uri ->
                        imageUri = uri
                        cameraLauncher.launch(uri)
                    } ?: Log.e("CameraCapture", "No se pudo crear el archivo de imagen")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isUploading
        ) {
            Text(text = if (!hasCameraPermission) "Conceder permiso de cámara" else "Hacer foto de la película")
        }
    }
}