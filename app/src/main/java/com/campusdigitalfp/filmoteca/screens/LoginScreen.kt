package com.campusdigitalfp.filmoteca.screens

import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

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

    fun goToList() {
        navController.navigate("list") {
            popUpTo("login") { inclusive = true }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            scope.launch {
                try {
                    val credential = Identity.getSignInClient(context)
                        .getSignInCredentialFromIntent(result.data)
                    val error = authViewModel.handleGoogleSignIn(credential)
                    if (error == null) goToList() else errorMessage = error
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Email / contraseña
        Button(
            onClick = {
                isLoading = true
                scope.launch {
                    val error = authViewModel.loginWithEmail(email, password)
                    isLoading = false
                    if (error == null) goToList() else errorMessage = error
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) { Text("Iniciar Sesión") }

        Spacer(modifier = Modifier.height(8.dp))

        // Google
        OutlinedButton(
            onClick = {
                isLoading = true
                scope.launch {
                    val intentSenderRequest = authViewModel.getGoogleSignInIntent(context)
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
        ) { Text("Iniciar Sesión con Google") }

        Spacer(modifier = Modifier.height(8.dp))

        // Anónimo
        TextButton(
            onClick = {
                isLoading = true
                scope.launch {
                    val error = authViewModel.signInAnonymously()
                    isLoading = false
                    if (error == null) goToList() else errorMessage = error
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Acceder como invitado", color = MaterialTheme.colorScheme.secondary)
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}