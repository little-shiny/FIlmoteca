package com.campusdigitalfp.filmoteca.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado del usuario actual — null significa no autenticado
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    // ── Email y contraseña ────────────────────────────────────────────────────

    /**
     * Inicia sesión con email y contraseña.
     * @return null si fue exitoso, o el mensaje de error traducido
     */
    suspend fun loginWithEmail(email: String, password: String): String? {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            _currentUser.value = auth.currentUser
            null
        } catch (e: Exception) {
            translateFirebaseError(e.message)
        }
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     * @return null si fue exitoso, o el mensaje de error traducido
     */
    suspend fun registerWithEmail(email: String, password: String): String? {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            _currentUser.value = auth.currentUser
            null
        } catch (e: Exception) {
            translateFirebaseError(e.message)
        }
    }

    // ── Google Sign-In ────────────────────────────────────────────────────────

    /**
     * Construye el BeginSignInRequest y devuelve el IntentSenderRequest para lanzar el flujo de Google.
     * Devuelve null si no se puede iniciar (p.ej. no hay cuentas Google en el dispositivo).
     */
    suspend fun getGoogleSignInIntent(context: Context): androidx.activity.result.IntentSenderRequest? {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Reemplaza con el Web Client ID de tu proyecto en Firebase Console
                    // -> Authentication > Sign-in method > Google > Web client ID
                    .setServerClientId("TU_WEB_CLIENT_ID")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        return try {
            val result = Identity.getSignInClient(context).beginSignIn(signInRequest).await()
            androidx.activity.result.IntentSenderRequest.Builder(
                result.pendingIntent.intentSender
            ).build()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Autentica en Firebase con las credenciales de Google obtenidas del launcher.
     * @return null si fue exitoso, o el mensaje de error traducido
     */
    suspend fun handleGoogleSignIn(credential: SignInCredential): String? {
        val googleIdToken = credential.googleIdToken
            ?: return "No se pudo obtener el token de Google"
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            auth.signInWithCredential(firebaseCredential).await()
            _currentUser.value = auth.currentUser
            null
        } catch (e: Exception) {
            translateFirebaseError(e.message)
        }
    }

    // ── Acceso anónimo ────────────────────────────────────────────────────────

    /**
     * Inicia sesión de forma anónima.
     * @return null si fue exitoso, o el mensaje de error traducido
     */
    suspend fun signInAnonymously(): String? {
        return try {
            auth.signInAnonymously().await()
            _currentUser.value = auth.currentUser
            null
        } catch (e: Exception) {
            translateFirebaseError(e.message)
        }
    }

    // ── Cerrar sesión ─────────────────────────────────────────────────────────

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Devuelve el UID del usuario actual, o null si no está autenticado */
    fun currentUserId(): String? = auth.currentUser?.uid

    /** Traduce los mensajes de error de Firebase a español */
    private fun translateFirebaseError(message: String?): String {
        return when {
            message == null -> "Error desconocido"
            message.contains("email address is already in use") ->
                "Este correo ya está registrado"
            message.contains("badly formatted") ->
                "El formato del correo no es válido"
            message.contains("no user record") ||
                    message.contains("user-not-found") ->
                "No existe ninguna cuenta con este correo"
            message.contains("password is invalid") ||
                    message.contains("wrong-password") ->
                "Contraseña incorrecta"
            message.contains("too-many-requests") ->
                "Demasiados intentos fallidos. Inténtalo más tarde"
            message.contains("network") ->
                "Error de red. Comprueba tu conexión"
            else -> message
        }
    }
}