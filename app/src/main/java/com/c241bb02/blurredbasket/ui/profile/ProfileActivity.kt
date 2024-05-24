package com.c241bb02.blurredbasket.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.Product
import com.c241bb02.blurredbasket.databinding.ActivityProfileBinding
import com.c241bb02.blurredbasket.ui.home.ProductsListAdapter
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar(window, this, R.color.blue_900, false)
        setupButtons()
        setupSellerProductsList()
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


    private fun setupLogoutDialog(dialog: BottomSheetDialog, view: View) {
        val logoutTriggerButton = view.findViewById<Button>(R.id.logout_trigger_button)
        val cancelLogoutButton = view.findViewById<Button>(R.id.cancel_logout_trigger_button)

        logoutTriggerButton.setOnClickListener {
            // TODO: hit backend
        }

        cancelLogoutButton.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun setupSellerProductsList() {
        val arrayList2 = ArrayList<Product>()

        arrayList2.add(Product(id="1", name = "Sayur", description = "asdkadsk", image = "https://images.unsplash.com/photo-1692862582645-3b6fd47b7513?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw0MXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        arrayList2.add(Product(id="2",name = "Sayur", description = "asdkadsk", image = "https://images.unsplash.com/photo-1692862582645-3b6fd47b7513?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw0MXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        arrayList2.add(Product(id="3",name = "Sayur", description = "asdkadsk", image = "https://images.unsplash.com/photo-1692584927805-d4096552a5ba?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw0Nnx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        arrayList2.add(Product(id="4",name = "Sayur", description = "asdkadsk", image = "https://images.unsplash.com/photo-1692584927805-d4096552a5ba?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw0Nnx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        arrayList2.add(Product(id="5",name = "Sayur", description = "asdkadsk", image = "https://images.unsplash.com/photo-1692854236272-cc49076a2629?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw1MXx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))

        val productsView = binding.sellerProductsList
        val productsAdapter = ProductsListAdapter(arrayList2)

        productsView.layoutManager = GridLayoutManager(this, 2)
        productsView.adapter = productsAdapter
    }
}