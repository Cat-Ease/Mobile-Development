package com.dicoding.picodiploma.loginwithanimation.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(dataStore).also { INSTANCE = it }
            }
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = user.isLogin // Menyimpan nilai boolean dari UserModel
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                name = "", // Kosongkan nama karena tidak disimpan
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                password = "", // Kosongkan password karena tidak disimpan
                isLogin = preferences[IS_LOGIN_KEY] ?: false // Mengambil nilai boolean
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}