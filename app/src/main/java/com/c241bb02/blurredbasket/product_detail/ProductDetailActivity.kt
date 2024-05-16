package com.c241bb02.blurredbasket.product_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.ActivityProductDetailBinding
import com.c241bb02.blurredbasket.utils.setupStatusBar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupStatusBar(window, this, R.color.white, true)
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