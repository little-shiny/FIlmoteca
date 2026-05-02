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
 * Composable reutilizable que gestiona la captura de imagen desde la cámara.
 *
 * Al capturar, llama a [FilmViewModel.saveFilmImageLocally] que guarda la imagen
 * en filesDir y persiste la ruta en Firestore
 */
@Composable
fun CameraCapture(film: Film, viewModel: FilmViewModel) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.saveFilmImageLocally(context, imageUri!!, film)
        }
    }

    fun createImageFile(): Uri? = try {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("IMG_${ts}_", ".jpg", dir)
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } catch (e: IOException) {
        Log.e("CameraCapture", "Error al crear archivo de imagen", e)
        null
    }

    val isUploading by viewModel.imageUploading.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUploading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Guardando imagen…", style = MaterialTheme.typography.bodySmall)
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
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isUploading
        ) {
            Text(if (!hasCameraPermission) "Conceder permiso de cámara" else "Hacer foto de la película")
        }
    }
}