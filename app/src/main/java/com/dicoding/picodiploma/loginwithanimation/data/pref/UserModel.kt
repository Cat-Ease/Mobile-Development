package com.dicoding.picodiploma.loginwithanimation.data.pref

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val password: String,
    val isLogin: Boolean = true
)