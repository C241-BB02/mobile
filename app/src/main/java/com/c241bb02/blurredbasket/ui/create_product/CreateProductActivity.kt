package com.c241bb02.blurredbasket.ui.create_product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.util.Resource
import com.c241bb02.blurredbasket.databinding.ActivityCreateProductBinding
import com.c241bb02.blurredbasket.ui.edit_product.EditProductActivity
import com.c241bb02.blurredbasket.ui.home.HomeActivity
import com.c241bb02.blurredbasket.ui.product_detail.ProductDetailActivity
import com.c241bb02.blurredbasket.ui.profile.ProfileActivity
import com.c241bb02.blurredbasket.ui.utils.reduceFileImage
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.utils.uriToFile
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.stfalcon.imageviewer.StfalconImageViewer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateProductBinding
    private lateinit var viewModel: CreateProductViewModel

    private var selectedImages: ArrayList<Uri> = arrayListOf(
        Uri.EMPTY,
        Uri.EMPTY,
        Uri.EMPTY,
        Uri.EMPTY,
        Uri.EMPTY,
    )
    private var adapter: CreateProductCarouselAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityCreateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setupStatusBar(window, this, R.color.blue_50, true)
        setDefaultBackBehavior()
        setupImageCarousel()
        setupButtons()
    }

    private fun setDefaultBackBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            moveToHomeScreen()
        }
    }

    private fun moveToHomeScreen() {
        val intent = Intent(this@CreateProductActivity, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun setupButtons() {
        with(binding) {
            createProductPreviewImageButton.setOnClickListener {
                openImageViewer(selectedImages)
            }
            createProductButton.setOnClickListener {
                val name = createRequestBody(createProductNameInput.text.toString())
                val category = createRequestBody(createProductCategoryInput.text.toString())
                val description = createRequestBody(createProductDescriptionInput.text.toString())
                val price = createRequestBody(createProductPriceInput.text.toString())
                val stock = createRequestBody(createProductStockInput.text.toString())
                val filledUris = selectedImages.filter { it != Uri.EMPTY }
                val photos = filledUris.map {
                    val file = uriToFile(it, this@CreateProductActivity).reduceFileImage()
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photos", file.name, requestImageFile)
                }

                viewModel.createProduct(photos, name, category, stock, price, description)
                    .observe(this@CreateProductActivity) {
                        when (it) {
                            is Resource.Loading -> {
                                showLoadingCreateProduct()
                            }

                            is Resource.Success -> {
                                stopLoadingCreateProduct()
                                val response = it.data
                                if (response != null) {
                                    binding.createProductButton.isEnabled = true
                                    val numberOfPasses =
                                        response.photos.filter { photo -> photo.status != "Blur" }.size
                                    if (response.status.equals("ACCEPTED")) {
                                        val message = if (numberOfPasses < response.photos.size)
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
                                stopLoadingCreateProduct()
                                showErrorDialog()
                            }

                            else -> {
                                stopLoadingCreateProduct()
                                showErrorDialog()
                            }

                        }
                    }

            }
        }
    }

    private fun showLoadingCreateProduct() {
        binding.createProductButton.isEnabled = false
        binding.createProductButton.text = "Creating product..."
    }

    private fun stopLoadingCreateProduct() {
        binding.createProductButton.isEnabled = true
        binding.createProductButton.text = "Create Product"
    }

    private fun showAcceptedDialog(message: String, product: GetProductsResponseItem) {
        MaterialAlertDialogBuilder(this@CreateProductActivity)
            .setCancelable(false)
            .setTitle("Success!")
            .setMessage(message)
            .setNeutralButton("Home") { dialog, _ ->
                moveToHomeScreen()
                dialog.dismiss()
            }
            .setNegativeButton("Profile") { dialog, _ ->
                moveToProfileScreen()
                dialog.dismiss()
            }
            .setPositiveButton("See My Product") { dialog, _ ->
                moveToProductDetailScreen(product)
                dialog.dismiss()
            }
            .show()
    }

    private fun showBannedDialog(numberOfPasses: Int, product: GetProductsResponseItem) {
        MaterialAlertDialogBuilder(this@CreateProductActivity)
            .setCancelable(false)
            .setTitle("Whoops!")
            .setMessage("After reviewing your product, we concluded that your product has not met the standard. A product must have at least 3 non-blurred photos, but you have uploaded $numberOfPasses non-blurred photos. Your product will not be displayed to customers, but you can still see it in your profile screen. You can update your product any time from the product detail screen.")
            .setNeutralButton("Home") { dialog, _ ->
                moveToHomeScreen()
                dialog.dismiss()
            }
            .setNegativeButton("See My Product") { dialog, _ ->
                moveToProductDetailScreen(product)
                dialog.dismiss()
            }
            .setPositiveButton("Update") { dialog, _ ->
                moveToEditProductScreen(product)
                dialog.dismiss()
            }
            .show()
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(this@CreateProductActivity)
            .setCancelable(false)
            .setTitle("Something went wrong")
            .setMessage("An error occurred while creating product.")
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

    private fun moveToEditProductScreen(product: GetProductsResponseItem) {
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra(EditProductActivity.EXTRA_PRODUCT, product)
        startActivity(intent)
    }

    private fun moveToProductDetailScreen(product: GetProductsResponseItem) {
        val moveIntent = Intent(this, ProductDetailActivity::class.java)
        moveIntent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product)
        moveIntent.putExtra(ProductDetailActivity.EXTRA_PREVIOUS_ACTIVITY, "create")
        startActivity(moveIntent)
    }

    private fun moveToProfileScreen() {
        val intent = Intent(this@CreateProductActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun createRequestBody(text: String): RequestBody {
        return text.trim().toRequestBody("text/plain".toMediaType())
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupImageCarousel() {
        val carouselView = binding.createProductImageCarousel
        adapter = CreateProductCarouselAdapter(selectedImages)

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselView)

        val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

        carouselView.layoutManager = carouselLayoutManager
        carouselView.adapter = adapter
        adapter?.setOnItemClickCallback(object : CreateProductCarouselAdapter.OnItemClickCallback {
            override fun onItemClicked(view: View) {
                startGallery()
            }

            override fun onDeleteClicked(view: View, itemPosition: Int) {
                selectedImages.removeAt(itemPosition)
                selectedImages.add(Uri.EMPTY)
                adapter?.updateData(itemPosition, selectedImages)
            }
        })
    }

    private fun openImageViewer(images: List<Uri>) {
        val filledUris = images.filter { it != Uri.EMPTY }
        StfalconImageViewer.Builder(this, filledUris) { view, image ->
            view.setImageURI(image)
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

    private fun obtainViewModel(activity: AppCompatActivity): CreateProductViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[CreateProductViewModel::class.java]
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}