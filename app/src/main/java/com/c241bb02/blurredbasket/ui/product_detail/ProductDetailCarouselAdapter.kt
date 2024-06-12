package com.c241bb02.blurredbasket.ui.product_detail

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.product.PhotosItem
import com.c241bb02.blurredbasket.databinding.CarouselListItemBinding

class ProductDetailCarouselAdapter(
    private val imageList: List<PhotosItem>,
    private val isOwnerOfProduct: Boolean
) : RecyclerView.Adapter<ProductDetailCarouselAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CarouselListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(private val binding: CarouselListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(photo: PhotosItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(Uri.parse(photo.image))
                    .into(homeCarouselItemImage)

                if (isOwnerOfProduct) {
                    imageStatusChip.visibility = View.VISIBLE
                    imageStatusChip.text = photo.status
                    if (photo.status == "Blur") {
                        imageStatusChip.setChipBackgroundColorResource(R.color.red)
                    } else {
                        imageStatusChip.setChipBackgroundColorResource(R.color.green)
                    }
                }

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(it, layoutPosition)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(view: View, position: Int)
    }
}