package com.app.planify.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    var email by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(value: String) {
        email = value
    }

    fun continuar(onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            errorMessage = null
            // TODO: POST /auth/v1/otp { email }
            // TODO: el backend devuelve siempre la misma respuesta
            onNavigate(email)
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null

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
