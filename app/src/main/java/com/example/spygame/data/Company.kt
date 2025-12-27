package com.example.spygame.data

data class Company(
    val name: String,
    val difficulty: Difficulty,
    val players: List<String>,
    val customLocations: List<String> = emptyList(),
    val usedLocations: Set<String> = emptySet()
)

