package com.example.galleryview.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryview.models.Album
import com.example.galleryview.models.Item
import kotlinx.coroutines.*
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


    private fun loadImages(context: Context): ArrayList<Item> {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(albumName)
        val query =
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        var listOfImages = ArrayList<Item>()
        query.use { cursor ->
            val pathColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val nameColumn =
                cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            while (cursor!!.moveToNext()) {
                val name = nameColumn?.let { cursor.getString(it) }
                val date = dateColumn?.let { cursor.getLong(it) }
                val path = pathColumn?.let { cursor.getString(pathColumn) }
                listOfImages.add(Item(name, date!!, path, 0))
            }
        }
        return listOfImages
    }

    private fun loadVideos(context: Context): ArrayList<Item> {
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION
        )
        val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(albumName)
        val query =
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        var listOfVideos = ArrayList<Item>()
        query.use { cursor ->
            val pathColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val nameColumn =
                cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val dateColumn = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            while (cursor!!.moveToNext()) {
                val name = nameColumn?.let { cursor.getString(it) }
                val date = dateColumn?.let { cursor.getLong(it) }
                val path = pathColumn?.let { cursor.getString(it) }
                val duration = durationColumn?.let { cursor.getInt(it) }
                val item = Item(name, date!!, path, duration!!)
                item.isVideo = true
                listOfVideos.add(item)
            }
        }
        return listOfVideos
    }

    private fun getItemsByAlbumName(context: Context): ArrayList<Item> {
        val list = ArrayList<Item>()
        val listImage = loadImages(context)
        val listVideo = loadVideos(context)
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
}