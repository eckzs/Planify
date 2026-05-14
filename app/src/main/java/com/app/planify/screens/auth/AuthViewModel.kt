package com.app.planify.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.planify.constants.AuthConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    var email by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
        successMessage = null
    }

    fun sendEmailLink(onEmailSaved: (String) -> Unit) {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) {
            errorMessage = "Escribe tu correo primero"
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl(AuthConstants.EMAIL_LINK_URL)
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                AuthConstants.ANDROID_PACKAGE_NAME,
                true,
                null
            )
            .build()

        firebaseAuth.sendSignInLinkToEmail(cleanEmail, actionCodeSettings)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    onEmailSaved(cleanEmail)
                    successMessage = "Te enviamos un link para entrar. Revisa tu correo."
                } else {
                    errorMessage = task.exception?.message
                        ?: "No se pudo enviar el link de ingreso"
                }
            }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        successMessage = null

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = task.exception?.message
                        ?: "No se pudo iniciar sesion con Google"
                }
            }
    }

    fun showGoogleError() {
        isLoading = false
        errorMessage = "No se pudo iniciar sesion con Google"
    }
}
