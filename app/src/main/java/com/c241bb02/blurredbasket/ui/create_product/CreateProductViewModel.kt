package com.c241bb02.blurredbasket.ui.create_product

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import com.c241bb02.blurredbasket.data.util.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    fun createProduct(
        photos: List<MultipartBody.Part>,
        name: RequestBody,
        category: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        description: RequestBody,
    ): LiveData<Resource<GetProductsResponseItem>?> {
        return productRepository.createProduct(photos, name, category, stock, price, description)
    }
}