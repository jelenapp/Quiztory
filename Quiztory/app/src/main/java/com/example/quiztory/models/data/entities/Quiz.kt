package com.example.quiztory.models.data.entities

data class Quiz(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)
