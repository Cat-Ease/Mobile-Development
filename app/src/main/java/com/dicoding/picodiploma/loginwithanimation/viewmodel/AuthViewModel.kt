package com.dicoding.picodiploma.loginwithanimation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.AuthResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.SignupResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(name: String, email: String, password: String, onResult: (SignupResponse) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.register(name, email, password) { response ->
                    onResult(response)
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Terjadi kesalahan"
                onResult(SignupResponse("error", errorMessage))
            } catch (e: Exception) {
                onResult(SignupResponse("error", e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    fun login(email: String, password: String, onResult: (AuthResponse) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.login(email, password) { response ->
                    onResult(response)
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Terjadi kesalahan"
                onResult(AuthResponse("error", errorMessage, null))
            } catch (e: Exception) {
                onResult(AuthResponse("error", e.message ?: "Terjadi kesalahan", null))
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            authRepository.saveSession(user)
        }
    }
}