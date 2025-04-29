package com.example.practice.feature_naturebot.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.feature_naturebot.data.ChatMessage
import com.example.practice.feature_naturebot.data.OpenAIRequest
import com.example.practice.feature_naturebot.data.OpenAIResponse
import com.example.practice.feature_naturebot.data.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response
import androidx.compose.runtime.State


class NatureBotViewModel : ViewModel() {

    var factMessage = mutableStateOf("")  // State for fun fact message

    private val _errorMessage = mutableStateOf<String>("")
    val errorMessage: State<String> get() = _errorMessage

    // Function to validate input
    fun validateInput(userQuestion: String, isOnline: Boolean): Boolean {
        return when {
            userQuestion.trim().isEmpty() -> {
                _errorMessage.value = "Please ask a question" // Validation error message
                false
            }
            userQuestion.length < 3 -> {
                _errorMessage.value = "Your question is too short. Please ask something more specific."
                false
            }
            userQuestion.length > 200 -> {
                _errorMessage.value = "Your question is too long. Please limit it to 200 characters."
                false
            }
            !isOnline -> {
                _errorMessage.value = "You are offline. Please check your internet connection."
                false
            }
            else -> {
                _errorMessage.value = "" // No error
                true
            }
        }
            }

    // Function to fetch fun fact based on a dynamic user question
    fun getAiResponse(userQuestion: String) {
        viewModelScope.launch {
            try {
                val request = OpenAIRequest(
                    model = "gpt-4.1-2025-04-14",  // Example: using model gpt-4.1
                    messages = listOf(
                        ChatMessage(role = "system", content = "You are a friendly nature expert acting as an AI bot inside a nature mobile app, be friendly and energetic please"),
                        ChatMessage(role = "user", content = "<RULE>ENSURE THE FOLLOWING QUESTION IS ABOUT NATURE, IF NOT THEN TELL THEM YOU CANNOT ASSIST IF NOT NATURE RELATED</RULE><RULE>IF THE USER ASKS ABOUT HOW TO USE THE APP THEN DESCRIBE THE FOLLOWING:this is a plant care social media where users can store their real life plants and meet new friends in the global nature feed, users can also interact with an AI chatbot which disscusses nature related topics, i am that robot helloo!! finally users can customise their profile and accessibilty options</RULE><USERQUESTION>$userQuestion</USERQUESTION>")
                    ),
                    max_completion_tokens = 500,
                )

                val response: Response<OpenAIResponse> = RetrofitClient.openAIService.getResponse(request)

                if (response.isSuccessful) {
                    val fact = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                    if (!fact.isNullOrEmpty()) {
                        factMessage.value = fact
                    } else {
                        factMessage.value = "Sorry, we couldn't fetch a response right now."
                        Log.e("FunFactViewModel", "Response body was empty.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (response.code() == 401)
                    {
                        factMessage.value = "Hello user, you need an API key in order to talk to me!"
                    } else {
                        factMessage.value = "Oops, something went wrong! Please try again later"
                    }
                    Log.e("FunFactViewModel", "Error fetching response: $errorBody")
                }
            } catch (e: Exception) {
                factMessage.value = "Failed to fetch response: ${e.message}"
                Log.e("FunFactViewModel", "Error fetching response", e)
            }
        }
    }
}
