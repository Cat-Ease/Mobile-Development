package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.article.Article
import com.dicoding.picodiploma.loginwithanimation.view.article.ArticleActivity
import com.dicoding.picodiploma.loginwithanimation.view.article.ArticleAdapter
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.save.SaveActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        // Inisialisasi ViewPager
        viewPager = findViewById(R.id.viewPager)
        val images = listOf(R.drawable.img_1, R.drawable.img_2, R.drawable.img_3) // Ganti dengan gambar Anda
        val adapter = ImageSliderAdapter(this, images)
        viewPager.adapter = adapter

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recycler_view_articles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ambil data artikel dari API
        fetchArticles()

        // Setup BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> true
                R.id.action_article -> {
                    startActivity(Intent(this, ArticleActivity::class.java))
                    true
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

        // Inisialisasi tombol untuk navigasi ke ArticleActivity
        val buttonCheckAll = findViewById<Button>(R.id.button_check_all)
        buttonCheckAll.setOnClickListener {
            // Navigasi ke ArticleActivity
            startActivity(Intent(this, ArticleActivity::class.java))
        }
    }

    private fun fetchArticles() {
        val url = "https://api-article-785296543353.asia-southeast2.run.app/articles"
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
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
        finish() // Tutup MainActivity
    }
}