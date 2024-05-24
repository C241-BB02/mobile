package com.c241bb02.blurredbasket.ui.create_product

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.ActivityCreateProductBinding
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.stfalcon.imageviewer.StfalconImageViewer


class CreateProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateProductBinding
    private var selectedImages: ArrayList<Uri> = arrayListOf(
        Uri.EMPTY,
        Uri.EMPTY,
        Uri.EMPTY,
        Uri.EMPTY,
        Uri.EMPTY,
    )
    private var adapter: CreateProductCarouselAdapter? = null
    private val MAX_IMAGES = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransition()

        super.onCreate(savedInstanceState)
        binding = ActivityCreateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar(window, this, R.color.blue_50, true)
        setupImageCarousel()
        setupButtons()
    }

    private fun setupButtons() {
        with(binding) {
            createProductPreviewImageButton.setOnClickListener {
                openImageViewer(selectedImages)
            }
            createProductButton.setOnClickListener {

            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupImageCarousel(){
        val carouselView = binding.createProductImageCarousel
        adapter = CreateProductCarouselAdapter(selectedImages)

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselView)

        val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carouselLayoutManager.carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER

        carouselView.layoutManager = carouselLayoutManager
        carouselView.adapter = adapter
        adapter?.setOnItemClickCallback(object: CreateProductCarouselAdapter.OnItemClickCallback {
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
        val filledUris = images.filter {it != Uri.EMPTY}
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

        window.sharedElementReturnTransition  = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 400L
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGES)
    ) {  uris ->
        val filledUris = selectedImages.filter { it != Uri.EMPTY }
        if(uris.size + filledUris.size > MAX_IMAGES) {
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
}