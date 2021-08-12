package com.example.galleryview.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.galleryview.R
import com.example.galleryview.models.Item

class ViewPAdapter(val context: Activity) : RecyclerView.Adapter<ViewPAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSlider = itemView.findViewById<ImageView>(R.id.image_slider)
        val videoSlider = itemView.findViewById<VideoView>(R.id.video_slider)
    }

    var data = ArrayList<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val view = context.layoutInflater.inflate(R.layout.item_view, container, false)
//        val imgSlider = view.findViewById<ImageView>(R.id.image_slider)
//        val videoSlider = view.findViewById<VideoView>(R.id.video_slider)
//        if(data[position].isVideo){
//            imgSlider.visibility = View.GONE
//            videoSlider.visibility = View.VISIBLE
//            videoSlider.setVideoURI(data[position].uri)
//            videoSlider.start()
////            videoSlider.setOnPreparedListener {
////                it.start()
////                it.isLooping = false
////            }
//        }
//        else {
//            imgSlider.visibility = View.VISIBLE
//            videoSlider.visibility = View.GONE
//            Glide.with(container.context).load(data[position].absolutePath).into(imgSlider)
//            container.addView(view)
//        }
//        return view
//    }
//
//    override fun getItemPosition(`object`: Any): Int {
//        return if (data.indexOf(`object`) == -1) {
//            POSITION_NONE
//        } else {
//            super.getItemPosition(`object`)
//        }
//    }
//
//    override fun getCount(): Int {
//        return data.size;
//    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view == `object`
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        if (data[position].isHeader) {
//            container.removeView(`object` as View)
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(data[position].isVideo){
            holder.imgSlider.visibility = View.GONE
            holder.videoSlider.visibility = View.VISIBLE
            holder.videoSlider.setVideoURI(data[position].uri)
            holder.videoSlider.setOnPreparedListener {
                it.start()
                it.isLooping = true
            }
//            videoSlider.setOnPreparedListener {
//                it.start()
//                it.isLooping = false
//            }
        }
        else {
            holder.imgSlider.visibility = View.VISIBLE
            holder.videoSlider.visibility = View.GONE
            Glide.with(context).load(data[position].absolutePath).into(holder.imgSlider)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
