package com.c241bb02.blurredbasket.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c241bb02.blurredbasket.databinding.HomeCarouselListItemBinding

class HomeCarouselAdapter(private val imageList: List<String>): RecyclerView.Adapter<HomeCarouselAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeCarouselListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }


    inner class ViewHolder(private val binding: HomeCarouselListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(Uri.parse(image))
                    .into(homeCarouselItemImage)
            }
        }
    }
}