package com.example.galleryview.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.galleryview.R
import com.example.galleryview.models.Item
import java.text.SimpleDateFormat
import java.util.*

class StoryAdapter(val context: Context) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storyDate = itemView.findViewById<TextView>(R.id.story_date)
        val storyImage = itemView.findViewById<ImageView>(R.id.story_image)
    }

    var data = listOf<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_story, parent, false)
        return StoryAdapter.ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {
        val item = data[position]
        val date = Date(item.createdDate * 1000)
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        holder.storyDate.text = formatter.format(date)
        Glide.with(context).load(item.absolutePath).placeholder(R.color.grey).centerCrop()
            .into(holder.storyImage)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}