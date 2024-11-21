package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.loginwithanimation.data.model.AuthResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.viewmodel.AuthViewModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi UserPreference dan AuthRepository
        val userPreferences = UserPreference.getInstance(dataStore)
        val authRepository = AuthRepository(ApiConfig.getAuthApiService(), userPreferences)
        authViewModel = AuthViewModel(authRepository)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Tampilkan ProgressBar
            binding.progressBar.visibility = android.view.View.VISIBLE

            // Melakukan login
            val userModel = UserModel(name = "", email = email, token = "", password = password) // Menggunakan string kosong untuk name
            val authApi = ApiConfig.getAuthApiService()
            authApi.login(userModel).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    binding.progressBar.visibility = android.view.View.GONE

                    if (response.isSuccessful && response.body()?.status == "success") {
                        // Simpan sesi pengguna
                        val token = response.body()?.loginResult?.token ?: ""
                        authViewModel.saveSession(UserModel(name = "", email = email, token = token, password = password))

                        // Tampilkan dialog sukses
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Success!")
                            setMessage("Login success.")
                            setPositiveButton("Next") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        showErrorDialog(response.body()?.message ?: "Email atau password salah.")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    binding.progressBar.visibility = android.view.View.GONE
                    showErrorDialog("Terjadi kesalahan: ${t.message}")
                }
            })
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }
}