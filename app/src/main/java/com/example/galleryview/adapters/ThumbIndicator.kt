package com.example.galleryview.adapters

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.Nullable
import androidx.viewpager.widget.ViewPager
import com.example.galleryview.models.Item
import java.util.*


class ThumbIndicator : ViewPager {
    private lateinit var mUrls: ArrayList<Item>
    var mAdp: IndicatorAdapter? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {}

    fun setupWithViewPager(
        mMainViewPager: ViewPager,
        urls: ArrayList<Item>,
        thumbSizeInDp: Float
    ) {
        mUrls = urls
        prepare(mMainViewPager, thumbSizeInDp)
    }

    fun prepare(vp: ViewPager, size: Float) {
        val callBack: OnThumbClickListener = object : OnThumbClickListener {
            override fun onThumbClick(pos: Int) {
                vp.currentItem = pos
                currentItem = pos
            }
        }
        mAdp = IndicatorAdapter(context, mUrls, callBack)
        adapter = mAdp
        pageMargin = dpToPixel(4f)
        vp.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (mUrls.size > 1) {
                    currentItem = i
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (mUrls!!.size > 1) vp.currentItem = i
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
        clipToPadding = false
        val padding = resources.displayMetrics.widthPixels / 2 - dpToPixel(size) / 2
        setPadding(padding, 0, padding, 0)
    }

    fun notifyDataSetChanged() {
        mAdp!!.notifyDataSetChanged()
    }

    private fun dpToPixel(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
            .toInt()
    }

    interface OnThumbClickListener {
        fun onThumbClick(pos: Int)
    }
}