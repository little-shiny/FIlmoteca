package com.campusdigitalfp.filmoteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusdigitalfp.filmoteca.common.Logger
import com.campusdigitalfp.filmoteca.navigation.Navigation
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.AuthViewModel
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de Firestore (solo RAM, sin caché persistente)
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()
        db.firestoreSettings = settings

        enableEdgeToEdge()

        // Limpiar logs de ejecuciones antiguas
        Logger.clearLogs(this)

        setContent {
            FilmotecaTheme {
                val filmViewModel: FilmViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()

                Navigation(viewModel = filmViewModel, authViewModel = authViewModel)
            }
        }
    }
}