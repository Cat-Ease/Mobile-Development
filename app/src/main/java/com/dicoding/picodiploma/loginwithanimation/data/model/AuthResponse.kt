package com.dicoding.picodiploma.loginwithanimation.data.model

data class AuthResponse(
    val status: String, // Status dari respons
    val message: String?, // Pesan dari respons
    val loginResult: LoginResult? // Hasil login yang berisi token
)

data class LoginResult(
    val token: String // Token yang diterima setelah login
)

data class SignupResponse(
    val status: String, // Status dari respons pendaftaran
    val message: String?, // Pesan dari respons pendaftaran
    val error: Boolean? = false // Menambahkan properti error
)