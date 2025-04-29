package com.example.practice.feature_garden.data

data class Plant(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val ownerId: String = "",
    val ownerEmail: String = "",
    val ownerUsername: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageName: String = "",
    val slot: Int = 0,
    val addedAt: Long = System.currentTimeMillis(),
    val wateringFrequency: String = "",
    val sunlightHours: String = "",
    val soilType: String = "",
    val temperatureRange: String = "",
    val commonPests: List<String> = emptyList(),
    val careTip: String = ""
)
{
    constructor() : this("", "", "", "", "", "", 0L, "", 0, 0L)
}

data class PlantInfo(
    val watering_frequency: String = "",
    val sunlight_hours: String = "",
    val soil_type: String = "",
    val temperature_range: String = "",
    val common_pests: List<String> = emptyList(), // ← FIXED
    val care_tip: String = ""
)


