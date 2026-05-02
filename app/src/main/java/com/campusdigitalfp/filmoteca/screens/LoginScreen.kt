package com.campusdigitalfp.filmoteca.screens

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import androidx.activity.result.IntentSenderRequest
import android.content.Context


private lateinit var oneTapClient: SignInClient
private val auth: FirebaseAuth = FirebaseAuth.getInstance()

fun initGoogleSignIn(context: Context) {
    oneTapClient = Identity.getSignInClient(context)
}

/**
 * Función suspendida que inicia el flujo de autenticación con Google
 * y devuelve un IntentSenderRequest
 */
suspend fun signInWithGoogle(signInRequest: com.google.android.gms.auth.api.identity.BeginSignInRequest): IntentSenderRequest? {
    return try {
        val result = oneTapClient.beginSignIn(signInRequest).await()
        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
    } catch (e: Exception) {
        null
    }
}

/**
 * Función suspendida que maneja el resultado del inicio de sesión
 * con Google y lo autentica en Firebase
 */
suspend fun handleGoogleSignInResult(credential: SignInCredential): Boolean {
    val googleIdToken = credential.googleIdToken ?: return false

    val firebaseCredential = GoogleAuthProvider
        .getCredential(googleIdToken, null)

    return try {
        auth.signInWithCredential(firebaseCredential).await()
        true
    } catch (e: Exception) {
        false
    }
}