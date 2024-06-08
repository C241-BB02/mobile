package com.c241bb02.blurredbasket.api

import com.c241bb02.blurredbasket.api.auth.LoginRequestDto
import com.c241bb02.blurredbasket.api.auth.LoginResponse
import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterResponse
import com.c241bb02.blurredbasket.api.product.DeleteProductResponse
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @Multipart
    @POST("create-product/")
    suspend fun createProduct(
        @Part photos: List<MultipartBody.Part>,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
    ): GetProductsResponseItem

    @DELETE("product/delete/{productCode}/")
    suspend fun deleteProduct(
        @Path("productCode") productCode: String
    ): DeleteProductResponse
}