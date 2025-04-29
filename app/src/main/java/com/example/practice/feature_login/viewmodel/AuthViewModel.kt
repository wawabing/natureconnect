package com.example.practice.feature_login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(email: String, password: String, username: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    // Store user info in Firestore
                    val userData = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "accessibilityMode" to false,
                        "uid" to user.uid
                    )

                    db.collection("users").document(user.uid).set(userData)
                        .addOnSuccessListener {
                            Log.d("FirebaseRegister", "User Registered and Data Stored in Firestore")
                            onSuccess()  // Now always calling onSuccess
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseRegister", "Error storing user data: ${e.message}")
                            onSuccess() // Even if Firestore fails, stop loading and move on
                        }
                } else {
                    onFailure("User creation failed")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRegister", "Registration Failed: ${e.message}")
                onFailure(e.message ?: "Registration failed")
            }
    }
}
