package com.c241bb02.blurredbasket.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import com.c241bb02.blurredbasket.data.repository.UserRepository
import com.c241bb02.blurredbasket.di.Injection
import com.c241bb02.blurredbasket.ui.create_product.CreateProductViewModel
import com.c241bb02.blurredbasket.ui.home.HomeViewModel
import com.c241bb02.blurredbasket.ui.onboarding.OnboardingViewModel
import com.c241bb02.blurredbasket.ui.product_detail.ProductDetailViewModel
import com.c241bb02.blurredbasket.ui.profile.ProfileViewModel
import com.c241bb02.blurredbasket.ui.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(userRepository, productRepository) as T
        }
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(userRepository, productRepository) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(productRepository) as T
        }
        if (modelClass.isAssignableFrom(CreateProductViewModel::class.java)) {
            return CreateProductViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    fun updateToken(token: String) {
        userRepository.updateToken(token)
        productRepository.updateToken(token)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideProductRepository(context)
                )
            }.also { instance = it }
    }
}