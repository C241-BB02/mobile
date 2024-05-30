package com.c241bb02.blurredbasket.ui.product_detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.ActivityProductDetailBinding
import com.c241bb02.blurredbasket.ui.edit_product.EditProductActivity
import com.c241bb02.blurredbasket.ui.profile.ProfileActivity
import com.c241bb02.blurredbasket.ui.register.RegisterActivity
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.stfalcon.imageviewer.StfalconImageViewer

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar(window, this, R.color.white, true)
        setupButtons()
        setupImageCarousel()
    }

    private fun setupImageCarousel(){
        val images = listOf(
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            )

        val carouselView = binding.productDetailImageCarousel
        val adapter = ProductDetailCarouselAdapter(images)

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselView)

        val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

        carouselView.layoutManager = carouselLayoutManager
        carouselView.adapter = adapter
        adapter.setOnItemClickCallback(object: ProductDetailCarouselAdapter.OnItemClickCallback {
            override fun onItemClicked(view: View) {
                openImageViewer(images)
            }
        })
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

    private fun openImageViewer(images: List<String>) {
        StfalconImageViewer.Builder(this, images) { view, image ->
            Glide.with(view.context)
                .load(Uri.parse(image))
                .into(view)
        }
            .withBackgroundColor(ContextCompat.getColor(this, R.color.blue_100))
            .show()
    }

    private fun setupDeleteProductDialog(dialog: BottomSheetDialog, view: View) {
        val deleteTriggerButton = view.findViewById<Button>(R.id.delete_trigger_button)
        val cancelDeleteTriggerButton = view.findViewById<Button>(R.id.cancel_delete_trigger_button)

        deleteTriggerButton.setOnClickListener {
            // TODO: hit backend
            moveToProfileScreen()
            dialog.dismiss()
        }
        cancelDeleteTriggerButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun moveToEditProductScreen() {
        val intent = Intent(this, EditProductActivity::class.java)
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
}