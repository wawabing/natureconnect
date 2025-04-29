package com.example.practice.feature_garden.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.feature_garden.data.Plant
import com.example.practice.feature_naturebot.data.ChatMessage
import com.example.practice.feature_naturebot.data.OpenAIRequest
import com.example.practice.feature_naturebot.data.OpenAIResponse
import com.example.practice.feature_naturebot.data.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.practice.feature_garden.data.PlantInfo




class GardenViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _mySharedPlants = MutableStateFlow<List<Plant>>(emptyList())
    val mySharedPlants: StateFlow<List<Plant>> = _mySharedPlants.asStateFlow()

    // Use a snapshot listener so changes update automatically.
    fun getPlants() {
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email ?: return
            db.collection("plants")
                .whereEqualTo("ownerEmail", userEmail)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("SharedPlants", "Error fetching shared plants: ${error.message}")
                        return@addSnapshotListener
                    }
                    val plants = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Plant::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    _mySharedPlants.value = plants.sortedBy { it.slot }
                    Log.d("SharedPlants", "Fetched ${plants.size} plants for $userEmail")
                }
        } else {
            Log.e("SharedPlants", "User not logged in")
        }
    }

    fun deletePlant(plantId: String) {
        val db = FirebaseFirestore.getInstance()
        Log.d("DELETE_PLANT", "Deleting plant with ID: $plantId")
        // Reference to the specific plant document
        val plantRef = db.collection("plants").document(plantId)
        // Delete the document
        plantRef.delete()
            .addOnSuccessListener {
                Log.d("GardenViewModel", "Plant deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("GardenViewModel", "Error deleting plant", e)
            }
    }


    fun addPlant(name: String, type: String, imageName: String, slot: Int) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            // Get plant info first
            getPlantAiInfo(name)

            // Wait for the AI data to be fetched (you can use collect to observe the flow)
            viewModelScope.launch {
                _plantInfo.collect { plantInfo ->
                    if (plantInfo != null) {
                        // Now you have the AI data, and you can add the plant to Firestore

                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val username = document.getString("username") ?: "Anonymous"
                                    val email = document.getString("email") ?: "Anonymous"
                                    val plantData = hashMapOf(
                                        "name" to name,
                                        "imageName" to imageName,
                                        "ownerId" to uid,
                                        "slot" to slot,  // slot is provided  (1-based)
                                        "ownerUsername" to username,
                                        "ownerEmail" to email,
                                        "timestamp" to System.currentTimeMillis(),
                                        // Add the AI data here
                                        "wateringFrequency" to plantInfo.watering_frequency,
                                        "sunlightHours" to plantInfo.sunlight_hours,
                                        "soilType" to plantInfo.soil_type,
                                        "temperatureRange" to plantInfo.temperature_range,
                                        "commonPests" to plantInfo.common_pests,
                                        "careTip" to plantInfo.care_tip
                                    )

                                    db.collection("plants")
                                        .add(plantData)
                                        .addOnSuccessListener {
                                            Log.d("PlantUpload", "Plant added to slot $slot")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("PlantUpload", "Error adding plant: ${e.message}")
                                        }
                                } else {
                                    Log.e("PlantUpload", "User document not found")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("PlantUpload", "Failed to fetch user profile: ${e.message}")
                            }
                    }
                }
            }
        } else {
            Log.e("PlantUpload", "User not logged in")
        }
    }


    // Add a MutableStateFlow to hold the plant info
    private val _plantInfo = MutableStateFlow<PlantInfo?>(null)

    // Function to fetch plant info based on the plant name
    private fun getPlantAiInfo(plantname: String) {
        viewModelScope.launch {
            try {
                val request = OpenAIRequest(
                    model = "gpt-4.1-2025-04-14",  // Example: using model gpt-4.1
                    messages = listOf(
                        ChatMessage(role = "system", content = "You are a plant expert and will be given the name of a plant, find and respond with the requested information"),
                        ChatMessage(role = "user", content = "<RULE>[[NO PROSE]] [[JSON ONLY]] TAKE THE FOLLOWING PLANT NAME AND RETURN A JSON OBJECT WITH THE FOLLOWING ATTRIBUTES: watering_frequency, sunlight_hours, soil_type, temperature_range, common_pests, care_tip</RULE><PLANTNAME>$plantname</PLANTNAME>")
                    ),
                    max_completion_tokens = 500,
                )

                val response: Response<OpenAIResponse> = RetrofitClient.openAIService.getResponse(request)

                if (response.isSuccessful) {
                    val info = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                    Log.e(TAG, "PLANT INFO AI Response: $info")

                    // Use Gson to parse the JSON response
                    val gson = Gson()
                    val plantInfo = gson.fromJson(info, PlantInfo::class.java)

                    // Now you can access the individual variables
                    _plantInfo.value = plantInfo // Store the plant info in the MutableStateFlow
                } else {
                    Log.e(TAG, "Error fetching response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching response", e)
            }
        }
    }
}