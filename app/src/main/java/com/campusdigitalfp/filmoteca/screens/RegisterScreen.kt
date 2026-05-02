package com.campusdigitalfp.filmoteca.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun registerScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Validación local antes de llamar a Firebase
    fun validate(): String? {
        if (email.isBlank()) return "El correo no puede estar vacío"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "El formato del correo no es válido"
        if (password.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        if (password != confirmPassword) return "Las contraseñas no coinciden"
        return null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título — mismo estilo que LoginScreen
        Text(
            text = "Filmoteca",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null // limpia el error al escribir
            },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            isError = errorMessage != null && email.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = errorMessage != null && password.length < 6,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            label = { Text("Confirmar contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = errorMessage != null && password != confirmPassword,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón registrarse — mismo estilo que el botón principal de LoginScreen
        Button(
            onClick = {
                val validationError = validate()
                if (validationError != null) {
                    errorMessage = validationError
                    return@Button
                }
                isLoading = true
                scope.launch {
                    try {
                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, password)
                            .await()
                        // Registro exitoso: navega a la lista limpiando todo el backstack
                        navController.navigate("list") {
                            popUpTo("login") { inclusive = true }
                        }
                    } catch (e: Exception) {
                        errorMessage = when {
                            e.message?.contains("email address is already in use") == true ->
                                "Este correo ya está registrado"
                            e.message?.contains("badly formatted") == true ->
                                "El formato del correo no es válido"
                            else -> e.message ?: "Error al crear la cuenta"
                        }
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Crear cuenta")
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

        // Volver al login — mismo estilo que el TextButton de LoginScreen
        TextButton(onClick = { navController.popBackStack() }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}