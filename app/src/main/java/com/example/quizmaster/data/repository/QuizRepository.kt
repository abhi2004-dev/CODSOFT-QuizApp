package com.example.quizmaster.data.repository

import com.example.quizmaster.data.model.Quiz
import com.example.quizmaster.data.local.SampleQuizzes
import kotlinx.coroutines.delay

interface QuizRepository {
    suspend fun getAllQuizzes(): List<Quiz>
    suspend fun getQuizById(id: String): Quiz?
}

class QuizRepositoryImpl : QuizRepository {
    override suspend fun getAllQuizzes(): List<Quiz> {
        delay(500) 
        return SampleQuizzes.getQuizzes
    }

    override suspend fun getQuizById(id: String): Quiz? {
        return SampleQuizzes.getQuizzes.find { it.id == id }
    }
}