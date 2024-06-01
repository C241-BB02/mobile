package com.c241bb02.blurredbasket.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.repository.ProductRepository

class HomeViewModel(private val productRepository: ProductRepository): ViewModel() {
    fun getProducts(): LiveData<List<GetProductsResponseItem>?> {
        return productRepository.getProducts()
    }

    companion object{
        private const val TAG = "HomeViewModel"
    }
}