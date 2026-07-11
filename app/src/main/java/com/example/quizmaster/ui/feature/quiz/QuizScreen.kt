package com.example.quizmaster.ui.feature.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizmaster.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizId: String,
    onQuizComplete: (score: Int, total: Int) -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuizMaster", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is QuizUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is QuizUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is QuizUiState.Active -> {
                    if (state.isFinished) {
                        LaunchedEffect(Unit) {
                            onQuizComplete(state.correctAnswersCount, state.quiz.questions.size)
                        }
                    } else {
                        QuizContent(
                            state = state,
                            onOptionSelected = { viewModel.selectOption(it) },
                            onSubmit = { viewModel.submitAnswer() },
                            onNext = { viewModel.nextQuestion() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizContent(
    state: QuizUiState.Active,
    onOptionSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        // --- FIXED HEADER ---
        // This stays locked to the top of the screen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.currentQuestionIndex + 1} of ${state.quiz.questions.size}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            AnimatedTimer(timeRemaining = state.timeRemaining)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val animatedProgress by animateFloatAsState(
            targetValue = (state.currentQuestionIndex + 1).toFloat() / state.quiz.questions.size,
            label = "progress"
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // --- SCROLLABLE BODY ---
        // We use weight(1f) to take up all remaining space, and add verticalScroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(
                targetState = state.currentQuestionIndex,
                transitionSpec = {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() with 
                    slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                },
                label = "question_transition"
            ) { targetIndex ->
                Column {
                    Text(
                        text = state.quiz.questions[targetIndex].text,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * 1.2f
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    state.quiz.questions[targetIndex].options.forEach { option ->
                        OptionCard(
                            option = option,
                            isSelected = option == state.selectedOption,
                            isCorrect = option == state.quiz.questions[targetIndex].correctAnswer,
                            isSubmitted = state.isSubmitted,
                            onClick = { onOptionSelected(option) }
                        )
                    }
                }
            }
        }

        // --- FIXED FOOTER ---
        // The button stays locked to the bottom, never scrolling out of view
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = if (state.isSubmitted) onNext else onSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = state.isSubmitted || state.selectedOption != null,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (state.isSubmitted) {
                    if (state.currentQuestionIndex == state.quiz.questions.size - 1) "View Results" else "Next Question"
                } else "Submit Answer",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun AnimatedTimer(timeRemaining: Int) {
    val timerColor = when {
        timeRemaining > Constants.TIMER_WARNING_THRESHOLD -> Color(0xFF4CAF50)
        timeRemaining > Constants.TIMER_CRITICAL_THRESHOLD -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = timeRemaining.toFloat() / Constants.TIMER_DURATION_SECONDS,
        label = "timer_progress"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
        CircularProgressIndicator(
            progress = 1f,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxSize()
        )
        CircularProgressIndicator(
            progress = animatedProgress,
            color = timerColor,
            modifier = Modifier.fillMaxSize(),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        Text(
            text = timeRemaining.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
    }
}

@Composable
fun OptionCard(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isSubmitted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSubmitted) {
        when {
            isCorrect -> Color(0xFFE8F5E9)
            isSelected && !isCorrect -> Color(0xFFFFEBEE)
            else -> MaterialTheme.colorScheme.surface
        }
    } else {
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSubmitted) {
        when {
            isCorrect -> Color(0xFF4CAF50)
            isSelected && !isCorrect -> Color(0xFFF44336)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    } else {
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSubmitted) {
        when {
            isCorrect -> Color(0xFF2E7D32)
            isSelected && !isCorrect -> Color(0xFFC62828)
            else -> MaterialTheme.colorScheme.onSurface
        }
    } else {
        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                enabled = !isSubmitted
            ),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(if (isSelected || isSubmitted) 2.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected || (isSubmitted && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}