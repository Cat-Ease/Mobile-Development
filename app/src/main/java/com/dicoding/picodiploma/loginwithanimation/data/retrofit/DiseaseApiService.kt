package com.dicoding.picodiploma.loginwithanimation.data.retrofit

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DiseaseApiService {
    @Multipart
    @POST("predict") // Endpoint API
    suspend fun predictDisease(@Part photo: MultipartBody.Part): Response<DiseasePredictionResponse>
}