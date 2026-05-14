package com.app.planify.logic.utils

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object EmailLinkAuthHandler {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow<EmailLinkState?>(null)
    val state: StateFlow<EmailLinkState?> = _state.asStateFlow()

    fun handleIntent(context: Context, intent: Intent?) {
        val link = intent?.data?.toString() ?: return
        if (!firebaseAuth.isSignInWithEmailLink(link)) return

        val email = EmailLinkPrefs.getEmail(context)
        if (email.isNullOrBlank()) {
            _state.value = EmailLinkState.Error("Abre el link desde el mismo celular donde escribiste tu correo.")
            return
        }

        firebaseAuth.signInWithEmailLink(email, link)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    EmailLinkPrefs.clearEmail(context)
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true
                    _state.value = EmailLinkState.Success(isNewUser)
                } else {
                    _state.value = EmailLinkState.Error(
                        task.exception?.message ?: "No se pudo iniciar sesion con el link."
                    )
                }
            }
    }

    fun clearState() {
        _state.value = null
    }
}

sealed class EmailLinkState {
    data class Success(val isNewUser: Boolean) : EmailLinkState()
    data class Error(val message: String) : EmailLinkState()
}
