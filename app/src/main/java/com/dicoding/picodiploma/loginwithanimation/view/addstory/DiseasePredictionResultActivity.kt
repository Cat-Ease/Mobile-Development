package com.dicoding.picodiploma.loginwithanimation.view.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class DiseasePredictionResultActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_prediction_result)

        imageView = findViewById(R.id.imageView)

        val predictedLabel = intent.getStringExtra("predictedLabel") ?: getString(R.string.no_result)
        val articleContent = intent.getStringExtra("articleContent") ?: getString(R.string.no_article_content)
        val articleLink = intent.getStringExtra("articleLink") ?: ""
        val medicationLink = intent.getStringExtra("medicationLink") ?: ""
        val articleTitle = intent.getStringExtra("articleTitle") ?: getString(R.string.no_title)
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val imagePath = intent.getStringExtra("imageUrl") // Ambil path gambar

        // Menggunakan string resource dengan placeholder
        findViewById<TextView>(R.id.textPredictionResult).text = getString(R.string.predicted_label, predictedLabel)
        findViewById<TextView>(R.id.textArticleContent).text = articleContent
        findViewById<TextView>(R.id.textArticleTitle).text = articleTitle

        // Menampilkan gambar menggunakan Glide
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        } else if (imagePath != null) {
            val uri = Uri.parse(imagePath)
            imageView.setImageURI(uri)
        }

        // Menambahkan tombol untuk menyimpan hasil
        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            savePredictionResult(predictedLabel, articleContent, articleTitle, imagePath)
        }

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

    private fun savePredictionResult(predictedLabel: String, articleContent: String, articleTitle: String?, imagePath: String?) {
        val fileName = "prediction_result.txt"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            val outputStream = FileOutputStream(file)
            val writer = OutputStreamWriter(outputStream)
            writer.write("Predicted Label: $predictedLabel\n")
            writer.write("Article Title: $articleTitle\n")
            writer.write("Article Content: $articleContent\n")
            writer.write("Image Path: $imagePath\n")
            writer.close()
            outputStream.close()
            Toast.makeText(this, "Prediction result saved to $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save prediction result", Toast.LENGTH_SHORT).show()
        }
    }
}