package com.c241bb02.blurredbasket.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.api.Product
import com.c241bb02.blurredbasket.databinding.HomeProductsListItemBinding

class ProductsListAdapter(private val list: List<Product>): RecyclerView.Adapter<ProductsListAdapter.ViewHolder>() {
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
        fun bind(product: Product) {
            val (id, name, description, image) = product
            with(binding) {
                Glide.with(itemView.context)
                    .load(Uri.parse(image))
                    .into(homeProductsItemImage)

                homeProductsItemName.text = name
                homeProductsItemDescription.text = description

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(it)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(view: View)
    }
}