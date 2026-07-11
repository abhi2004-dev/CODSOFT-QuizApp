package com.example.quizmaster.ui.feature.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizmaster.data.local.ScoreManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    quizId: String,
    score: Int,
    total: Int,
    scoreManager: ScoreManager,
    onNavigateHome: () -> Unit
) {
    val targetPercentage = if (total > 0) ((score.toFloat() / total.toFloat()) * 100).toInt() else 0
    
    var animationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationPlayed = true
        // Save the high score to DataStore as soon as the screen loads
        scoreManager.saveScoreIfHigher(quizId, targetPercentage)
    }

    val animatedPercentage by animateIntAsState(
        targetValue = if (animationPlayed) targetPercentage else 0,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "percentage"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) targetPercentage / 100f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val (feedbackText, themeColor) = when {
        targetPercentage >= 80 -> "Outstanding!" to Color(0xFF4CAF50) 
        targetPercentage >= 50 -> "Good Job!" to Color(0xFFFF9800) 
        else -> "Keep Practicing!" to Color(0xFFF44336) 
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quiz Complete", fontWeight = FontWeight.Bold) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = feedbackText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = themeColor
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                CircularProgressIndicator(
                    progress = 1f, color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 16.dp, modifier = Modifier.fillMaxSize()
                )
                CircularProgressIndicator(
                    progress = animatedProgress, color = themeColor,
                    strokeWidth = 16.dp, strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$animatedPercentage%", style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
                    )
                    Text("Score", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), label = "Correct", value = score.toString(), color = Color(0xFF4CAF50))
                StatCard(modifier = Modifier.weight(1f), label = "Missed", value = (total - score).toString(), color = Color(0xFFF44336))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onNavigateHome,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Return to Home", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(color.copy(alpha = 0.1f)).padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.titleSmall, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
        }
    }
}