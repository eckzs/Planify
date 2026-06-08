package com.app.planify.api.client

import com.app.planify.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction

object GeminiClient {

    private const val BASE_SYSTEM_INSTRUCTION =
        "Eres un asistente de estudio para estudiantes universitarios. " +
        "Eres amigable, claro y conciso. Ayudas a entender conceptos, " +
        "resolver dudas académicas y prepararse para exámenes. " +
        "Cuando el estudiante pida generar o crear flashcards, usa la función generate_flashcards. " +
        "Responde siempre en español a menos que el estudiante escriba en otro idioma."

    // Modelo básico: explicaciones, generación directa (sin tools)
    val model: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content { text(BASE_SYSTEM_INSTRUCTION) }
        )
    }

    private val generateFlashcardsTool = Tool(
        functionDeclarations = listOf(
            defineFunction(
                name = "generate_flashcards",
                description = "Genera y guarda tarjetas de estudio (flashcards) en un curso del estudiante. " +
                    "Úsalo cuando el estudiante pida crear, generar o agregar flashcards sobre un tema.",
                parameters = Schema.obj(
                    properties = mapOf(
                        "topic" to Schema.str("El tema sobre el que generar las flashcards"),
                        "count" to Schema.int("Número de flashcards: 5, 10 o 15"),
                        "course_name" to Schema.str("Nombre del curso donde guardar las tarjetas, tal como lo mencionó el estudiante")
                    )
                )
            )
        )
    )

    // Modelo de chat: incluye el tool generate_flashcards y el listado de cursos del usuario
    fun createChatModel(courseNames: List<String>): GenerativeModel {
        val coursesContext = if (courseNames.isEmpty()) {
            "El estudiante aún no tiene cursos creados."
        } else {
            "Los cursos del estudiante son: ${courseNames.joinToString(", ")}."
        }
        return GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            tools = listOf(generateFlashcardsTool),
            systemInstruction = content { text("$BASE_SYSTEM_INSTRUCTION\n\n$coursesContext") }
        )
    }
}
