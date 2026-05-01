package com.campusdigitalfp.filmoteca

import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusdigitalfp.filmoteca.common.FilmDataSource

import com.campusdigitalfp.filmoteca.common.Logger
import com.campusdigitalfp.filmoteca.navigation.Navigation
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import com.campusdigitalfp.filmoteca.viewmodel.FilmViewModel
import com.google.firebase.Firebase
import  com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de Firestore
        val db = FirebaseFirestore.getInstance()

        // Configura firestore sin almacenamiento en cache que persista
        val settings = FirebaseFirestoreSettings.Builder().setLocalCacheSettings(MemoryCacheSettings.newBuilder()
            .build()).build()
        //utiliza solo RAM

        db.firestoreSettings = settings //Se aplica la configuración a la db

        enableEdgeToEdge()
        // Limpiar logs de ejecuciones antiguas
        Logger.clearLogs(this)
        setContent {
            // Crea una instancia de HabitViewModel
            val viewmodel: FilmViewModel = viewModel()

            //Se inicia la navegacion en la app y pasa el viewmodel
            Navigation(viewModel)
        }
    }
}

