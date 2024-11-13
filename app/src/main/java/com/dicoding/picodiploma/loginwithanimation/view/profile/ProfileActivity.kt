package com.dicoding.picodiploma.loginwithanimation.view.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }

        // Inisialisasi Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    // Navigasi ke MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_add_story -> {
                    // Navigasi ke AddStoryActivity
                    startActivity(Intent(this, AddStoryActivity::class.java))
                    true
                }
                R.id.action_profile -> {
                    // Profile sudah di ProfileActivity, tidak perlu melakukan apa-apa
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
        viewModel.logout() // Panggil fungsi logout dari ViewModel
        startActivity(Intent(this, WelcomeActivity::class.java)) // Arahkan ke WelcomeActivity
        finish() // Tutup ProfileActivity
    }
}