package com.example.flickrbrowserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ItemRowBinding

class RVadapter (val activity: MainActivity, private val photos: ArrayList<Images>): RecyclerView.Adapter<RVadapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photos[position]

        holder.binding.apply {
            ImageText.text = photo.title
            Glide.with(activity).load(photo.link).into(imageView2)
            llItemRow.setOnClickListener { activity.openImg(photo.link) }
        }
    }

    override fun getItemCount() = photos.size
}