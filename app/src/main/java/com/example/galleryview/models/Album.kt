package com.example.galleryview.models

data class Album(val name: String) {
    var itemCount = 0;
    var lastItemPath: String? = null
}