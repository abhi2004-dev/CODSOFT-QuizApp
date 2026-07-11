package com.example.quizmaster.data.model

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)