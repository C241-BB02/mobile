package com.c241bb02.blurredbasket.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.c241bb02.blurredbasket.api.ApiConfig
import com.c241bb02.blurredbasket.api.ApiService
import com.c241bb02.blurredbasket.api.product.DeleteProductResponse
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.pref.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part

class ProductRepository(
    private var apiService: ApiService,
) {
    private val _products = MutableLiveData<List<GetProductsResponseItem>?>()
    private val products: LiveData<List<GetProductsResponseItem>?> = _products

    private val _sellerProducts = MutableLiveData<List<GetProductsResponseItem>?>()
    private val sellerProducts: LiveData<List<GetProductsResponseItem>?> = _sellerProducts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun getProducts(): LiveData<List<GetProductsResponseItem>?> {
        _isLoading.value = true
        val client = apiService.getProducts()
        client.enqueue(object : Callback<List<GetProductsResponseItem>> {
            override fun onResponse(
                call: Call<List<GetProductsResponseItem>>,
                response: Response<List<GetProductsResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _products.value = responseBody
                    }
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GetProductsResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return products
    }

    fun getSellerProducts(sellerId: String): LiveData<List<GetProductsResponseItem>?> {
        _isLoading.value = true
        val client = apiService.getSellerProducts(sellerId)
        client.enqueue(object : Callback<List<GetProductsResponseItem>> {
            override fun onResponse(
                call: Call<List<GetProductsResponseItem>>,
                response: Response<List<GetProductsResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _sellerProducts.value = responseBody
                    }
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GetProductsResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return sellerProducts
    }

    suspend fun createProduct(
        photos: List<MultipartBody.Part>,
        name: RequestBody,
        category: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        description: RequestBody,
    ): GetProductsResponseItem {
        return apiService.createProduct(photos, name, category, stock, price, description)
    }

    suspend fun deleteProduct(productCode: String): DeleteProductResponse {
        return apiService.deleteProduct(productCode)
    }

    fun updateToken(token: String) {
        apiService = ApiConfig.getApiService(token)
    }

    companion object {
        private const val TAG = "ProductRepository"

        @Volatile
        private var instance: ProductRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): ProductRepository =
            instance ?: synchronized(this) {
                instance ?: ProductRepository(apiService)
            }.also { instance = it }
    }
}