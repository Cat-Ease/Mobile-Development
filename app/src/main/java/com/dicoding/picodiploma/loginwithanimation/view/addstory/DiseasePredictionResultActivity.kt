package com.dicoding.picodiploma.loginwithanimation.view.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R

class DiseasePredictionResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_prediction_result)

        val predictedLabel = intent.getStringExtra("predictedLabel") ?: "No result"
        val accuracy = intent.getDoubleExtra("accuracy", 0.0)
        val articleContent = intent.getStringExtra("articleContent") ?: "No article content"
        val articleLink = intent.getStringExtra("articleLink") ?: ""
        val medicationLink = intent.getStringExtra("medicationLink") ?: ""
        val articleTitle = intent.getStringExtra("articleTitle") ?: "No title"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        findViewById<TextView>(R.id.textPredictionResult).text = "Predicted: $predictedLabel\nAccuracy: ${accuracy * 100}%"
        findViewById<TextView>(R.id.textArticleContent).text = articleContent

        // Menampilkan judul artikel
        findViewById<TextView>(R.id.textArticleTitle).text = articleTitle

        // Menampilkan tombol untuk membaca lebih lanjut
        val readMoreButton = findViewById<Button>(R.id.buttonReadMore)
        readMoreButton.setOnClickListener {
            if (articleLink.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleLink))
                startActivity(intent)
            }
        }

        // Menampilkan tombol untuk pengobatan
        val medicationButton = findViewById<Button>(R.id.buttonMedication)
        medicationButton.setOnClickListener {
            if (medicationLink.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(medicationLink))
                startActivity(intent)
            }
        }
    }
}