package com.example.galleryview.models

data class Item(var name: String?, var createdDate: Long, var path: String?, var duration: Int) {
    var isSelected = false
    var isHeader = false
    var isVideo = false
}