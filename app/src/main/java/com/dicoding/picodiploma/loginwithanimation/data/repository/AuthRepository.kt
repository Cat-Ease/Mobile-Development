package com.dicoding.picodiploma.loginwithanimation.data.repository

import com.dicoding.picodiploma.loginwithanimation.data.api.AuthApi
import com.dicoding.picodiploma.loginwithanimation.data.model.AuthResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.SignupResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val authApi: AuthApi, private val userPreferences: UserPreference) {

    // Fungsi untuk registrasi
    fun register(name: String, email: String, password: String, callback: (SignupResponse) -> Unit) {
        val user = UserModel(name, email, token = "", password = password) // Menggunakan nama dan password
        authApi.signup(user).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                callback(response.body() ?: SignupResponse("error", "Registration failed"))
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                callback(SignupResponse("error", t.message ?: "Unknown error"))
            }
        })
    }

    // Fungsi untuk login
    fun login(email: String, password: String, callback: (AuthResponse) -> Unit) {
        val user = UserModel(name = "", email = email, token = "", password = password) // Menggunakan email dan password
        authApi.login(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                callback(response.body() ?: AuthResponse("error", "Login failed", null))
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(AuthResponse("error", t.message ?: "Unknown error", null))
            }
        })
    }

    // Fungsi untuk menyimpan sesi pengguna
    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }
}