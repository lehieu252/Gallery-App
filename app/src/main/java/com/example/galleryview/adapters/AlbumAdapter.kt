package com.example.galleryview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.galleryview.R
import com.example.galleryview.models.Album
import com.google.android.material.imageview.ShapeableImageView

class AlbumAdapter(val context: Context) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    var data = mutableListOf<Album>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumHolder = itemView.findViewById<LinearLayout>(R.id.album_holder)
        val albumImage = itemView.findViewById<ShapeableImageView>(R.id.album_image)
        val albumName = itemView.findViewById<TextView>(R.id.album_name)
        val albumItemCount = itemView.findViewById<TextView>(R.id.albums_item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_album, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.albumName.text = item.name
        holder.albumItemCount.text = item.itemCount.toString()
        Glide.with(context).load(item.lastItemPath).placeholder(R.color.grey).centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(150)).into(holder.albumImage)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}