package com.example.quizmaster.data.local

import com.example.quizmaster.data.model.Difficulty
import com.example.quizmaster.data.model.Question
import com.example.quizmaster.data.model.Quiz

object SampleQuizzes {
    val getQuizzes = listOf(
        Quiz(
            id = "q1",
            title = "Android Fundamentals",
            description = "Test your knowledge of Android development, Jetpack Compose, and architecture.",
            difficulty = Difficulty.MEDIUM,
            questions = listOf(
                Question(
                    id = "q1_1",
                    text = "What is the primary language used for modern Android development?",
                    options = listOf("Java", "Kotlin", "C++", "Python"),
                    correctAnswer = "Kotlin"
                ),
                Question(
                    id = "q1_2",
                    text = "Which component is strictly responsible for holding and managing UI-related data in a lifecycle conscious way?",
                    options = listOf("Activity", "Fragment", "ViewModel", "Repository"),
                    correctAnswer = "ViewModel"
                ),
                Question(
                    id = "q1_3",
                    text = "In Jetpack Compose, what annotation is used to define a UI component?",
                    options = listOf("@Composable", "@UiNode", "@Component", "@View"),
                    correctAnswer = "@Composable"
                ),
                Question(
                    id = "q1_4",
                    text = "Which Coroutine builder is used to launch a coroutine without blocking the current thread and returns a Job?",
                    options = listOf("runBlocking", "async", "launch", "withContext"),
                    correctAnswer = "launch"
                )
            )
        ),
        Quiz(
            id = "q2",
            title = "Machine Learning & Python",
            description = "Dive into data classification, decision trees, and model deployment.",
            difficulty = Difficulty.HARD,
            questions = listOf(
                Question(
                    id = "q2_1",
                    text = "Which scikit-learn class is typically used to create a decision tree classifier?",
                    options = listOf("TreePredictor", "DecisionTreeClassifier", "RandomForest", "ClassifierTree"),
                    correctAnswer = "DecisionTreeClassifier"
                ),
                Question(
                    id = "q2_2",
                    text = "The classic 'Iris' dataset is most commonly used to demonstrate which machine learning task?",
                    options = listOf("Image Generation", "Regression", "Classification", "Text Translation"),
                    correctAnswer = "Classification"
                ),
                Question(
                    id = "q2_3",
                    text = "What is the primary purpose of the Hugging Face Hub?",
                    options = listOf("Hosting web applications", "Version control for standard code", "Hosting and sharing ML models and datasets", "Compiling Python scripts"),
                    correctAnswer = "Hosting and sharing ML models and datasets"
                ),
                Question(
                    id = "q2_4",
                    text = "In a Vision-Encoder-Decoder architecture (often used for OCR), what is the role of the encoder?",
                    options = listOf("Generating text output", "Extracting visual features from the input image", "Calculating the loss function", "Storing the dataset in memory"),
                    correctAnswer = "Extracting visual features from the input image"
                )
            )
        ),
        Quiz(
            id = "q3",
            title = "Cinematic Image Editing",
            description = "Explore the techniques behind ethereal lighting and digital image enhancement.",
            difficulty = Difficulty.EASY,
            questions = listOf(
                Question(
                    id = "q3_1",
                    text = "Which visual effect creates a soft, glowing halo around bright light sources in an image?",
                    options = listOf("Vignette", "Lens Bloom", "Chromatic Aberration", "Sharpening"),
                    correctAnswer = "Lens Bloom"
                ),
                Question(
                    id = "q3_2",
                    text = "To create soft, diffused sunbeams (often called 'God Rays'), which lighting technique is utilized?",
                    options = listOf("Volumetric Lighting", "Global Illumination", "Ray Tracing", "Ambient Occlusion"),
                    correctAnswer = "Volumetric Lighting"
                ),
                Question(
                    id = "q3_3",
                    text = "When heavily color-grading a portrait to achieve an ethereal look, what is the most critical detail to preserve?",
                    options = listOf("Background noise", "Facial details and skin tones", "Lens distortion", "Deep black shadows"),
                    correctAnswer = "Facial details and skin tones"
                )
            )
        )
    )
}