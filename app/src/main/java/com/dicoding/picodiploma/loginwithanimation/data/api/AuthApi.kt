package com.dicoding.picodiploma.loginwithanimation.data.api

import com.dicoding.picodiploma.loginwithanimation.data.model.AuthResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.SignupResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("/register") // Endpoint untuk signup
    fun signup(@Body user: UserModel): Call<SignupResponse>

    @POST("/login") // Endpoint untuk login
    fun login(@Body user: UserModel): Call<AuthResponse>

    @GET("/users") // Endpoint untuk mendapatkan semua pengguna
    fun getAllUsers(): Call<List<UserModel>>
}