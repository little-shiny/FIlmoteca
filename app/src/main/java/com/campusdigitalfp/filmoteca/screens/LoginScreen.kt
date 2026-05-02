package com.campusdigitalfp.filmoteca.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ── Variables globales de autenticación ──────────────────────────────────────

private lateinit var oneTapClient: SignInClient
private val auth: FirebaseAuth = FirebaseAuth.getInstance()

fun initGoogleSignIn(context: Context) {
    oneTapClient = Identity.getSignInClient(context)
}

// ── Funciones suspendidas ─────────────────────────────────────────────────────

/**
 * Inicia el flujo de autenticación con Google y devuelve un IntentSenderRequest
 */
suspend fun signInWithGoogle(signInRequest: BeginSignInRequest): IntentSenderRequest? {
    return try {
        val result = oneTapClient.beginSignIn(signInRequest).await()
        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
    } catch (e: Exception) {
        null
    }
}

/**
 * Maneja el resultado del inicio de sesión con Google y lo autentica en Firebase
 */
suspend fun handleGoogleSignInResult(credential: SignInCredential): Boolean {
    val googleIdToken = credential.googleIdToken ?: return false

    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

    return try {
        auth.signInWithCredential(firebaseCredential).await()
        true
    } catch (e: Exception) {
        false
    }
}

fun logout() {
    auth.signOut()
}

// ── Composable de pantalla de login ──────────────────────────────────────────

@Composable
fun LoginScreen(
    navController: NavHostController,
    signInRequest: BeginSignInRequest// se pasa desde fuera (ViewModel o Activity)
) {

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Launcher para recibir el resultado del intent de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()

    ) { result ->
        scope.launch {
            try {
                // Obtiene las credenciales de Google desde el resultado
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val success = handleGoogleSignInResult(credential)
                if (success) {
                    navController.navigate("list")
                } else {
                    errorMessage = "Error al autenticar con Google"
                }
            } catch (e: Exception) {
                errorMessage = "Error al obtener credenciales: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Botón de inicio de sesión con Google
    Button(
        onClick = {
            isLoading = true
            scope.launch {
                // Obtiene el IntentSenderRequest e inicia el launcher
                val intentSenderRequest = signInWithGoogle(signInRequest)
                if (intentSenderRequest != null) {
                    googleSignInLauncher.launch(intentSenderRequest)
                } else {
                    errorMessage = "No se pudo iniciar sesión con Google"
                    isLoading = false
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),

        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text("Iniciar sesión con Google")
    }

    if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
    }
}