package com.example.practice.feature_feed.data

import java.util.UUID


data class Post(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val localAvatarFileName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor (required for Firestore deserialization)
    constructor() : this("", "", "", "", System.currentTimeMillis()) // Updated constructor to match the parameters
}