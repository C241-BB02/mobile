package com.c241bb02.blurredbasket.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.databinding.ActivityHomeBinding
import com.c241bb02.blurredbasket.ui.create_product.CreateProductActivity
import com.c241bb02.blurredbasket.ui.product_detail.ProductDetailActivity
import com.c241bb02.blurredbasket.ui.profile.ProfileActivity
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setupStatusBar(window, this, R.color.blue_50, true)
        setupBannerCarousel()
        observeProducts()
        setupBottomAppBar()
    }

    override fun onResume() {
        super.onResume()
        observeProducts()
    }

    private fun setupBannerCarousel() {
        val arrayList = listOf(
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1692528131755-d4e366b2adf0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzNXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
        )

        val carouselView = binding.homeCarouselRecyclerView
        val adapter = HomeCarouselAdapter(arrayList)

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselView)

        val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

        carouselView.layoutManager = carouselLayoutManager
        carouselView.adapter = adapter
    }

    private fun observeProducts() {
        viewModel.getProducts().observe(this) { products ->
            if (products != null) {
                val productsView = binding.homeProductsRecyclerView
                val productsAdapter = ProductsListAdapter(products)

                productsView.layoutManager = GridLayoutManager(this, 2)
                productsView.adapter = productsAdapter
                productsAdapter.setOnItemClickCallback(object :
                    ProductsListAdapter.OnItemClickCallback {
                    override fun onItemClicked(product: GetProductsResponseItem, view: View) {
                        moveToProductDetailScreen(product, view)
                    }
                })
            }
        }

    }

    private fun setupBottomAppBar() {
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.profile_icon -> {
                    moveToProfileScreen()
                    true
                }

                else -> false
            }
        }

        binding.createProductCta.setOnClickListener {
            moveToCreateProductScreen(it)
        }
    }

    private fun setupTransition() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
    }

    private fun moveToCreateProductScreen(view: View) {
        val moveIntent = Intent(this, CreateProductActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            view,
            "create_product_transition"
        )
        startActivity(moveIntent, options.toBundle())
    }

    private fun moveToProfileScreen() {
        val moveIntent = Intent(this, ProfileActivity::class.java)
        startActivity(moveIntent)
    }

    private fun moveToProductDetailScreen(product: GetProductsResponseItem, view: View) {
        val moveIntent = Intent(this, ProductDetailActivity::class.java)
        moveIntent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            view,
            "shared_element_container"
        )

        startActivity(moveIntent, options.toBundle())
    }

    private fun obtainViewModel(activity: AppCompatActivity): HomeViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HomeViewModel::class.java]
    }
}