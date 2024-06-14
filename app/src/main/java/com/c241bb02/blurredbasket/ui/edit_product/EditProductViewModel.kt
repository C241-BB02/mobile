package com.c241bb02.blurredbasket.ui.edit_product

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.repository.ProductRepository
import com.c241bb02.blurredbasket.data.util.Resource
import com.c241bb02.blurredbasket.ui.utils.urlToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class EditProductViewModel(private val productRepository: ProductRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun updateProduct(
        id: String,
        photos: List<MultipartBody.Part>,
        name: RequestBody,
        category: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        description: RequestBody,
    ): LiveData<Resource<GetProductsResponseItem>?> {
        return productRepository.updateProduct(id, photos, name, category, stock, price, description)
    }

    fun downloadCurrentPhotos(downloadedImages: MutableMap<String, File>, product: GetProductsResponseItem, context: Context) {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            _isLoading.postValue(true)
            product.photos.forEach {
                try {
                    val file = urlToFile(it.image, context)
                    downloadedImages[it.image] = file
                    Log.d("SUCCESS DOWNLOAD", file.absolutePath)
                } catch (e: IOException) {
                    Log.d("EDIT ERROR", e.toString())
                }
            }
            _isLoading.postValue(false)
        }
    }
}