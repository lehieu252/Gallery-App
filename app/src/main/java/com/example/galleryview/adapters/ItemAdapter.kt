package com.example.galleryview.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.galleryview.R
import com.example.galleryview.models.Item

class ItemAdapter(val context: Context) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header_holder = itemView.findViewById<LinearLayout>(R.id.header_holder)
        var item_holder = itemView.findViewById<RelativeLayout>(R.id.item_holder)
        var header = itemView.findViewById<TextView>(R.id.header_text)
        var item_image = itemView.findViewById<ImageView>(R.id.item_image)
        var video_tag = itemView.findViewById<LinearLayout>(R.id.video_tag)
        var video_duration = itemView.findViewById<TextView>(R.id.video_duration)
    }

    companion object {
        const val TYPE_ITEM = 0;
        const val TYPE_HEADER = 1;
    }

    var data = listOf<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
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
            holder.header.text = item.name
        } else {
            holder.header_holder.visibility = View.GONE
            holder.item_holder.visibility = View.VISIBLE
            if (item.isVideo) {
                holder.video_tag.visibility = View.VISIBLE
                val minute = item.duration / (1000 * 60)
                val second = (item.duration / 1000) % 60
                var duration = ""
                duration = if (second < 10) {
                    "${minute}:0${second}"
                } else {
                    "${minute}:${second}"
                }
                holder.video_duration.text = duration
            } else {
                holder.video_tag.visibility = View.GONE
            }
            Glide.with(context).load(item.path).placeholder(R.color.grey).centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(250)).into(holder.item_image)
            holder.item_holder.setOnClickListener {
                val bundle: Bundle = Bundle()
                bundle.putInt("position", item.position)
                it.findNavController().navigate(R.id.action_pictureFragment_to_viewFragment, bundle)
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
}