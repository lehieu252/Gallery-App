package com.example.galleryview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.galleryview.R
import com.example.galleryview.models.Item


class IndicatorAdapter(val context: Context, var urls: ArrayList<Item>, var currentSetter: ThumbIndicator.OnThumbClickListener) :
    PagerAdapter() {
    private var listItem: ArrayList<Item> = urls

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.thumb_item, container, false)
        v.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                currentSetter.onThumbClick(position)
            }
        })
        val imgSlider: ImageView = v.findViewById(R.id.thumb_image_slider)
        Glide.with(container.context).load(listItem[position].path).into(imgSlider)
        container.addView(v)
        return v
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return listItem.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (listItem.indexOf(`object`) == -1) POSITION_NONE else super.getItemPosition(`object`)
    }
}