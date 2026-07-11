package com.example.quizmaster.data.model

data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val questions: List<Question>
)