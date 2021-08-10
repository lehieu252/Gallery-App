package com.example.galleryview.adapters

import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.util.set
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.galleryview.R
import com.example.galleryview.models.Item
import com.example.galleryview.views.AlbumViewFragment
import com.example.galleryview.views.PictureFragment

class ItemAdapter(val context: Context, val type: Int) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header_holder = itemView.findViewById<LinearLayout>(R.id.header_holder)
        var item_holder = itemView.findViewById<RelativeLayout>(R.id.item_holder)
        var header = itemView.findViewById<TextView>(R.id.header_text)
        var item_image = itemView.findViewById<ImageView>(R.id.item_image)
        var video_tag = itemView.findViewById<LinearLayout>(R.id.video_tag)
        var video_duration = itemView.findViewById<TextView>(R.id.video_duration)
        var itemCheckBox = itemView.findViewById<CheckBox>(R.id.item_checkbox)
    }

    var data = listOf<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var albumName: String = ""
    var isSelectedMode = false
    var selectedList = SparseBooleanArray(data.size)


    private lateinit var itemClick: OnItemClick

    fun setItemClick(onItemClick: OnItemClick) {
        this.itemClick = onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val item = data[position]
        if (isHeader(position)) {
            holder.header_holder.visibility = View.VISIBLE
            holder.item_holder.visibility = View.GONE
            holder.header.text = item.albumName
        } else {
            if (isSelectedMode) {
                holder.itemCheckBox.visibility = View.VISIBLE
            } else {
                holder.itemCheckBox.isChecked = false
                holder.itemCheckBox.visibility = View.GONE
            }
            holder.header_holder.visibility = View.GONE
            holder.item_holder.visibility = View.VISIBLE
            if (item.isVideo) {
                holder.video_tag.visibility = View.VISIBLE
                val minute = item.duration / (1000 * 60)
                val second = (item.duration / 1000) % 60
                var duration = if (second < 10) {
                    "${minute}:0${second}"
                } else {
                    "${minute}:${second}"
                }
                holder.video_duration.text = duration
            } else {
                holder.video_tag.visibility = View.GONE
            }
            Glide.with(context).load(item.absolutePath).placeholder(R.color.grey).centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(100)).into(holder.item_image)

            holder.item_holder.setOnClickListener {
                if (isSelectedMode) {
                    if (holder.itemCheckBox.isChecked) {
                        holder.itemCheckBox.isChecked = false
                        selectedList[position] = false
                    } else {
                        holder.itemCheckBox.isChecked = true
                        selectedList[position] = true
                    }
                    itemClick.onItemClick(it, position, item)
                } else {
                    val bundle: Bundle = Bundle()
                    if (type == PictureFragment.TYPE_PICTURE_FRAGMENT) {
                        bundle.putInt("position", item.position)
                        bundle.putInt("type", type)
                        it.findNavController()
                            .navigate(R.id.action_pictureFragment_to_viewFragment, bundle)
                    } else if (type == AlbumViewFragment.TYPE_ALBUM_FRAGMENT) {
                        bundle.putInt("position", position)
                        bundle.putInt("type", type)
                        bundle.putString("album_name", albumName)
                        it.findNavController()
                            .navigate(R.id.action_albumViewFragment_to_viewFragment, bundle)
                    }
                }
            }

            holder.item_holder.setOnLongClickListener {
                itemClick.onItemLongClick(it, position, item)
                if (!isSelectedMode) {
                    toSelectedMode()
                    true
                } else false
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun isHeader(position: Int): Boolean {
        return data[position].isHeader
    }

    override fun getItemViewType(position: Int): Int {
//        if (isHeader(position)) return TYPE_HEADER
//        else return TYPE_ITEM
        return position
    }

    fun toSelectedMode() {
        isSelectedMode = true;
        this.notifyDataSetChanged()
    }
}