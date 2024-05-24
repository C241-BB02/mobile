package com.c241bb02.blurredbasket.ui.product_detail

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.databinding.CarouselListItemBinding

class ProductDetailCarouselAdapter(private val imageList: List<String>): RecyclerView.Adapter<ProductDetailCarouselAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CarouselListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(private val binding: CarouselListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(Uri.parse(image))
                    .into(homeCarouselItemImage)

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