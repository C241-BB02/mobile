package com.c241bb02.blurredbasket.ui.home

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.databinding.HomeProductsListItemBinding
import com.c241bb02.blurredbasket.ui.utils.numberToRupiah
import java.text.NumberFormat
import java.util.Currency

class ProductsListAdapter(
    private val list: List<GetProductsResponseItem>,
    private val showSellerProducts: Boolean
) : RecyclerView.Adapter<ProductsListAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomeProductsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: HomeProductsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(product: GetProductsResponseItem) {
            with(binding) {
                homeProductsItemName.text = product.name
                homeProductsItemPrice.text = product.price?.let { numberToRupiah(it) }

                if (showSellerProducts) {
                    Glide.with(itemView.context)
                        .load(Uri.parse(product.photos[0].image))
                        .into(homeProductsItemImage)

                    homeProductsItemSellerName.visibility = View.GONE
                    sellerProductStatusChip.visibility = View.VISIBLE
                    sellerProductStatusChip.text = product.status.lowercase().replaceFirstChar { it.uppercase() }
                    val chipColor = if (product.status == "ACCEPTED") R.color.green else R.color.red
                    sellerProductStatusChip.setChipBackgroundColorResource(chipColor)
                } else {
                    val image = product.photos.find { it.status != "Blur" }?.image
                    Glide.with(itemView.context)
                        .load(Uri.parse(image))
                        .into(homeProductsItemImage)

                    homeProductsItemSellerName.text = product.user?.username
                }

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(product, it)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(product: GetProductsResponseItem, view: View)
    }
}