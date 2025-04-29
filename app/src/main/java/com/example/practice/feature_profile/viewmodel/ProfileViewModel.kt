package com.example.practice.feature_profile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.practice.feature_profile.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    // Change the type from Int to String to store the file name
    private val _localAvatarFileName = MutableStateFlow<String?>(null)
    val localAvatarFileName = _localAvatarFileName.asStateFlow()

    // Holds the user's accessibility preference
    private val _accessibilityEnabled = MutableStateFlow(false)
    val accessibilityEnabled: StateFlow<Boolean> = _accessibilityEnabled.asStateFlow()

    // Holds the user language for display
    private val _languageCode = MutableStateFlow("en")
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    init {
        getUserProfile()
    }

    fun setLanguage(code: String) {
        val userId = auth.currentUser?.uid ?: return
        _languageCode.value = code
        db.collection("users").document(userId)
            .update("languageCode", code)
            .addOnSuccessListener { Log.d("ProfileVM", "Language updated: $code") }
            .addOnFailureListener { Log.e("ProfileVM", "Failed to update language", it) }
    }

    // Save the file name, not the resource ID
    fun setProfilePic(fileName: String) {
        val userId = auth.currentUser?.uid ?: return

        _localAvatarFileName.value = fileName

        db.collection("users").document(userId)
            .update("localAvatarFileName", fileName)
            .addOnSuccessListener { Log.d("Profile", "Avatar saved") }
            .addOnFailureListener { Log.e("Profile", "Failed to save avatar", it) }
    }

    /** Toggle and persist the accessibility setting */
    fun setAccessibility(enabled: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        _accessibilityEnabled.value = enabled
        db.collection("users").document(userId)
            .update("accessibilityMode", enabled)
            .addOnSuccessListener { Log.d("ProfileVM", "Accessibility mode saved: $enabled") }
            .addOnFailureListener { Log.e("ProfileVM", "Failed to save accessibility mode", it) }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    // Fetch User Profile Data from Firestore
    private fun getUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val profile = snapshot.toObject(UserProfile::class.java)
                _userProfile.value = profile
                _localAvatarFileName.value = profile?.localAvatarFileName // Set avatar filename from Firestore

                // 2. Read the accessibilityMode field (default false)
                val enabled = snapshot.getBoolean("accessibilityMode") ?: false
                _accessibilityEnabled.value = enabled

                val lang = snapshot.getString("languageCode") ?: "en"
                _languageCode.value = lang

            }
    }
}
