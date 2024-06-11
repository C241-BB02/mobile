package com.c241bb02.blurredbasket.ui.product_detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.databinding.ActivityProductDetailBinding
import com.c241bb02.blurredbasket.ui.edit_product.EditProductActivity
import com.c241bb02.blurredbasket.ui.home.HomeActivity
import com.c241bb02.blurredbasket.ui.profile.ProfileActivity
import com.c241bb02.blurredbasket.ui.profile.ProfileViewModel
import com.c241bb02.blurredbasket.ui.register.RegisterActivity
import com.c241bb02.blurredbasket.ui.utils.numberToRupiah
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setupStatusBar(window, this, R.color.white, true)
        setDefaultBackBehavior()
        handleRoleBasedButtons()
        setupButtons()
        setupImageCarousel()
        setupProductDetails()
    }

    private fun setDefaultBackBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            val previousActivity = intent.getStringExtra(EXTRA_PREVIOUS_ACTIVITY)
            if (previousActivity == "profile" || previousActivity == "create" || previousActivity == "edit") {
                moveToProfileScreen()
            } else {
                moveToHomeScreen()
            }
        }
    }

    private fun moveToHomeScreen() {
        val intent = Intent(this@ProductDetailActivity, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun setupImageCarousel(){
        val product = getProductParcelableExtra()

        if (product != null) {
            val images = product.photos.map { it.image }
            val carouselView = binding.productDetailImageCarousel
            val adapter = ProductDetailCarouselAdapter(images)

            val snapHelper = CarouselSnapHelper()
            snapHelper.attachToRecyclerView(carouselView)
            val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
            carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

            carouselView.layoutManager = carouselLayoutManager
            carouselView.adapter = adapter
            adapter.setOnItemClickCallback(object: ProductDetailCarouselAdapter.OnItemClickCallback {
                override fun onItemClicked(view: View, position: Int) {
                    openImageViewer(images, position)
                }
            })
        }
    }

    private fun setupProductDetails() {
        val product = getProductParcelableExtra()
        if (product != null) {
            with(binding) {
                Glide.with(this@ProductDetailActivity)
                    .load(Uri.parse(ProfileActivity.BASE_PROFILE_PICTURE))
                    .into(productDetailSellerProfilePicture)
                productDetailName.text = product.name
                productDetailSellerUsername.text = product.user?.username
                productDescriptionText.text = product.description
                productDetailProductPrice.text = product.price?.let { numberToRupiah(it) }
            }
        }
    }

    private fun getProductParcelableExtra(): GetProductsResponseItem? {
        val product = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_PRODUCT, GetProductsResponseItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PRODUCT)
        }

        return product
    }

    private fun setupButtons() {
        with(binding) {
            productDetailBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            productDetailEditButton.setOnClickListener {
                moveToEditProductScreen()
            }
            productDetailDeleteButton.setOnClickListener {
                val dialog = BottomSheetDialog(this@ProductDetailActivity, R.style.CustomBottomSheetDialog)
                val view = LayoutInflater.from(this@ProductDetailActivity).inflate(R.layout.delete_product_dialog, null)
                dialog.setContentView(view)
                setupDeleteProductDialog(dialog, view)
                dialog.show()
            }
        }
    }

    private fun handleRoleBasedButtons() {
        val product = getProductParcelableExtra()

        viewModel.getSession().observe(this) { user ->
            with(binding) {
                if (user.role == "CUSTOMER" || product?.user?.id != user.id) {
                    productDetailEditButton.visibility = View.GONE
                    productDetailDeleteButton.visibility = View.GONE
                }
            }
        }
    }

    private fun openImageViewer(images: List<String>, position: Int) {
        StfalconImageViewer.Builder(this, images) { view, image ->
            Glide.with(view.context)
                .load(Uri.parse(image))
                .into(view)
        }
            .withBackgroundColor(ContextCompat.getColor(this, R.color.blue_100))
            .withStartPosition(position)
            .show()
    }

    private fun setupDeleteProductDialog(dialog: BottomSheetDialog, view: View) {
        val product = getProductParcelableExtra()

        if (product != null) {
            val deleteTriggerButton = view.findViewById<Button>(R.id.delete_trigger_button)
            val cancelDeleteTriggerButton = view.findViewById<Button>(R.id.cancel_delete_trigger_button)

            deleteTriggerButton.setOnClickListener {
                try {
                    lifecycleScope.launch {
                        viewModel.deleteProduct(product.code ?: "")
                        showToast("Product successfully deleted.")
                        moveToProfileScreen()
                        dialog.dismiss()
                    }
                } catch (e: HttpException) {
                    showToast("An error occurred while deleting the product. Please try again.")
                }

            }
            cancelDeleteTriggerButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun moveToEditProductScreen() {
        val product = getProductParcelableExtra()
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra(EditProductActivity.EXTRA_PRODUCT, product)
        startActivity(intent)
    }

    private fun moveToProfileScreen() {
        val moveIntent = Intent(this, ProfileActivity::class.java)
        startActivity(moveIntent)
    }

    private fun setupTransition() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 500L

        }

        window.sharedElementReturnTransition  = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 400L
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): ProductDetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ProductDetailViewModel::class.java]
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
        const val EXTRA_PREVIOUS_ACTIVITY = "extra_previous_activity"
    }
}