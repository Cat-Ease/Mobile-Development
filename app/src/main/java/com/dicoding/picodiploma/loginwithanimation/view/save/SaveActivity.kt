package com.dicoding.picodiploma.loginwithanimation.view.save

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.article.ArticleActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class SaveActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var savedResultsAdapter: SavedResultsAdapter
    private val savedResults = mutableListOf<String>() // Menyimpan hasil yang disimpan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Menampilkan hasil yang disimpan
        displaySavedResults()

        setupBottomNavigation()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_save
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
                    startActivity(Intent(this, ArticleActivity::class.java))
                    true
                }
                R.id.action_add_story -> {
                    startActivity(Intent(this, AddStoryActivity::class.java))
                    true
                }
                R.id.action_save -> {
                    true // Sudah berada di SaveActivity
                }
                R.id.action_maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun displaySavedResults() {
        val fileName = "prediction_result.txt"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        if (file.exists()) {
            try {
                val inputStream = FileInputStream(file)
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    savedResults.add(line ?: "")
                }

                savedResultsAdapter = SavedResultsAdapter(savedResults)
                recyclerView.adapter = savedResultsAdapter
                reader.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Menangani jika tidak ada hasil yang disimpan
            savedResults.add("No saved results found.")
            savedResultsAdapter = SavedResultsAdapter(savedResults)
            recyclerView.adapter = savedResultsAdapter
        }
    }
}