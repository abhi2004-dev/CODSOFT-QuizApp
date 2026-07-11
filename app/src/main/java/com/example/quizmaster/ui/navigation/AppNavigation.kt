package com.example.quizmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizmaster.data.local.ScoreManager
import com.example.quizmaster.ui.feature.home.HomeScreen
import com.example.quizmaster.ui.feature.quiz.QuizScreen
import com.example.quizmaster.ui.feature.result.ResultScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Initialize DataStore using the current Android Context
    val context = LocalContext.current
    val scoreManager = remember { ScoreManager(context) }

    NavHost(navController = navController, startDestination = NavRoutes.Home.route) {
        
        composable(NavRoutes.Home.route) {
            HomeScreen(
                scoreManager = scoreManager,
                onQuizSelected = { quizId -> navController.navigate(NavRoutes.Quiz.createRoute(quizId)) }
            )
        }

        composable(
            route = NavRoutes.Quiz.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            
            QuizScreen(
                quizId = quizId,
                onQuizComplete = { finalScore, totalQuestions ->
                    // Pass the quizId to the result screen route
                    navController.navigate(NavRoutes.Result.createRoute(quizId, finalScore, totalQuestions)) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.Result.route,
            arguments = listOf(
                navArgument("quizId") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            
            ResultScreen(
                quizId = quizId,
                score = score,
                total = total,
                scoreManager = scoreManager, // Pass it down to save the score
                onNavigateHome = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}