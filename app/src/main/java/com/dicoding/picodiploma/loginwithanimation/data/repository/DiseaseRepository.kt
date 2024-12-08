package com.dicoding.picodiploma.loginwithanimation.data.repository

import com.dicoding.picodiploma.loginwithanimation.data.retrofit.DiseaseApiService
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.DiseasePredictionResponse
import okhttp3.MultipartBody
import retrofit2.Response

class DiseaseRepository(private val apiService: DiseaseApiService) {
    suspend fun predictDisease(photo: MultipartBody.Part): Response<DiseasePredictionResponse> {
        return apiService.predictDisease(photo)
    }
}