package com.example.quizmaster.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizmaster.data.model.Quiz
import com.example.quizmaster.data.repository.QuizRepository
import com.example.quizmaster.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val quizzes: List<Quiz>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: QuizRepository = QuizRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadQuizzes()
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val quizzes = repository.getAllQuizzes()
                _uiState.value = HomeUiState.Success(quizzes)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to load quizzes.")
            }
        }
    }
}