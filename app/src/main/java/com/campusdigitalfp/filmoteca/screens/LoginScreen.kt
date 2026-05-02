package com.campusdigitalfp.filmoteca.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.viewmodel.AuthViewModel
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

suspend fun signInWithGoogle(signInRequest: BeginSignInRequest): IntentSenderRequest? {
    return try {
        val result = oneTapClient.beginSignIn(signInRequest).await()
        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
    } catch (e: Exception) {
        null
    }
}

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

// ── Pantalla de Login ─────────────────────────────────────────────────────────

@Composable
fun loginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Launcher para el resultado del intent de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            scope.launch {
                try {
                    val credential = Identity.getSignInClient(context)
                        .getSignInCredentialFromIntent(result.data)
                    val success = handleGoogleSignInResult(credential)
                    if (success) {
                        navController.navigate("FilmListScreen")
                    } else {
                        errorMessage = "Error al autenticar con Google"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error al obtener credenciales: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
            errorMessage = "Inicio de sesión con Google cancelado"
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título
        Text(
            text = "Filmoteca",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón email/contraseña
        Button(
            onClick = {
                isLoading = true
                scope.launch {
                    try {
                        auth.signInWithEmailAndPassword(email, password).await()
                        navController.navigate("FilmListScreen")
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error al iniciar sesión"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Google
        OutlinedButton(
            onClick = {
                isLoading = true
                scope.launch {
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
            enabled = !isLoading
        ) {
            Text("Iniciar Sesión con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón anónimo
        TextButton(
            onClick = {
                isLoading = true
                scope.launch {
                    try {
                        auth.signInAnonymously().await()
                        navController.navigate("FilmListScreen")
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error al acceder como invitado"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(
                "Acceder como invitado",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Indicador de carga
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Mensaje de error
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ir a registro
        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}