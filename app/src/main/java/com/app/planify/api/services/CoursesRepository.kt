package com.app.planify.api.services

import com.app.planify.api.models.Course
import com.app.planify.constants.CourseConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CoursesRepository constructor() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val coursesCollection = firestore.collection(CourseConstants.COLLECTION)

    suspend fun getCourses(): Result<List<Course>> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        coursesCollection
            .whereEqualTo(CourseConstants.FIELD_USER_ID, userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val courses = snapshot.documents.map { Course.fromDocument(it) }
                continuation.resume(Result.success(courses))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun getCourseById(courseId: String): Result<Course> = suspendCancellableCoroutine { continuation ->
        coursesCollection
            .document(courseId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    continuation.resume(Result.success(Course.fromDocument(document)))
                } else {
                    continuation.resume(Result.failure(Exception("Curso no encontrado")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun createCourse(name: String, teacherName: String, color: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        val course = Course(
            name = name.trim(),
            teacherName = teacherName.trim(),
            color = color,
            userId = userId
        )

        coursesCollection
            .add(course.toMap())
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }
}
