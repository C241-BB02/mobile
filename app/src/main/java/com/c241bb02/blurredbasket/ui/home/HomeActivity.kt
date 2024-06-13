package com.c241bb02.blurredbasket.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.data.util.Resource
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
    private var backCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setupStatusBar(window, this, R.color.blue_50, true)
        setDefaultBackBehavior()
        setupBannerCarousel()
        handleRoleBasedComponents()
        observeProducts()
        setupBottomAppBar()
    }

    private fun setDefaultBackBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            backCount += 1
            if (backCount == 1) {
                showToast("Press back once more to exit Blurred Basket.")
            } else {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleRoleBasedComponents() {
        viewModel.getSession().observe(this) { user ->
            with(binding) {
                if (user.role == "CUSTOMER") {
                    createProductCta.visibility = View.GONE
                }
            }
        }
    }

    private fun setupBannerCarousel() {
        val banners = listOf(
            "https://res.cloudinary.com/dvvccpigs/image/upload/v1718273129/1_pk94ek.png",
            "https://res.cloudinary.com/dvvccpigs/image/upload/v1718272950/2_ayug5x.png",
            "https://res.cloudinary.com/dvvccpigs/image/upload/v1718272951/3_oa9vfc.png",
            )

        val carouselView = binding.homeCarouselRecyclerView
        val adapter = HomeCarouselAdapter(banners)

        val snapHelper = CarouselSnapHelper()
            carouselView.onFlingListener = null
        snapHelper.attachToRecyclerView(carouselView)

        val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

        carouselView.layoutManager = carouselLayoutManager
        carouselView.adapter = adapter
    }

    private fun observeProducts() {
        viewModel.getProducts().observe(this) {
            when (it) {
                is Resource.Loading -> {
                    startSkeletonLoader()
                }

                is Resource.Success -> {
                    stopSkeletonLoader()

                    val productsView = binding.homeProductsRecyclerView
                    val productsAdapter = ProductsListAdapter(it.data!!, showSellerProducts = false)

                    productsView.layoutManager = GridLayoutManager(this, 2)
                    productsView.adapter = productsAdapter
                    productsAdapter.setOnItemClickCallback(object :
                        ProductsListAdapter.OnItemClickCallback {
                        override fun onItemClicked(product: GetProductsResponseItem, view: View) {
                            moveToProductDetailScreen(product, view)
                        }
                    })
                }


                is Resource.Error -> {
                    stopSkeletonLoader()
                    showToast("An error occurred while getting products.")
                }

                else -> {
                    stopSkeletonLoader()
                    showToast("An error occurred while getting products.")
                }
            }
        }
    }

    private fun startSkeletonLoader() {
        binding.homeProductsRecyclerView.visibility = View.GONE
        binding.shimmerLayout.apply {
            visibility = View.VISIBLE
            startShimmer()
        }
    }

    private fun stopSkeletonLoader() {
        binding.homeProductsRecyclerView.visibility = View.VISIBLE
        binding.shimmerLayout.apply {
            visibility = View.GONE
            stopShimmer()
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
        moveIntent.putExtra(ProductDetailActivity.EXTRA_PREVIOUS_ACTIVITY, "home")
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