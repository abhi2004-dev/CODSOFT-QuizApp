package com.example.quizmaster.ui.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.repository.QuizRepository
import com.example.quizmaster.data.repository.QuizRepositoryImpl
import com.example.quizmaster.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository = QuizRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val originalQuiz = repository.getQuizById(quizId)
            
            if (originalQuiz != null && originalQuiz.questions.isNotEmpty()) {
                // PHASE 6: Shuffle Logic
                // 1. Shuffle the order of the questions
                // 2. For every question, shuffle the order of the multiple-choice options
                val shuffledQuestions = originalQuiz.questions.shuffled().map { question ->
                    question.copy(options = question.options.shuffled())
                }
                
                // Create a new version of the quiz with the randomized data
                val randomizedQuiz = originalQuiz.copy(questions = shuffledQuestions)
                
                _uiState.value = QuizUiState.Active(
                    quiz = randomizedQuiz,
                    currentQuestion = randomizedQuiz.questions[0]
                )
                startTimer()
            } else {
                _uiState.value = QuizUiState.Error("Quiz not found.")
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel() 
        
        timerJob = viewModelScope.launch {
            for (i in Constants.TIMER_DURATION_SECONDS downTo 0) {
                val currentState = _uiState.value as? QuizUiState.Active ?: break
                
                if (currentState.isSubmitted) break 
                
                _uiState.value = currentState.copy(timeRemaining = i)
                
                if (i == 0) {
                    handleTimeout()
                }
                delay(1000L) 
            }
        }
    }

    private fun handleTimeout() {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Active && !currentState.isSubmitted) {
            _uiState.value = currentState.copy(
                isSubmitted = true,
                isTimeUp = true,
                selectedOption = null 
            )
        }
    }

    fun selectOption(option: String) {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Active && !currentState.isSubmitted) {
            _uiState.value = currentState.copy(selectedOption = option)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Active && currentState.selectedOption != null && !currentState.isSubmitted) {
            timerJob?.cancel() 
            val isCorrect = currentState.selectedOption == currentState.currentQuestion.correctAnswer
            _uiState.value = currentState.copy(
                isSubmitted = true,
                correctAnswersCount = if (isCorrect) currentState.correctAnswersCount + 1 else currentState.correctAnswersCount
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Active) {
            val nextIndex = currentState.currentQuestionIndex + 1
            
            if (nextIndex < currentState.quiz.questions.size) {
                _uiState.value = currentState.copy(
                    currentQuestionIndex = nextIndex,
                    currentQuestion = currentState.quiz.questions[nextIndex],
                    selectedOption = null,
                    isSubmitted = false,
                    isTimeUp = false,
                    timeRemaining = Constants.TIMER_DURATION_SECONDS
                )
                startTimer() 
            } else {
                _uiState.value = currentState.copy(isFinished = true)
            }
        }
    }
}