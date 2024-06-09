package com.c241bb02.blurredbasket.ui.edit_product

import androidx.lifecycle.ViewModel
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EditProductViewModel(private val productRepository: ProductRepository): ViewModel() {
    suspend fun updateProduct(
        id: String,
        photos: List<MultipartBody.Part>,
        name: RequestBody,
        category: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        description: RequestBody,
    ): GetProductsResponseItem {
        return productRepository.updateProduct(id, photos, name, category, stock, price, description)
    }
}