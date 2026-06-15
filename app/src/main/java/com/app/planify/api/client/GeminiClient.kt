package com.app.planify.api.client

import com.app.planify.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionDeclaration
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig

object GeminiClient {

    private const val BASE_SYSTEM_INSTRUCTION =
        "Eres un asistente de estudio para estudiantes universitarios. " +
        "Eres amigable, claro y conciso. Ayudas a entender conceptos y prepararse para exámenes. " +
        "Responde siempre en español a menos que el estudiante escriba en otro idioma."

    private const val FLASHCARD_SYSTEM_INSTRUCTION = 
        "$BASE_SYSTEM_INSTRUCTION\n\n" +
        "Tu tarea es generar tarjetas de estudio. " +
        "IMPORTANTE: Responde UNICAMENTE con un JSON array válido. " +
        "No incluyas explicaciones, ni markdown, ni texto fuera del JSON. " +
        "Formato: [{\"front\":\"pregunta\",\"back\":\"respuesta\"}]"

    private val apiKey = BuildConfig.GEMINI_API_KEY

    // Modelo estándar para chat y explicaciones
    val model: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-3.1-flash-lite",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
            },
            systemInstruction = content { text(BASE_SYSTEM_INSTRUCTION) }
        )
    }

    // Modelo especializado en generar Flashcards
    val flashcardModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-3.1-flash-lite",
            apiKey = apiKey,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                temperature = 1.0f
            },
            systemInstruction = content { text(FLASHCARD_SYSTEM_INSTRUCTION) }
        )
    }

    // Herramienta para el chat (Function Calling)
    private val generateFlashcardsTool = Tool(
        listOf(
            FunctionDeclaration(
                name = "generate_flashcards",
                description = "Genera tarjetas de estudio (flashcards) sobre un tema específico.",
                parameters = listOf(
                    Schema.str("topic", "El tema sobre el que generar las flashcards"),
                    Schema.int("count", "Número de flashcards a generar"),
                    Schema.str("courseName", "Nombre del curso")
                ),
                requiredParameters = listOf("topic", "count", "courseName")
            )
        )
    )

    fun createChatModel(courseNames: List<String>): GenerativeModel {
        val coursesContext = "Los cursos del estudiante son: ${courseNames.joinToString(", ")}."
        return GenerativeModel(
            modelName = "gemini-3.1-flash-lite",
            apiKey = apiKey,
            tools = listOf(generateFlashcardsTool),
            systemInstruction = content { text("$BASE_SYSTEM_INSTRUCTION\n\n$coursesContext") }
        )
    }
}
