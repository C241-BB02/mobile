package com.c241bb02.blurredbasket.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.pref.UserModel
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import com.c241bb02.blurredbasket.data.repository.UserRepository
import com.c241bb02.blurredbasket.data.util.Resource

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun getSellerProducts(sellerId: String): LiveData<Resource<List<GetProductsResponseItem>>?> {
        return productRepository.getSellerProducts(sellerId)
    }

    suspend fun logout() {
        userRepository.logout()
    }
}