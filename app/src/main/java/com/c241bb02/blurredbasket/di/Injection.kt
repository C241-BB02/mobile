package com.c241bb02.blurredbasket.di

import android.content.Context
import com.c241bb02.blurredbasket.api.ApiConfig
import com.c241bb02.blurredbasket.data.pref.UserPreference
import com.c241bb02.blurredbasket.data.pref.dataStore
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import com.c241bb02.blurredbasket.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }

    fun provideProductRepository(context: Context): ProductRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return ProductRepository.getInstance(apiService, pref)
    }
}