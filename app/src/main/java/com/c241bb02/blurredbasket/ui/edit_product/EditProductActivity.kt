package com.c241bb02.blurredbasket.ui.edit_product

import android.content.Intent
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
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.util.Resource
import com.c241bb02.blurredbasket.databinding.ActivityCreateProductBinding
import com.c241bb02.blurredbasket.databinding.ActivityEditProductBinding
import com.c241bb02.blurredbasket.ui.create_product.CreateProductActivity
import com.c241bb02.blurredbasket.ui.create_product.CreateProductCarouselAdapter
import com.c241bb02.blurredbasket.ui.create_product.CreateProductViewModel
import com.c241bb02.blurredbasket.ui.home.HomeActivity
import com.c241bb02.blurredbasket.ui.product_detail.ProductDetailActivity
import com.c241bb02.blurredbasket.ui.profile.ProfileActivity
import com.c241bb02.blurredbasket.ui.utils.createCustomTempFile
import com.c241bb02.blurredbasket.ui.utils.reduceFileImage
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.utils.uriToFile
import com.c241bb02.blurredbasket.ui.utils.urlToFile
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private var hasEditedImages = false



    private var adapter: EditProductCarouselAdapter? = null

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
        handleInputsChanges()
    }

    private fun setupDefaultState() {
        val product = getProductParcelableExtra()
        with(binding) {
            if (product != null) {
                viewModel.downloadCurrentPhotos(downloadedImages, product, this@EditProductActivity)

                editProductNameInput.setText(product.name)
                editProductDescriptionInput.setText(product.description)
                editProductCategoryInput.setText(product.category)
                editProductPriceInput.setText(product.price.toString())
                editProductStockInput.setText(product.stock.toString())
            }
        }
    }

    private fun handleInputsChanges() {
        with(binding) {
            editProductNameInput.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isEmpty()) {
                    editProductNameInput.error = "Required"
                }
            }

            editProductDescriptionInput.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isEmpty()) {
                    editProductDescriptionInput.error = "Required"
                }
            }

            editProductCategoryInput.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isEmpty()) {
                    editProductCategoryInput.error = "Required"
                }
            }

            editProductPriceInput.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    if (text.isEmpty()) {
                        editProductPriceInput.error = "Required"
                    } else {
                        try {
                            if (text.toString().toInt() < 0) {
                                editProductPriceInput.error = "Price must be at least 0"
                            }
                        } catch (e: NumberFormatException) {
                            editProductPriceInput.error = "Value too high"
                        }
                    }
                }
            }

            editProductStockInput.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    if (text.isEmpty()) {
                        editProductStockInput.error = "Required"
                    } else {
                        try {
                            if (text.toString().toInt() < 0) {
                                editProductStockInput.error = "Stock must be at least 0"
                            }
                        } catch (e: NumberFormatException) {
                            editProductStockInput.error = "Value too high"
                        }
                    }
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
                    val filledUris = selectedImages.filter { it != Uri.EMPTY }
                    if (filledUris.isEmpty()) {
                        showToast("You have not selected any images.")
                    } else {
                        openImageViewer(selectedImages)
                    }
                }
                editProductButton.setOnClickListener {
                    val nameText = editProductNameInput.text.toString()
                    val categoryText = editProductCategoryInput.text.toString()
                    val descriptionText = editProductDescriptionInput.text.toString()
                    val priceText = editProductPriceInput.text.toString()
                    val stockText = editProductStockInput.text.toString()
                    val filledUris = selectedImages.filter { it != Uri.EMPTY }

                    if (editProductPayloadIsValid(
                            photos = filledUris,
                            name = nameText,
                            category = categoryText,
                            stock = stockText,
                            price = priceText,
                            description = descriptionText
                        )
                    ) {
                        val id = product.code
                        val name = createRequestBody(nameText)
                        val category = createRequestBody(categoryText)
                        val description = createRequestBody(descriptionText)
                        val price = createRequestBody(priceText)
                        val stock = createRequestBody(stockText)

                        var photos: ArrayList<MultipartBody.Part> = arrayListOf()
                        if (hasEditedImages) {
                            val photoFiles = downloadedImages.filter { true }
                            photos = ArrayList(photoFiles.map {
                                val requestImageFile =
                                    it.value.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                MultipartBody.Part.createFormData(
                                    "photos",
                                    it.value.name,
                                    requestImageFile
                                )
                            })
                            val newPhotos =
                                ArrayList(filledUris.filter { !it.toString().startsWith("http") }
                                    .map {
                                        val file = uriToFile(
                                            it,
                                            this@EditProductActivity
                                        )
                                        val requestImageFile =
                                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                        MultipartBody.Part.createFormData(
                                            "photos",
                                            file.name,
                                            requestImageFile
                                        )
                                    })

                            photos.addAll(newPhotos)
                        }


                        viewModel.updateProduct(
                            id,
                            photos,
                            name,
                            category,
                            stock,
                            price,
                            description
                        )
                            .observe(this@EditProductActivity) {
                                when (it) {
                                    is Resource.Loading -> {
                                        showLoadingEditProduct()
                                    }

                                    is Resource.Success -> {
                                        stopLoadingEditProduct()
                                        val response = it.data
                                        if (response != null) {
                                            val numberOfPasses =
                                                response.photos.filter { photo -> photo.status != "Blur" }.size
                                            if (response.status == "ACCEPTED") {
                                                val message =
                                                    if (numberOfPasses < response.photos.size)
                                                        "Your product is now live. However, after reviewing your product, ${response.photos.size - numberOfPasses} photos are blurred. These photos won't be shown to customers, but you can still see them by opening this product from your profile screen. You can update your product any time from the product detail screen."
                                                    else
                                                        "Your product is now live. You can update your product any time from the product detail screen."

                                                showAcceptedDialog(message, product = response)

                                            } else {
                                                showBannedDialog(numberOfPasses, product = response)
                                            }

                                        }
                                    }

                                    is Resource.Error -> {
                                        stopLoadingEditProduct()
                                        showErrorDialog()
                                    }

                                    else -> {
                                        stopLoadingEditProduct()
                                        showErrorDialog()
                                    }
                                }
                            }

                    }
                }
            }
        }
    }


    private fun editProductPayloadIsValid(
        photos: List<Uri>,
        name: String,
        category: String,
        stock: String,
        price: String,
        description: String,
    ): Boolean {
        if (photos.size < 3) {
            showToast("You must select at least 3 photos.")
            return false
        }
        if (photos.size > 6) {
            showToast("You can only select up to 5 photos.")
            return false
        }
        if (name.isEmpty()) {
            showToast("You must enter a product name.")
            return false
        }
        if (category.isEmpty()) {
            showToast("You must enter a product category.")
            return false
        }

        try {
            if (price.toInt() < 0) {
                showToast("Product price cannot be negative.")
                return false
            }
        } catch (e: NumberFormatException) {
            showToast("Product price is too high.")
            return false
        }

        try {
            if (stock.toInt() < 0) {
                showToast("Product stock cannot be negative.")
                return false
            }
        } catch (e: NumberFormatException) {
            showToast("Product stock is too high.")
            return false
        }

        if (price.toInt() < 0) {
            showToast("Product price cannot be negative.")
            return false
        }
        if (description.isEmpty()) {
            showToast("You must enter a product category.")
            return false
        }

        return true
    }

    private fun showLoadingEditProduct() {
        binding.editProductButton.isEnabled = false
        binding.editProductButton.text = "Editing product..."
    }

    private fun stopLoadingEditProduct() {
        binding.editProductButton.isEnabled = true
        binding.editProductButton.text = "Edit Product"
    }

    private fun showAcceptedDialog(message: String, product: GetProductsResponseItem) {
        MaterialAlertDialogBuilder(this@EditProductActivity)
            .setCancelable(false)
            .setTitle("Success!")
            .setMessage(message)
            .setNegativeButton("Home") { dialog, _ ->
                moveToHomeScreen()
                dialog.dismiss()
            }
            .setPositiveButton("Profile") { dialog, _ ->
                moveToProfileScreen()
                dialog.dismiss()
            }
            .show()
    }

    private fun showBannedDialog(numberOfPasses: Int, product: GetProductsResponseItem) {
        MaterialAlertDialogBuilder(this@EditProductActivity)
            .setCancelable(false)
            .setTitle("Whoops!")
            .setMessage("After reviewing your product, we concluded that your product has not met the standard. A product must have at least 3 non-blurred photos, but you have uploaded $numberOfPasses non-blurred photos. Your product will not be displayed to customers, but you can still see it in your profile screen. You can update your product any time from the product detail screen.")
            .setNegativeButton("Home") { dialog, _ ->
                moveToHomeScreen()
                dialog.dismiss()
            }
            .setPositiveButton("Profile") { dialog, _ ->
                moveToProfileScreen()
                dialog.dismiss()
            }
            .show()
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(this@EditProductActivity)
            .setCancelable(false)
            .setTitle("Something went wrong")
            .setMessage("An error occurred while editing the product.")
            .setNeutralButton("Home") { dialog, _ ->
                moveToHomeScreen()
                dialog.dismiss()
            }
            .setNegativeButton("Profile") { dialog, _ ->
                moveToProfileScreen()
                dialog.dismiss()
            }
            .setPositiveButton("Try again") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun moveToHomeScreen() {
        val intent = Intent(this@EditProductActivity, HomeActivity::class.java)
        startActivity(intent)
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

            viewModel.isLoading.observe(this) {
                if (it) {
                    startSkeletonLoader()
                } else {
                    stopSkeletonLoader()
                }
            }

            val snapHelper = CarouselSnapHelper()
            carouselView.onFlingListener = null
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
                    hasEditedImages = true
                }
            }
            )
        }
    }

    private fun startSkeletonLoader() {
        binding.editProductImageCarousel.visibility = View.INVISIBLE
        binding.editProductCarouselShimmerLayout.apply {
            visibility = View.VISIBLE
            startShimmer()
        }
        binding.editProductButton.isEnabled = false
    }

    private fun stopSkeletonLoader() {
        binding.editProductImageCarousel.visibility = View.VISIBLE
        binding.editProductCarouselShimmerLayout.apply {
            visibility = View.GONE
            stopShimmer()
        }
        binding.editProductButton.isEnabled = true
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

    private fun moveToProfileScreen() {
        val moveIntent = Intent(this, ProfileActivity::class.java)
        startActivity(moveIntent)
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
            adapter?.updateData(0, selectedImages)
            showToast("You have selected ${uris.size} photos.")
            hasEditedImages = true
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): EditProductViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[EditProductViewModel::class.java]
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
        private const val MAX_IMAGES = 5
    }
}