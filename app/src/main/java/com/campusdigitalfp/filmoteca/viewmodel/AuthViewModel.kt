package com.campusdigitalfp.filmoteca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

/**
 * Clase utilizada para manejar la autenticación dentro de la aplicación
 * Su objetivo es separar la lógica de la autenticación de la interfaz de usuario
 * etiende AndroidViewModel para proporcionar el acceso al contesto de la app
 */
class AuthViewModel (application: Application): AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // Inicia el proceso de la creación de un usuario y contraseña en firebase auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Se añade un listener para detectar cuando acaba el proceso
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    // Si hay error
                    val errorMessage = translateErrorFirebase(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }

    private fun translateErrorFirebase(exception: Exception?): String {
        // Se verifica el tipo de excepcion
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El email no tiene un formato válido"
            "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta o el usuario no tiene contraseña"
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este corre o."
            "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada."
            "ERROR_TOO_MANY_REQUESTS" -> "Has realizado demasiados intent os, intenta más tarde."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado en otra cuenta."
            "ERROR_NETWORK_REQUEST_FAILED" -> "No se pudo conectar a la r ed. Verifica tu conexión."
            "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil. U sa una más segura."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "Este correo ya está registrado con otro método de inicio de sesión."
            else -> "Ocurrió un error desconocido, Intente nuevamente"
        }
    }

    /**
     * Inicio de sesión
     */
    fun loginUser(email: String?, password: String?, onResult: (Boolean, String?) -> Unit) {
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            onResult(false, "El correo y la contraseña no pueden estar vacíos.")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorMessage = translateErrorFirebase(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }

    /**
     * Inicio de sesión con Google (OneTap, solo funciona con cuentas registradas en el dispositivo)
     * Si no hay, no funciona
     */
    // Se crea una instancia de BeginSignInRequest para iniciar el proceso de autenticación con Google
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(

    // Configuración de la solicitud de to ken de ID de Google
    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
        .setSupported(true)
        // Habilita el soporte para autenticación con Google
        .setServerClientId("579991462040-47j30k8rke093ov130r5mjivp14ang74.apps.googleusercontent.com")
        // Establece el Client ID del servidor, necesario para verificar el token de ID en el backend
        .setFilterByAuthorizedAccounts(false)
        // Permite mostrar todas las cuentas de Google disponibles en el dis positivo, en lugar de restringirse solo
        // a cuentas que ya han iniciado sesión previamente en la app
        .build()
        // Construye la configuración de solicitud de token de Google
        )
    .build()


}