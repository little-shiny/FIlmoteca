package com.campusdigitalfp.filmoteca.common

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Logger {

    private const val LOG_FILE_NAME = "filmoteca_logs.txt"

    // Borrar logs anteriores (al iniciar la app)
    fun clearLogs(context: Context) {
        val file = File(context.filesDir, LOG_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }

    // Guardar un log
    fun log(context: Context, tag: String, message: String) {
        // Logcat
        android.util.Log.i(tag, message)

        // Archivo
        try {
            val file = File(context.filesDir, LOG_FILE_NAME)
            val writer = FileWriter(file, true)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            writer.append("$timestamp [$tag] $message\n")
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
