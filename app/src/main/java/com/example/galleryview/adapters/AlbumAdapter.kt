package com.example.galleryview.adapters

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.galleryview.R
import com.example.galleryview.models.Album
import com.google.android.material.imageview.ShapeableImageView

class AlbumAdapter(val context: Context, val type: Int) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumHolder = itemView.findViewById<RelativeLayout>(R.id.album_holder)
        val albumImage = itemView.findViewById<ShapeableImageView>(R.id.album_image)
        val albumName = itemView.findViewById<TextView>(R.id.album_name)
        val albumItemCount = itemView.findViewById<TextView>(R.id.albums_item_count)
        val albumCheckBox = itemView.findViewById<CheckBox>(R.id.album_checkbox)
    }

    var data = mutableListOf<Album>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isSelectedMode = false
    var selectedList = SparseBooleanArray(data.size)

    private lateinit var itemClick: ItemClick
    fun setItemClick(itemClick: ItemClick) {
        this.itemClick = itemClick
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_album, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if (!isSelectedMode) {
            holder.albumCheckBox.visibility = View.GONE
            holder.albumName.text = item.name
            holder.albumItemCount.text = item.itemCount.toString()
            Glide.with(context).load(item.lastItemPath).placeholder(R.color.grey).centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(150)).into(holder.albumImage)
        } else {
            holder.albumCheckBox.visibility = View.VISIBLE
            holder.albumCheckBox.isChecked = false
        }
        holder.albumHolder.setOnClickListener {
            if (isSelectedMode) {
                if (holder.albumCheckBox.isChecked) {
                    holder.albumCheckBox.isChecked = false
                    selectedList[position] = false
                } else {
                    holder.albumCheckBox.isChecked = true
                    selectedList[position] = true
                }
                itemClick.onItemClick(it, position, item)
            }
            else{
                itemClick.onItemClick(it, position, item)
            }
        }
        holder.albumHolder.setOnLongClickListener {
            itemClick.onItemLongClick(it, position, item)
            true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun toSelectedMode() {
        isSelectedMode = true;
        this.notifyDataSetChanged()
    }

    interface ItemClick {
        fun onItemClick(view: View, position: Int, album: Album)
        fun onItemLongClick(view: View, position: Int, album: Album)
    }
}