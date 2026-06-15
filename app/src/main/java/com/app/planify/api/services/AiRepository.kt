package com.app.planify.api.services

import android.util.Log
import com.app.planify.api.client.GeminiClient
import com.app.planify.api.models.ChatMessage
import com.app.planify.api.models.GeneratedFlashcard
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.content
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiRepository {

    // Chat general sin tools (usado solo en explicaciones)
    suspend fun sendMessage(history: List<ChatMessage>, newMessage: String): Result<String> = try {
        val chat = GeminiClient.model.startChat(
            history = history.map { msg -> content(role = msg.role) { text(msg.text) } }
        )
        val response = chat.sendMessage(newMessage)
        Result.success(response.text ?: "")
    } catch (e: IOException) {
        Result.failure(Exception("Sin conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Error desconocido"))
    }

    // Chat con function calling: el modelo puede invocar generate_flashcards
    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        newMessage: String,
        courseNames: List<String>,
        onFunctionCall: suspend (name: String, args: Map<String, String>) -> String
    ): Result<String> = try {
        val chatModel = GeminiClient.createChatModel(courseNames)
        val chat = chatModel.startChat(
            history = history.map { msg -> content(role = msg.role) { text(msg.text) } }
        )

        var response = chat.sendMessage(newMessage)

        // Manejar function call si Gemini decide usar la herramienta
        val functionCall = response.functionCalls.firstOrNull()

        if (functionCall != null) {

            // Extraer args como strings simples
            val args = functionCall.args.mapValues { (_, v) -> v.toString() }

            val functionResult = onFunctionCall(functionCall.name, args)

            // Enviar el resultado de vuelta a Gemini para que genere la respuesta final
            val functionResponseContent = content(role = "function") {
                part(FunctionResponsePart(
                    name = functionCall.name,
                    response = JSONObject(mapOf("result" to functionResult))
                ))
            }
            response = chat.sendMessage(functionResponseContent)
        }

        Result.success(response.text ?: "")
    } catch (e: IOException) {
        Result.failure(Exception("Sin conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Error desconocido"))
    }

    suspend fun explainFlashcard(front: String, back: String): Result<String> = try {
        val prompt = """
            El estudiante acaba de ver esta tarjeta de estudio y necesita una explicación del concepto.

            Pregunta: $front
            Respuesta: $back

            Explica el concepto en 3-4 oraciones simples. Usa un ejemplo concreto si ayuda. No repitas la pregunta ni la respuesta textualmente.
        """.trimIndent()
        val response = GeminiClient.model.generateContent(prompt)
        Result.success(response.text ?: "")
    } catch (e: IOException) {
        Result.failure(Exception("Sin conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Error desconocido"))
    }

    suspend fun generateFlashcards(courseName: String, topic: String, count: Int): Result<List<GeneratedFlashcard>> = try {
        val prompt = """
            Genera exactamente $count tarjetas de estudio para el curso "$courseName".
            Tema o contenido: "$topic"

            Las preguntas deben ser concisas. Las respuestas deben ser claras, máximo 2 oraciones.
        """.trimIndent()

        val response = GeminiClient.flashcardModel.generateContent(prompt)
        val rawText = response.text?.trim() ?: return Result.failure(Exception("Respuesta vacía"))

        // Limpieza de Markdown si Gemini incluye bloques ```json ... ```
        val cleanedJson = rawText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        Log.d("AiRepository", "Raw Response: $rawText")
        Log.d("AiRepository", "Cleaned JSON: $cleanedJson")

        val jsonArray = JSONArray(cleanedJson)
        val cards = (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            GeneratedFlashcard(front = obj.getString("front"), back = obj.getString("back"))
        }
        Result.success(cards)
    } catch (e: IOException) {
        Result.failure(Exception("Sin conexión a internet"))
    } catch (e: Exception) {
        Log.e("AiRepository", "Error parsing flashcards JSON", e)
        Result.failure(Exception("Error al generar tarjetas: Formato inválido"))
    }
}
