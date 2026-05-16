package com.app.planify.api.models

import com.app.planify.constants.FlashcardConstants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class Flashcard(
    val id: String = "",
    val userId: String = "",
    val courseId: String = "",
    val front: String = "",
    val back: String = "",
    val nextReview: Timestamp = Timestamp.now(),
    val interval: Int = 0,
    val repetitions: Int = 0,
    val easeFactor: Double = 2.5
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            FlashcardConstants.FIELD_USER_ID to userId,
            FlashcardConstants.FIELD_COURSE_ID to courseId,
            FlashcardConstants.FIELD_FRONT to front,
            FlashcardConstants.FIELD_BACK to back,
            FlashcardConstants.FIELD_NEXT_REVIEW to nextReview,
            FlashcardConstants.FIELD_INTERVAL to interval,
            FlashcardConstants.FIELD_REPETITIONS to repetitions,
            FlashcardConstants.FIELD_EASE_FACTOR to easeFactor
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): Flashcard {
            return Flashcard(
                id = document.id,
                userId = document.getString(FlashcardConstants.FIELD_USER_ID).orEmpty(),
                courseId = document.getString(FlashcardConstants.FIELD_COURSE_ID).orEmpty(),
                front = document.getString(FlashcardConstants.FIELD_FRONT).orEmpty(),
                back = document.getString(FlashcardConstants.FIELD_BACK).orEmpty(),
                nextReview = document.getTimestamp(FlashcardConstants.FIELD_NEXT_REVIEW) ?: Timestamp.now(),
                interval = document.getLong(FlashcardConstants.FIELD_INTERVAL)?.toInt() ?: 0,
                repetitions = document.getLong(FlashcardConstants.FIELD_REPETITIONS)?.toInt() ?: 0,
                easeFactor = document.getDouble(FlashcardConstants.FIELD_EASE_FACTOR) ?: 2.5
            )
        }
    }
}
