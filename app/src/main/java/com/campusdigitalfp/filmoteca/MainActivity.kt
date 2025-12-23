package com.campusdigitalfp.filmoteca

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.campusdigitalfp.filmoteca.common.Logger
import com.campusdigitalfp.filmoteca.navigation.Navigation
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Limpiar logs de ejecuciones antiguas
        Logger.clearLogs(this)
        setContent {
            FilmotecaTheme {
                Navigation()
            }
        }
    }
}

