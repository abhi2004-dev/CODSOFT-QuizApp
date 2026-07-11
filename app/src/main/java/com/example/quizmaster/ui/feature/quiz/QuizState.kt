package com.example.quizmaster.ui.feature.quiz

import com.example.quizmaster.data.model.Question
import com.example.quizmaster.data.model.Quiz
import com.example.quizmaster.util.Constants

sealed interface QuizUiState {
    object Loading : QuizUiState
    data class Error(val message: String) : QuizUiState
    
    data class Active(
        val quiz: Quiz,
        val currentQuestionIndex: Int = 0,
        val currentQuestion: Question,
        val selectedOption: String? = null,
        val isSubmitted: Boolean = false,
        val correctAnswersCount: Int = 0,
        val isFinished: Boolean = false,
        val timeRemaining: Int = Constants.TIMER_DURATION_SECONDS,
        val isTimeUp: Boolean = false
    ) : QuizUiState
}