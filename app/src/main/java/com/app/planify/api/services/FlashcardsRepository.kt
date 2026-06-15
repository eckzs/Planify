package com.app.planify.api.services

import com.app.planify.api.models.Flashcard
import com.app.planify.constants.FlashcardConstants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FlashcardsRepository constructor() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val flashcardsCollection = firestore.collection(FlashcardConstants.COLLECTION)

    suspend fun getCardsByCourse(courseId: String): Result<List<Flashcard>> = suspendCancellableCoroutine { continuation ->
        flashcardsCollection
            .whereEqualTo(FlashcardConstants.FIELD_COURSE_ID, courseId)
            .get()
            .addOnSuccessListener { snapshot ->
                val cards = snapshot.documents.map { Flashcard.fromDocument(it) }
                continuation.resume(Result.success(cards))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun createCard(courseId: String, front: String, back: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        val card = Flashcard(
            userId = userId,
            courseId = courseId,
            front = front.trim(),
            back = back.trim(),
            nextReview = Timestamp.now()
        )

        flashcardsCollection
            .add(card.toMap())
            .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
    }

    suspend fun updateCard(card: Flashcard): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val updates = mapOf(
            FlashcardConstants.FIELD_NEXT_REVIEW to card.nextReview,
            FlashcardConstants.FIELD_INTERVAL to card.interval,
            FlashcardConstants.FIELD_REPETITIONS to card.repetitions,
            FlashcardConstants.FIELD_EASE_FACTOR to card.easeFactor
        )

        flashcardsCollection
            .document(card.id)
            .update(updates)
            .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
    }

    suspend fun updateCardContent(cardId: String, front: String, back: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val updates = mapOf(
            FlashcardConstants.FIELD_FRONT to front.trim(),
            FlashcardConstants.FIELD_BACK to back.trim()
        )

        flashcardsCollection
            .document(cardId)
            .update(updates)
            .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
    }

    suspend fun deleteCard(cardId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        flashcardsCollection
            .document(cardId)
            .delete()
            .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
    }
}
