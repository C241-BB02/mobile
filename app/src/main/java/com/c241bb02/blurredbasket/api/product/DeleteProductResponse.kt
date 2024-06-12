package com.c241bb02.blurredbasket.api.product

import com.google.gson.annotations.SerializedName

data class DeleteProductResponse (

    @field:SerializedName("message")
    val message: String = "",

    @field:SerializedName("product")
    val product: GetProductsResponseItem? = null,
)
