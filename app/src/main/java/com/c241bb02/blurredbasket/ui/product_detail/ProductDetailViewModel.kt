package com.c241bb02.blurredbasket.ui.product_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.c241bb02.blurredbasket.api.product.DeleteProductResponse
import com.c241bb02.blurredbasket.data.pref.UserModel
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import com.c241bb02.blurredbasket.data.repository.UserRepository
import com.c241bb02.blurredbasket.data.util.Resource
import retrofit2.Call

class ProductDetailViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun deleteProduct(productCode: String): LiveData<Resource<DeleteProductResponse>?> {
        return productRepository.deleteProduct(productCode)
    }
}