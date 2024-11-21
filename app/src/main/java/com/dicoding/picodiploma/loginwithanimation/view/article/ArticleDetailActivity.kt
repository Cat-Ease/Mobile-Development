package com.dicoding.picodiploma.loginwithanimation.view.article

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.squareup.picasso.Picasso

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var articleImage: ImageView
    private lateinit var articleTitle: TextView
    private lateinit var articleDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        articleImage = findViewById(R.id.detail_article_image)
        articleTitle = findViewById(R.id.detail_article_title)
        articleDescription = findViewById(R.id.detail_article_description)

        // Ambil data dari Intent
        val articleId = intent.getStringExtra("ARTICLE_ID")
        val articleTitleText = intent.getStringExtra("ARTICLE_TITLE")
        val articleDescriptionText = intent.getStringExtra("ARTICLE_DESCRIPTION")
        val articleImageUrl = intent.getStringExtra("ARTICLE_IMAGE")

        // Set data ke tampilan
        articleTitle.text = articleTitleText
        articleDescription.text = articleDescriptionText
        Picasso.get().load(articleImageUrl).into(articleImage) // Menggunakan Picasso untuk memuat gambar
    }
}