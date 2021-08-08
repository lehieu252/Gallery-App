package com.example.galleryview.models

import androidx.lifecycle.MutableLiveData

data class Album(val name: String) {
    var itemCount = 0;
    var lastItemPath: String? = null
    var imagesCount = 0;
    var videosCount = 0;
}