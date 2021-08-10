package com.example.galleryview.viewmodels

import android.content.ContentUris
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


    private fun loadImages(context: Context): ArrayList<Item> {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.MediaColumns.DATA,
        )
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(albumName)
        val query =
            context.contentResolver.query(collection, projection, selection, selectionArgs, null)
        var listOfImages = ArrayList<Item>()
        query.use { cursor ->
            val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
            val albumNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val relativePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val absolutePathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val albumName = cursor.getString(albumNameColumn)
                val date = cursor.getLong(dateColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val absolutePath = cursor.getString(absolutePathColumn)
                listOfImages.add(Item(id, contentUri, albumName, date, relativePath, absolutePath))
            }
        }
        return listOfImages
    }

    private fun loadVideos(context: Context): ArrayList<Item> {
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.RELATIVE_PATH,
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Media.DURATION
        )
        val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(albumName)
        val query =
            context.contentResolver.query(collection, projection, selection, selectionArgs, null)
        var listOfVideos = ArrayList<Item>()
        query.use { cursor ->
            val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val albumNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val relativePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
            val absolutePathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val albumName = cursor.getString(albumNameColumn)
                val date = cursor.getLong(dateColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val absolutePath = cursor.getString(absolutePathColumn)
                val duration = cursor.getInt(durationColumn)

                val item = Item(id, contentUri, albumName, date, relativePath, absolutePath)
                item.isVideo = true
                item.duration = duration
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


    private fun deleteItem(context: Context, item: Item) {
        item.uri?.let { context.contentResolver.delete(it, null, null) }
    }

    fun deleteSelectedItem(context: Context, list: ArrayList<Item>) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(10)
            for (item in list) {
                val worker = Runnable {
                    deleteItem(context, item)
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