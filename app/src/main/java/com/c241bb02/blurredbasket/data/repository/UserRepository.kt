package com.c241bb02.blurredbasket.data.repository

import com.c241bb02.blurredbasket.api.ApiConfig
import com.c241bb02.blurredbasket.api.ApiService
import com.c241bb02.blurredbasket.api.auth.LoginRequestDto
import com.c241bb02.blurredbasket.api.auth.LoginResponse
import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterResponse
import com.c241bb02.blurredbasket.data.pref.UserModel
import com.c241bb02.blurredbasket.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private var apiService: ApiService,
    private val pref: UserPreference
) {
    // Auth related
    suspend fun saveSession(user: UserModel) {
        pref.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return pref.getSession()
    }

    suspend fun register(
        dto: RegisterRequestDto
    ): RegisterResponse {
        return apiService.register(dto)
    }

    suspend fun login(
        dto: LoginRequestDto
    ): LoginResponse {
        return apiService.login(dto)
    }

    suspend fun logout() {
        pref.logout()
    }

    fun updateToken(token: String) {
        apiService = ApiConfig.getApiService(token)
    }

    companion object {
        private const val TAG = "UserRepository"

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreference,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, pref)
            }.also { instance = it }
    }
}