package com.dicoding.picodiploma.loginwithanimation.view.article

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
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
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var searchArticle: EditText
    private lateinit var buttonSearch: Button
    private var articleList: MutableList<Article> = mutableListOf() // Menyimpan semua artikel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        // Inisialisasi Views
        breakingNewsTitle = findViewById(R.id.breaking_news_title)
        recyclerView = findViewById(R.id.recycler_view_articles)
        searchArticle = findViewById(R.id.search_article)
        buttonSearch = findViewById(R.id.button_search)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mengatur konten judul berita
        breakingNewsTitle.text = "Article"

        // Ambil data dari API
        fetchArticles()

        // Setup Bottom Navigation
        setupBottomNavigation()

        // Setup listener untuk tombol pencarian
        buttonSearch.setOnClickListener {
            val query = searchArticle.text.toString()
            filterArticles(query)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_article
    }

    private fun fetchArticles() {
        val url = "https://api-article-785296543353.asia-southeast2.run.app/articles"
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                // Proses data JSON dan update RecyclerView
                val articles = response.getJSONObject("data").getJSONArray("articles")
                for (i in 0 until articles.length()) {
                    val articleJson = articles.getJSONObject(i)
                    val article = Article(
                        articleJson.getString("id"),
                        articleJson.getString("title"),
                        articleJson.getString("description"),
                        articleJson.getString("image")
                    )
                    articleList.add(article) // Simpan semua artikel
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

    private fun filterArticles(query: String) {
        val filteredList = articleList.filter { article ->
            article.title.contains(query, ignoreCase = true) ||
                    article.description.contains(query, ignoreCase = true)
        }
        articleAdapter = ArticleAdapter(filteredList) // Update adapter dengan artikel yang difilter
        recyclerView.adapter = articleAdapter
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