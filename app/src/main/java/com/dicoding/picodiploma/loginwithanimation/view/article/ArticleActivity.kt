package com.dicoding.picodiploma.loginwithanimation.view.article

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.save.SaveActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class ArticleActivity : AppCompatActivity() {

    private lateinit var breakingNewsTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter // Pastikan Anda membuat adapter ini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        // Inisialisasi Bottom Navigation
        setupBottomNavigation()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_article

        // Inisialisasi Views
        breakingNewsTitle = findViewById(R.id.breaking_news_title)
        recyclerView = findViewById(R.id.recycler_view_articles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mengatur konten judul berita
        breakingNewsTitle.text = "Artikel"

        // Ambil data dari API
        fetchArticles()
    }

    private fun fetchArticles() {
        val url = "https://article-api-684282003208.us-central1.run.app/articles"
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                // Proses data JSON dan update RecyclerView
                val articles = response.getJSONObject("data").getJSONArray("articles")
                val articleList = mutableListOf<Article>() // Pastikan Anda memiliki model Article
                for (i in 0 until articles.length()) {
                    val articleJson = articles.getJSONObject(i)
                    val article = Article(
                        articleJson.getString("id"),
                        articleJson.getString("title"),
                        articleJson.getString("description"),
                        articleJson.getString("image")
                    )
                    articleList.add(article)
                }
                articleAdapter = ArticleAdapter(articleList) // Pastikan Anda membuat adapter ini
                recyclerView.adapter = articleAdapter
            },
            { error ->
                // Tangani error
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_article -> {
                    true // Artikel sudah aktif
                }
                R.id.action_add_story -> {
                    startActivity(Intent(this, AddStoryActivity::class.java))
                    true
                }
                R.id.action_save -> {
                    startActivity(Intent(this, SaveActivity::class.java))
                    true
                }
                R.id.action_maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu) // Memuat menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout() // Panggil fungsi logout saat item menu diklik
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Hapus sesi pengguna dari SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Menghapus semua data
        editor.apply() // Simpan perubahan

        // Arahkan kembali ke WelcomeActivity
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish() // Tutup ArticleActivity
    }
}