package com.app.planify.logic.utils

import com.app.planify.api.models.Flashcard
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.math.roundToInt

fun applySpacedRepetition(card: Flashcard, rating: Int): Flashcard {
    val newRepetitions: Int
    val newInterval: Int
    val newEaseFactor: Double

    if (rating >= 3) {
        newRepetitions = card.repetitions + 1
        newInterval = when (card.repetitions) {
            0 -> 1
            1 -> 6
            else -> (card.interval * card.easeFactor).roundToInt()
        }
        newEaseFactor = (card.easeFactor + 0.1 - (5 - rating) * (0.08 + (5 - rating) * 0.02))
            .coerceAtLeast(1.3)
    } else {
        newRepetitions = 0
        newInterval = 1
        newEaseFactor = card.easeFactor
    }

    val nextReviewInstant = LocalDate.now()
        .plusDays(newInterval.toLong())
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()

    return card.copy(
        repetitions = newRepetitions,
        interval = newInterval,
        easeFactor = newEaseFactor,
        nextReview = Timestamp(Date.from(nextReviewInstant))
    )
}
