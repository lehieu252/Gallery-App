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

class AlbumViewModel(val albumName: String) : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var _itemList = MutableLiveData<ArrayList<Item>>()
    val itemList: LiveData<ArrayList<Item>>
        get() = _itemList


    private var _album = MutableLiveData<Album>()
    val album: LiveData<Album>
        get() = _album

    private var _hideFunctionNav = MutableLiveData<Boolean>()
    val hideFunctionNav: LiveData<Boolean>
        get() = _hideFunctionNav

    fun hideFunctionNavigation() {
        _hideFunctionNav.value = true;
    }

    fun showFunctionNavigation() {
        _hideFunctionNav.value = false;
    }

    var selectedList = ArrayList<Item>()

    private fun getItemsByAlbumName(context: Context): ArrayList<Item> {
        val list = ArrayList<Item>()
        val listImage = FileUtil.getImagesByAlbum(context,albumName)
        val listVideo = FileUtil.getVideosByAlbum(context,albumName)
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

    fun getItemsByAlbum(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            _itemList.value = withContext(Dispatchers.IO) {
                getItemsByAlbumName(context)
            }!!
        }
    }


    fun deleteSelectedItem(context: Context, list: ArrayList<Item>) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(10)
            for (item in list) {
                val worker = Runnable {
                    FileUtil.deleteItem(context, item)
                }
                executor.execute(worker)
            }
            executor.shutdown()
            while (!executor.isTerminated) {
            }
            if (executor.isTerminated) {
                _itemList.value = getItemsByAlbumName(context)
            }
        }
    }

    fun moveSelectedItems(context: Context, list: ArrayList<Item>, album: Album) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(10)
            for (item in list) {
                val worker = Runnable {
                    FileUtil.moveItem(context, item, album)
                }
                executor.execute(worker)
            }
            executor.shutdown()
            while (!executor.isTerminated) {
            }
            if (executor.isTerminated) {
                _itemList.value = getItemsByAlbumName(context)
            }
        }
    }

    fun copySelectedItems(context: Context, list: ArrayList<Item>, album: Album) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(10)
            for (item in list) {
                val worker = Runnable {
                    FileUtil.copyItem(context, item, album)
                }
                executor.execute(worker)
            }
            executor.shutdown()
            while (!executor.isTerminated) {
            }
            if (executor.isTerminated) {
                _itemList.value = getItemsByAlbumName(context)
            }
        }
    }

}