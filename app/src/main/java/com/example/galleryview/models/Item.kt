package com.example.galleryview.models

import android.net.Uri

data class Item(
    var id: Long,
    var uri: Uri?,
    var albumName: String?,
    var createdDate: Long,
    var name: String?,
    var absolutePath: String?,
) {
    var isHeader = false
    var isVideo = false
    var position = -1
    var duration = 0
}