package com.app.planify.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OnboardingViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var name by mutableStateOf("")
        private set
    var career by mutableStateOf("")
        private set
    var university by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onNameChange(value: String) {
        name = value
        errorMessage = null
    }

    fun onCareerChange(value: String) {
        career = value
    }

    fun onUniversityChange(value: String) {
        university = value
    }

    fun complete(onNavigate: () -> Unit) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        if (name.isBlank()) {
            errorMessage = "El nombre es obligatorio"
            return
        }

        isLoading = true
        errorMessage = null

        val profile = hashMapOf(
            "name" to name.trim(),
            "major" to career.trim().ifBlank { null },
            "uniName" to university.trim().ifBlank { null },
            "email" to firebaseAuth.currentUser?.email
        )

        firestore.collection("users")
            .document(userId)
            .set(profile)
            .addOnSuccessListener {
                isLoading = false
                onNavigate()
            }
            .addOnFailureListener { exception ->
                isLoading = false
                errorMessage = exception.message ?: "No se pudo guardar el perfil"
            }
    }
}
