package com.example.galleryview.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.galleryview.R
import com.example.galleryview.models.Item

class ViewPAdapter(val context: Activity) : PagerAdapter() {

    var data = ArrayList<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }



    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = context.layoutInflater.inflate(R.layout.item_view, container, false)
        val imgSlider = view.findViewById<ImageView>(R.id.image_slider)
        if (!data[position].isHeader) {
            Glide.with(container.context).load(data[position].absolutePath).into(imgSlider)
            container.addView(view)
        }
        return view
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (data.indexOf(`object`) == -1) {
            POSITION_NONE
        } else {
            super.getItemPosition(`object`)
        }
    }

    override fun getCount(): Int {
        return data.size;
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (data[position].isHeader) {
            container.removeView(`object` as View)
        }
    }

}
