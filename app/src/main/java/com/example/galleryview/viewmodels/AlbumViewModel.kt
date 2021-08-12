package com.example.galleryview.viewmodels

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryview.models.Album
import com.example.galleryview.models.Item
import com.example.galleryview.utils.FileUtil
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class AlbumViewModel : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var _hideFunctionNav = MutableLiveData<Boolean>()
    val hideFunctionNav: LiveData<Boolean>
        get() = _hideFunctionNav

    private var _album = MutableLiveData<Album>()
    val album: LiveData<Album>
        get() = _album


    private var _itemListByAlbum = MutableLiveData<ArrayList<Item>>()
    val itemListByAlbum: LiveData<ArrayList<Item>>
        get() = _itemListByAlbum

    fun hideFunctionNavigation() {
        _hideFunctionNav.value = true;
    }

    fun showFunctionNavigation() {
        _hideFunctionNav.value = false;
    }


    private fun getItemsByAlbumName(context: Context, albumName: String): ArrayList<Item> {
        val list = ArrayList<Item>()
        val listImage = FileUtil.getImagesByAlbum(context, albumName)
        val listVideo = FileUtil.getVideosByAlbum(context, albumName)
        list.addAll(listImage)
        list.addAll(listVideo)
        list.sortByDescending { it.createdDate }
        viewModelScope.launch(Dispatchers.Main) {
            _album.value = withContext(Dispatchers.IO) {
                val album = Album(albumName)
                album.imagesCount = listImage.size
                album.videosCount = listVideo.size
                album
            }!!
        }
        return list
    }

    fun getItemsByAlbum(context: Context, albumName: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _itemListByAlbum.value = withContext(Dispatchers.IO) {
                getItemsByAlbumName(context, albumName)
            }!!
        }
    }


}