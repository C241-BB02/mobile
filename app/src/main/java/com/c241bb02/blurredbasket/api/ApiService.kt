package com.c241bb02.blurredbasket.api

import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("register/")
    suspend fun register(@Body dto: RegisterRequestDto): RegisterResponse
}