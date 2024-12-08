package com.dicoding.picodiploma.loginwithanimation.data.retrofit

data class DiseasePredictionResponse(
    val predicted_label: String,
    val accuracy: Double,
    val article: Article,
    val image_url: String,
    val raw_output: List<List<Double>>
)

data class Article(
    val content: String,
    val link: String,
    val medication_link: String,
    val title: String
)