package com.example.practice.feature_splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// SplashScreenViewModel.kt
class SplashScreenViewModel : ViewModel() {
    private val _languageCode = MutableStateFlow("")
    val languageCode: StateFlow<String> = _languageCode

    fun fetchLanguageCode(userId: String) {
        viewModelScope.launch {
            val code = try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()
                    .getString("languageCode")
                    .orEmpty()
            } catch(e: Exception) {
                "en"
            }
            _languageCode.value = code.ifBlank { "en" }
        }
    }
}
