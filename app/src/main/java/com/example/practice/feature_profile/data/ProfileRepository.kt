package com.example.practice.feature_profile.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


data class UserProfile(
    val username: String = "",
    val email: String = "",
    val localAvatarFileName: String? = null,
    val accessibilityMode: Boolean = false,
    val languageCode: String = "en" // default
)

class ProfileRepository(private val db: FirebaseFirestore) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun getUserProfile(): UserProfile? {
        val userId = auth.currentUser?.uid ?: return null // Get logged-in user ID

        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            snapshot.toObject(UserProfile::class.java) // Convert Firestore data to object
        } catch (e: Exception) {
            null // Handle errors gracefully
        }
    }
}
