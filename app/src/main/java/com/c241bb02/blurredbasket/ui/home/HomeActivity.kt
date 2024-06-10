package com.c241bb02.blurredbasket.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
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
                finish()
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
            "https://storage.googleapis.com/c241-bb02/photos/Blurred.png?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=django-bucket%40c241-bb02.iam.gserviceaccount.com%2F20240610%2Fauto%2Fstorage%2Fgoog4_request&X-Goog-Date=20240610T164925Z&X-Goog-Expires=86400&X-Goog-SignedHeaders=host&X-Goog-Signature=555968757fcde63cdf7d5118a57f7ee9422af78fd04d41601ea490dc21b03525fe5a774df8b6b80671ed19737083fb82fbc902f766f586384e2f00810694f5ba99ee7a6fed3291eaa17e89ecc8624a0b8fac1e85ffb605ee68a90ba893509ddbf5d3330e25c9025035d41ea852233a021d6f792ee44494df9ac1a7862308cc64d388850bab856e1882909d54b8e9178e6b7fe83f01d15c7d9818b2bf8623cbd7bad77252cfebef1b4e037c5be3e6567feda4f0011557426fe23b338ba364d6edfc9d7a8fa3df3e5505f31ee4dbd81e65c07581c6dadb760d54778bf4f5775099db8063a034fe60bf272da2b693aa199e3d0b6d85a49ac5286f1497813496db50",
            "https://storage.googleapis.com/c241-bb02/photos/2.png?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=django-bucket%40c241-bb02.iam.gserviceaccount.com%2F20240610%2Fauto%2Fstorage%2Fgoog4_request&X-Goog-Date=20240610T164725Z&X-Goog-Expires=86400&X-Goog-SignedHeaders=host&X-Goog-Signature=84b1ea3564f6537569d51f861b2b3af53a1b76a91b8a143d0ec46db4106757d77c969305485014e36ca72a11201cc627aefbf517271af075193827460af3c986decb6f4f9e0197431921b987486d16397bc9a4a8e9b457f5ff978ceafb37db77992bce19bd74a4928a8b137bac89c1cb2348fd2014119abd71934371bb8e7328d65b2e55f916236e67f54eca1be7f5face9fb4b5a5934730f6c23a47099480d70e44a8451ae4bb8c1d79ecaf3b4000beb9b611414ae96d6d9cab594c9717c2f8f0c33aadc62f7fbaf3071ac9a2282a3205d1dd6e4d358e2b072190866c9258f6bc0107daddc571fe5b6b625a79a6d84c0ca540b4ee7441de81495565ba6cd8bf",
            "https://storage.googleapis.com/c241-bb02/photos/3.png?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=django-bucket%40c241-bb02.iam.gserviceaccount.com%2F20240610%2Fauto%2Fstorage%2Fgoog4_request&X-Goog-Date=20240610T164725Z&X-Goog-Expires=86400&X-Goog-SignedHeaders=host&X-Goog-Signature=13818a45016c3cd0902c264cdde5bc6ea4ef919fb40b6f584e5eec92de701fab51fa42259ad73d3279fa7c5c8962f8f1d99674455086ec752f8f752558352c08102c200219cdc7ad5d30d4e3f00f905cdcc76323213d6f5e4ad56183ca5c44c1f654ed15dba64427827b4d5e22b2a0861d5aefdc5c88e95cefce44e26aa979e6834f3e23b99281ad89c4394129423a41fae7eaef90f93053d83335ad888edfde2b1e990283677e6332defa40d2443e2811e347713dcd643bbccee41f5ff30e42eaac941c65b381af7006b0727e22e62c711be8a33463eded99d4042f490d9cfe9ca88f164685f8a636042c7ff1b519d336a1694ffe80769682a2a768611ed26c",
        )

        val carouselView = binding.homeCarouselRecyclerView
        val adapter = HomeCarouselAdapter(banners)

        val snapHelper = CarouselSnapHelper()
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
                    binding.homeProductsRecyclerView.visibility = View.VISIBLE

                    val productsView = binding.homeProductsRecyclerView
                    val productsAdapter = ProductsListAdapter(it.data!!)

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
                }

                else -> {
                    stopSkeletonLoader()
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