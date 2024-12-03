package com.dicoding.picodiploma.loginwithanimation.view.addstory

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DiseaseApiService {
    @Multipart
    @POST("/predict")
    fun predictDisease(@Part photo: MultipartBody.Part): Call<DiseasePredictionResponse>
}