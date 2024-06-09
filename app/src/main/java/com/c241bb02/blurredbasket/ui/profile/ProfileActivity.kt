package com.c241bb02.blurredbasket.ui.profile

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.databinding.ActivityProfileBinding
import com.c241bb02.blurredbasket.ui.home.HomeActivity
import com.c241bb02.blurredbasket.ui.home.ProductsListAdapter
import com.c241bb02.blurredbasket.ui.onboarding.OnboardingActivity
import com.c241bb02.blurredbasket.ui.product_detail.ProductDetailActivity
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setDefaultBackBehavior()
        setupStatusBar(window, this, R.color.blue_900, false)
        setupButtons()
        setupUserData()
        handleRoleBasedComponents()
        setupSellerProductsList()
    }

    private fun setDefaultBackBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            moveToHomeScreen()
        }
    }

    private fun moveToHomeScreen() {
        val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun setupButtons() {
        with(binding) {
            profileBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            logoutButton.setOnClickListener {
                val dialog = BottomSheetDialog(this@ProfileActivity, R.style.CustomBottomSheetDialog)
                val view = LayoutInflater.from(this@ProfileActivity).inflate(R.layout.logout_dialog, null)
                dialog.setContentView(view)
                setupLogoutDialog(dialog, view)
                dialog.show()
            }
        }
    }

    private fun setupUserData() {
        viewModel.getSession().observe(this) { user ->
            with(binding) {
                Glide.with(this@ProfileActivity)
                    .load(Uri.parse(BASE_PROFILE_PICTURE))
                    .into(userProfilePicture)
                userProfileRoleChip.text = user.role.lowercase().replaceFirstChar { it.uppercase() }
                userProfileUsername.text = user.username
                userProfileEmail.text = user.email
            }
        }
    }

    private fun handleRoleBasedComponents() {
        viewModel.getSession().observe(this) { user ->
            with(binding) {
                if (user.role == "CUSTOMER") {
                    sellerProductsHeader.visibility = View.GONE
                    sellerProductsList.visibility = View.GONE
                }
            }
        }
    }

    private fun setupLogoutDialog(dialog: BottomSheetDialog, view: View) {
        val logoutTriggerButton = view.findViewById<Button>(R.id.logout_trigger_button)
        val cancelLogoutButton = view.findViewById<Button>(R.id.cancel_logout_trigger_button)

        logoutTriggerButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    viewModel.logout()
                    moveToOnboardingScreen()
                    dialog.dismiss()
                    showToast("Logout successful!")
                    finish()
                } catch (e: Error) {
                    showToast("An error occurred while logging out. Please try again.")
                }
            }
        }

        cancelLogoutButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setupSellerProductsList() {
        viewModel.getSession().observe(this) { user ->
            if (user != null && user.role == "SELLER") {
                viewModel.getSellerProducts(user.id).observe(this) { products ->
                    if (products != null) {
                        val productsView = binding.sellerProductsList
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
        }
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

    private fun moveToOnboardingScreen() {
        val moveIntent = Intent(this, OnboardingActivity::class.java)
        startActivity(moveIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): ProfileViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ProfileViewModel::class.java]
    }

    companion object {
        const val BASE_PROFILE_PICTURE = "https://images.unsplash.com/photo-1605106702842-01a887a31122?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    }
}