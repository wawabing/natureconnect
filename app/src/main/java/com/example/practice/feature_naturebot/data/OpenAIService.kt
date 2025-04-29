package com.example.practice.feature_naturebot.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Request body format for OpenAI
data class OpenAIRequest(
    val model: String,
    val messages: List<ChatMessage> = listOf(),
    val max_completion_tokens: Int
    )

data class ChatMessage(
    val role: String,
    val content: String
)

// Response format for OpenAI
data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    val finish_reason: String
)

data class Message(
    val role: String,
    val content: String?,
    val refusal: Any?,
    val annotations: List<Any>
)

interface OpenAIService {
    @POST("chat/completions")
    suspend fun getResponse(@Body request: OpenAIRequest): Response<OpenAIResponse>
}
