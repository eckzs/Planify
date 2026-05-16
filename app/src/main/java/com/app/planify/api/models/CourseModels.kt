package com.app.planify.api.models

import com.app.planify.constants.CourseConstants
import com.google.firebase.firestore.DocumentSnapshot

data class Course(
    val id: String = "",
    val name: String = "",
    val color: String = "",
    val userId: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            CourseConstants.FIELD_NAME to name,
            CourseConstants.FIELD_COLOR to color,
            CourseConstants.FIELD_USER_ID to userId
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): Course {
            return Course(
                id = document.id,
                name = document.getString(CourseConstants.FIELD_NAME).orEmpty(),
                color = document.getString(CourseConstants.FIELD_COLOR).orEmpty(),
                userId = document.getString(CourseConstants.FIELD_USER_ID).orEmpty()
            )
        }
    }
}
