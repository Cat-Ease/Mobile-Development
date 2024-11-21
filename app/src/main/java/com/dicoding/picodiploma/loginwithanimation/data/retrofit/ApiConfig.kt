package com.dicoding.picodiploma.loginwithanimation.data.retrofit

import com.dicoding.picodiploma.loginwithanimation.data.api.AuthApi
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    private const val BASE_URL_AUTH = "https://authentication-684282003208.asia-southeast2.run.app/"
    private const val BASE_URL_STORY = "https://story-api.dicoding.dev/v1/"

    // Fungsi untuk mendapatkan AuthApi
    fun getAuthApiService(): AuthApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(AuthApi::class.java)
    }

    // Fungsi untuk mendapatkan ApiService
    fun getApiService(token: String? = null): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)

        // Tambahkan interceptor untuk token jika token tidak null
        token?.let {
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer $it")
                    .build()
                chain.proceed(requestHeaders)
            }
            clientBuilder.addInterceptor(authInterceptor)
        }

        val client = clientBuilder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_STORY)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}