package com.example.quizmaster.ui.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    
    object Quiz : NavRoutes("quiz/{quizId}") {
        fun createRoute(quizId: String) = "quiz/$quizId"
    }
    
    // Added quizId to the path so the Result screen knows what to save
    object Result : NavRoutes("result/{quizId}/{score}/{total}") {
        fun createRoute(quizId: String, score: Int, total: Int) = "result/$quizId/$score/$total"
    }
}