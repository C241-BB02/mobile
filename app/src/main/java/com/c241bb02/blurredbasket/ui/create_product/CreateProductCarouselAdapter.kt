package com.c241bb02.blurredbasket.ui.create_product

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.CarouselListItemBinding

class CreateProductCarouselAdapter(private val imageList: ArrayList<Uri>): RecyclerView.Adapter<CreateProductCarouselAdapter.ViewHolder>() {
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

    fun updateData(startIndex: Int, newImageList: ArrayList<Uri>) {
        for (index in startIndex..<newImageList.size) {
            imageList[index] = newImageList[index]
        }
        notifyItemRangeChanged(startIndex, newImageList.size)
    }

    inner class ViewHolder(private val binding: CarouselListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Uri) {
            with(binding) {
                if (image == Uri.EMPTY) {
                    homeCarouselItemImage.setImageResource(R.drawable.ic_place_holder)
                    itemView.setOnClickListener {
                        onItemClickCallback.onItemClicked(it)
                    }
                } else {
                    homeCarouselItemImage.setImageURI(image)
                    itemView.setOnClickListener {
                        onItemClickCallback.onDeleteClicked(it, layoutPosition)
                    }
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(view: View)
        fun onDeleteClicked(view: View, itemPosition: Int)
    }
}