package com.example.practice.feature_feed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.practice.feature_feed.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FeedViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        loadPosts()
    }

    fun validatePostContent(content: String): String? {
        val trimmed = content.trim()
        return when {
            trimmed.isEmpty() -> "Post cannot be empty."
            trimmed.length < 4 -> "Post must be longer than 3 characters."
            trimmed.length > 200 -> "Post must be fewer than 200 characters."
            else -> null
        }
    }

    fun addPost(content: String) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "Anonymous"
                        val localAvatarFileName = document.getString("localAvatarFileName") ?: "default_avatar"

                        val newPost = Post(
                            username = username,
                            localAvatarFileName = localAvatarFileName,
                            content = content
                        )

                        db.collection("posts")
                            .document(newPost.id)
                            .set(newPost)
                            .addOnSuccessListener {
                                Log.d("FeedViewModel", "Post added")
                            }
                            .addOnFailureListener { e ->
                                Log.e("FeedViewModel", "Error adding post: ${e.message}")
                            }
                    } else {
                        Log.e("FeedViewModel", "User document not found")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FeedViewModel", "Failed to fetch user profile: ${e.message}")
                }
        } else {
            Log.e("FeedViewModel", "User not logged in")
        }
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FeedViewModel", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val postsList = snapshot.documents.mapNotNull { document ->
                        val post = document.toObject(Post::class.java)
                        val localAvatarFileName = post?.localAvatarFileName
                        if (localAvatarFileName != null) {
                            post.copy(localAvatarFileName = localAvatarFileName)
                        } else {
                            null
                        }
                    }
                    _posts.value = postsList
                }
            }
    }
}
