package com.example.quizmaster.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creates a single instance of DataStore attached to the application context
val Context.dataStore by preferencesDataStore(name = "quiz_scores")

class ScoreManager(private val context: Context) {
    
    // Reads the high score as a continuous stream of data (Flow)
    fun getHighScore(quizId: String): Flow<Int> {
        val key = intPreferencesKey(quizId)
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: 0 // Default to 0 if no score exists yet
        }
    }

    // Only overwrites the saved score if the new one is higher
    suspend fun saveScoreIfHigher(quizId: String, score: Int) {
        val key = intPreferencesKey(quizId)
        context.dataStore.edit { preferences ->
            val currentScore = preferences[key] ?: 0
            if (score > currentScore) {
                preferences[key] = score
            }
        }
    }
}