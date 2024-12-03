package com.dicoding.picodiploma.loginwithanimation.view.addstory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.dicoding.picodiploma.loginwithanimation.R

class DiseasePredictionResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_prediction_result)

        val resultText = intent.getStringExtra("predictionResult") ?: "No result"
        findViewById<TextView>(R.id.textPredictionResult).text = resultText
    }
}