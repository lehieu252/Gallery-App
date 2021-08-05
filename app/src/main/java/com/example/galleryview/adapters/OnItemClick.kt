package com.example.galleryview.adapters

import android.view.View

interface OnItemClick {
    fun onItemClick(view: View, position: Int)
    fun onItemLongClick(view: View, position: Int)
}