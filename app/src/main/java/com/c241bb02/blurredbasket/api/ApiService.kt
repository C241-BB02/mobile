package com.c241bb02.blurredbasket.api

import com.c241bb02.blurredbasket.api.auth.LoginRequestDto
import com.c241bb02.blurredbasket.api.auth.LoginResponse
import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterResponse
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @POST("register/")
    suspend fun register(@Body dto: RegisterRequestDto): RegisterResponse

    @POST("token/")
    suspend fun login(@Body dto: LoginRequestDto): LoginResponse

    @GET("products/status/ACCEPTED")
    fun getProducts(): Call<List<GetProductsResponseItem>>

    @GET("products/seller/{id}")
    fun getSellerProducts(
        @Path("id") id: String
    ): Call<List<GetProductsResponseItem>>
}