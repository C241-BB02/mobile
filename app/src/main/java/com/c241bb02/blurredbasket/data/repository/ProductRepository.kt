package com.c241bb02.blurredbasket.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.c241bb02.blurredbasket.api.ApiConfig
import com.c241bb02.blurredbasket.api.ApiService
import com.c241bb02.blurredbasket.api.product.DeleteProductResponse
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.util.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository(
    private var apiService: ApiService,
) {
    private val _products = MutableLiveData<Resource<List<GetProductsResponseItem>>?>()
    private val products: LiveData<Resource<List<GetProductsResponseItem>>?> = _products

    private val _sellerProducts = MutableLiveData<Resource<List<GetProductsResponseItem>>?>()
    private val sellerProducts: LiveData<Resource<List<GetProductsResponseItem>>?> = _sellerProducts

    private val _createProductResponse = MutableLiveData<Resource<GetProductsResponseItem>?>()
    private val createProductResponse: LiveData<Resource<GetProductsResponseItem>?> = _createProductResponse

    private val _updateProductResponse = MutableLiveData<Resource<GetProductsResponseItem>?>()
    private val updateProductResponse: LiveData<Resource<GetProductsResponseItem>?> = _updateProductResponse

    private val _deleteProductResponse = MutableLiveData<Resource<DeleteProductResponse>?>()
    private val deleteProductResponse: LiveData<Resource<DeleteProductResponse>?> = _deleteProductResponse

    fun getProducts(): LiveData<Resource<List<GetProductsResponseItem>>?> {
        _products.value = Resource.Loading()
        val client = apiService.getProducts()
        client.enqueue(object : Callback<List<GetProductsResponseItem>> {
            override fun onResponse(
                call: Call<List<GetProductsResponseItem>>,
                response: Response<List<GetProductsResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _products.value = Resource.Success(responseBody)
                    }
                } else {
                    _products.value = Resource.Error(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GetProductsResponseItem>>, t: Throwable) {
                _products.value = Resource.Error(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return products
    }

    fun getSellerProducts(sellerId: String): LiveData<Resource<List<GetProductsResponseItem>>?> {
        _sellerProducts.value = Resource.Loading()
        val client = apiService.getSellerProducts(sellerId)
        client.enqueue(object : Callback<List<GetProductsResponseItem>> {
            override fun onResponse(
                call: Call<List<GetProductsResponseItem>>,
                response: Response<List<GetProductsResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _sellerProducts.value = Resource.Success(responseBody)
                    }
                } else {
                    _sellerProducts.value = Resource.Error(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GetProductsResponseItem>>, t: Throwable) {
                _sellerProducts.value = Resource.Error(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return sellerProducts
    }

    fun createProduct(
        photos: List<MultipartBody.Part>,
        name: RequestBody,
        category: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        description: RequestBody,
    ): LiveData<Resource<GetProductsResponseItem>?> {
        _createProductResponse.value = Resource.Loading()
        val client = apiService.createProduct(photos, name, category, stock, price, description)
        client.enqueue(object : Callback<GetProductsResponseItem> {
            override fun onResponse(
                call: Call<GetProductsResponseItem>,
                response: Response<GetProductsResponseItem>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _createProductResponse.value = Resource.Success(responseBody)
                    }
                } else {
                    _createProductResponse.value = Resource.Error(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetProductsResponseItem>, t: Throwable) {
                _createProductResponse.value = Resource.Error(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return createProductResponse

    }

    fun updateProduct(
        id: String,
        photos: List<MultipartBody.Part>,
        name: RequestBody,
        category: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        description: RequestBody,
    ): LiveData<Resource<GetProductsResponseItem>?> {
        _updateProductResponse.value = Resource.Loading()
        val client = apiService.updateProduct(id, photos, name, category, stock, price, description)
        client.enqueue(object : Callback<GetProductsResponseItem> {
            override fun onResponse(
                call: Call<GetProductsResponseItem>,
                response: Response<GetProductsResponseItem>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _updateProductResponse.value = Resource.Success(responseBody)
                    }
                } else {
                    _updateProductResponse.value = Resource.Error(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetProductsResponseItem>, t: Throwable) {
                _updateProductResponse.value = Resource.Error(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return updateProductResponse
    }

    fun deleteProduct(productCode: String): LiveData<Resource<DeleteProductResponse>?> {
        _deleteProductResponse.value = Resource.Loading()
        val client = apiService.deleteProduct(productCode)
        client.enqueue(object : Callback<DeleteProductResponse> {
            override fun onResponse(
                call: Call<DeleteProductResponse>,
                response: Response<DeleteProductResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _deleteProductResponse.value = Resource.Success(responseBody)
                    }
                } else {
                    _deleteProductResponse.value = Resource.Error(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DeleteProductResponse>, t: Throwable) {
                _deleteProductResponse.value = Resource.Error(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

        return deleteProductResponse
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