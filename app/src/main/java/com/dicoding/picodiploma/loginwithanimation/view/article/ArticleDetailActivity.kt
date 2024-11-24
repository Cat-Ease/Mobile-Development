package com.dicoding.picodiploma.loginwithanimation.view.article

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dicoding.picodiploma.loginwithanimation.R
import com.squareup.picasso.Picasso
import android.view.View // Tambahkan import untuk View

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var articleImage: ImageView
    private lateinit var articleTitle: TextView
    private lateinit var articleDescription: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var backButton: View // Mengganti Button dengan View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        // Inisialisasi Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Menampilkan tombol kembali
        supportActionBar?.title = "" // Menghilangkan judul default

        articleImage = findViewById(R.id.detail_article_image)
        articleTitle = findViewById(R.id.detail_article_title)
        articleDescription = findViewById(R.id.detail_article_description)
        backButton = findViewById(R.id.back_button) // Inisialisasi RelativeLayout sebagai backButton

        // Ambil data dari Intent
        val articleId = intent.getStringExtra("ARTICLE_ID")
        val articleTitleText = intent.getStringExtra("ARTICLE_TITLE")
        val articleDescriptionText = intent.getStringExtra("ARTICLE_DESCRIPTION")
        val articleImageUrl = intent.getStringExtra("ARTICLE_IMAGE")

        // Set data ke tampilan
        articleTitle.text = articleTitleText
        articleDescription.text = articleDescriptionText
        Picasso.get().load(articleImageUrl).into(articleImage) // Menggunakan Picasso untuk memuat gambar

        // Set listener untuk tombol kembali
        backButton.setOnClickListener {
            onBackPressed() // Menangani klik tombol kembali
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Menangani tombol kembali di toolbar
        return true
    }
}