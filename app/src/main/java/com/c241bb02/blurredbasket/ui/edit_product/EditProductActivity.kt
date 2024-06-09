package com.c241bb02.blurredbasket.ui.edit_product

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.databinding.ActivityCreateProductBinding
import com.c241bb02.blurredbasket.databinding.ActivityEditProductBinding
import com.c241bb02.blurredbasket.ui.create_product.CreateProductCarouselAdapter
import com.c241bb02.blurredbasket.ui.create_product.CreateProductViewModel
import com.c241bb02.blurredbasket.ui.product_detail.ProductDetailActivity
import com.c241bb02.blurredbasket.ui.utils.createCustomTempFile
import com.c241bb02.blurredbasket.ui.utils.reduceFileImage
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.utils.uriToFile
import com.c241bb02.blurredbasket.ui.utils.urlToFile
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProductBinding
    private lateinit var selectedImages: ArrayList<Uri>
    private lateinit var viewModel: EditProductViewModel
    private var downloadedImages = mutableMapOf<String, File>()

    private var adapter: EditProductCarouselAdapter? = null
    private val MAX_IMAGES = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setupStatusBar(window, this, R.color.blue_50, true)
        setupImageCarousel()
        setupDefaultState()
        setupButtons()
    }

    private fun setupDefaultState() {
        val product = getProductParcelableExtra()
        with(binding) {
            if (product != null) {
                downloadCurrentPhotos(product)

                editProductNameInput.setText(product.name)
                editProductDescriptionInput.setText(product.description)
                editProductCategoryInput.setText(product.category)
                editProductPriceInput.setText(product.price.toString())
                editProductStockInput.setText(product.stock.toString())
            }
        }
    }

    private fun downloadCurrentPhotos(product: GetProductsResponseItem) {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            product.photos.forEach {
                try {
                    val file = urlToFile(it.image, this@EditProductActivity)
                    downloadedImages[it.image] = file
                    Log.d("SUCCESS DOWNLOAD", file.absolutePath)
                } catch (e: IOException) {
                    Log.d("EDIT ERROR", e.toString())
                }
            }
        }
    }

    private fun getProductParcelableExtra(): GetProductsResponseItem? {
        val product = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                EXTRA_PRODUCT,
                GetProductsResponseItem::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PRODUCT)
        }

        return product
    }


    private fun setupButtons() {
        val product = getProductParcelableExtra()
        if (product != null) {
            with(binding) {
                editProductPreviewImageButton.setOnClickListener {
                    val images = selectedImages
                    openImageViewer(images)
                }
                editProductButton.setOnClickListener {
                    val id = product.code
                    val name = createRequestBody(editProductNameInput.text.toString())
                    val category = createRequestBody(editProductCategoryInput.text.toString())
                    val description = createRequestBody(editProductDescriptionInput.text.toString())
                    val price = createRequestBody(editProductPriceInput.text.toString())
                    val stock = createRequestBody(editProductStockInput.text.toString())
                    val filledUris = selectedImages.filter { it != Uri.EMPTY }
                    val photoFiles = downloadedImages.filter { true }
                    val photos = ArrayList(photoFiles.map {
                        val requestImageFile =
                            it.value.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("photos", it.value.name, requestImageFile)
                    })
                    val newPhotos =
                        ArrayList(filledUris.filter { !it.toString().startsWith("http") }.map {
                            val file = uriToFile(
                                it,
                                this@EditProductActivity
                            )
                            val requestImageFile =
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("photos", file.name, requestImageFile)
                        })

                    photos.addAll(newPhotos)

                    lifecycleScope.launch {
                        try {
                            viewModel.updateProduct(
                                id,
                                photos,
                                name,
                                category,
                                stock,
                                price,
                                description
                            )
                            showToast("Your product has been updated!")

                        } catch (e: HttpException) {
                            showToast("An error occurred while updating product. Please try again.")
                        }
                    }
                }
            }
        }
    }

    private fun createRequestBody(text: String): RequestBody {
        return text.trim().toRequestBody("text/plain".toMediaType())
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupImageCarousel() {
        val product = getProductParcelableExtra()
        if (product != null) {
            selectedImages = ArrayList(product.photos.map { Uri.parse(it.image) })

            for (i in 1..(MAX_IMAGES - selectedImages.size)) {
                selectedImages.add(Uri.EMPTY)
            }

            val carouselView = binding.editProductImageCarousel
            adapter = EditProductCarouselAdapter(selectedImages)

            val snapHelper = CarouselSnapHelper()
            snapHelper.attachToRecyclerView(carouselView)

            val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
            carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

            carouselView.layoutManager = carouselLayoutManager
            carouselView.adapter = adapter
            adapter?.setOnItemClickCallback(object :
                EditProductCarouselAdapter.OnItemClickCallback {
                override fun onItemClicked(view: View) {
                    startGallery()
                }

                override fun onDeleteClicked(view: View, itemPosition: Int, image: Uri) {
                    selectedImages.removeAt(itemPosition)
                    selectedImages.add(Uri.EMPTY)
                    downloadedImages.remove(image.toString())
                    adapter?.updateData(itemPosition, selectedImages)
                }
            }
            )
        }
    }

    private fun openImageViewer(images: List<Uri>) {
        val filledUris = images.filter { it != Uri.EMPTY }
        StfalconImageViewer.Builder(this, filledUris) { view, image ->
            if (image.toString().startsWith("http")) {
                Glide.with(this)
                    .load(image)
                    .into(view)
            } else {
                view.setImageURI(image)
            }

        }.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupTransition() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "create_product_transition"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 500L

        }

        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 400L
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGES)
    ) { uris ->
        val filledUris = selectedImages.filter { it != Uri.EMPTY }
        if (uris.size + filledUris.size > MAX_IMAGES) {
            showToast("You can only pick at most $MAX_IMAGES photos.")
        } else {
            val newImages = ArrayList(uris)
            val startIndex = filledUris.size
            for (index in 0..<newImages.size) {
                selectedImages[startIndex + index] = newImages[index]
            }
            adapter?.updateData(startIndex, newImages)
            showToast("You have selected ${uris.size} photos.")
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): EditProductViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[EditProductViewModel::class.java]
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}