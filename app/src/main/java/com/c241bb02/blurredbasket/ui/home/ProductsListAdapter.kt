package com.c241bb02.blurredbasket.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.api.product.GetProductsResponseItem
import com.c241bb02.blurredbasket.databinding.HomeProductsListItemBinding
import com.c241bb02.blurredbasket.ui.utils.numberToRupiah
import java.text.NumberFormat
import java.util.Currency

class ProductsListAdapter(private val list: List<GetProductsResponseItem>): RecyclerView.Adapter<ProductsListAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeProductsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: HomeProductsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: GetProductsResponseItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(Uri.parse(product.photos[0].image))
                    .into(homeProductsItemImage)

                homeProductsItemName.text = product.name
                homeProductsItemSellerName.text = product.user?.username
                homeProductsItemPrice.text = product.price?.let { numberToRupiah(it) }

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