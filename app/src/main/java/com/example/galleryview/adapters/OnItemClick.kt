package com.example.galleryview.adapters

import android.view.View
import com.example.galleryview.models.Item

interface OnItemClick {
    fun onItemClick(view: View, position: Int, item: Item)
    fun onItemLongClick(view: View, position: Int, item : Item)
}