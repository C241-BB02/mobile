package com.c241bb02.blurredbasket.product_detail

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.adapters.ProductsListAdapter
import com.c241bb02.blurredbasket.databinding.ActivityProductDetailBinding
import com.c241bb02.blurredbasket.home.HomeCarouselAdapter
import com.c241bb02.blurredbasket.utils.setupStatusBar
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
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

    private fun openImageViewer(images: List<String>) {
        StfalconImageViewer.Builder(this, images) { view, image ->
            Glide.with(view.context)
                .load(Uri.parse(image))
                .into(view)
        }
            .withBackgroundColor(ContextCompat.getColor(this, R.color.blue_100))
            .show()
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