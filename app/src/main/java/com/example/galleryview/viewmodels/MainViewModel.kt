package com.example.galleryview.viewmodels

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class MainViewModel : ViewModel(), CoroutineScope {
    private var _onClickPicture = MutableLiveData<Boolean>()
    val onClickPicture: LiveData<Boolean>
        get() = _onClickPicture


    private var _onClickAlbum = MutableLiveData<Boolean>()
    val onClickAlbum: LiveData<Boolean>
        get() = _onClickAlbum

    private var _onClickStory = MutableLiveData<Boolean>()
    val onClickStory: LiveData<Boolean>
        get() = _onClickStory

    private var _hideBottomNav = MutableLiveData<Boolean>()
    val hideBottomNav: LiveData<Boolean>
        get() = _hideBottomNav

    private var _hideFunctionNav = MutableLiveData<Boolean>()
    val hideFunctionNav: LiveData<Boolean>
        get() = _hideFunctionNav

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var _itemList = MutableLiveData<ArrayList<Item>>()
    val itemList: LiveData<ArrayList<Item>>
        get() = _itemList

    private var _itemView = MutableLiveData<ArrayList<Item>>()
    val itemView: LiveData<ArrayList<Item>>
        get() = _itemView

    private var _albums = MutableLiveData<MutableList<Album>>()
    val albums: LiveData<MutableList<Album>>
        get() = _albums

    private var _onLoading = MutableLiveData<Boolean>()
    val onLoading: LiveData<Boolean>
        get() = _onLoading

    var selectedList = ArrayList<Item>()

    fun openPictureFragment() {
        _onClickPicture.value = true
        _onClickAlbum.value = false
        _onClickStory.value = false
    }

    fun openAlbumFragment() {
        _onClickPicture.value = false
        _onClickAlbum.value = true
        _onClickStory.value = false
    }

    fun openStoryFragment() {
        _onClickPicture.value = false
        _onClickAlbum.value = false
        _onClickStory.value = true
    }

    fun hideBottomNavigation() {
        _hideBottomNav.value = true;
    }

    fun showBottomNavigation() {
        _hideBottomNav.value = false;
    }

    fun hideFunctionNavigation() {
        _hideFunctionNav.value = true;
    }

    fun showFunctionNavigation() {
        _hideFunctionNav.value = false;
    }


    private fun loadAlbums(context: Context): MutableList<Album> {
        val list = mutableListOf<Item>()
        list.addAll(loadImages(context))
        list.addAll(loadVideos(context))
        val map = list.groupBy { it.albumName }
        val listAlbumName = map.keys
        val listAlbumCount = map.values
        val listAlbum = mutableListOf<Album>()
        for (item in listAlbumName) {
            if (item != null) {
                listAlbum.add(Album(item))
            }
        }
        for ((index, value) in listAlbumCount.withIndex()) {
            listAlbum[index].itemCount = value.size
            listAlbum[index].lastItemPath = value[value.size - 1].absolutePath
            listAlbum[index].relativePath = value[0].relativePath
        }
        return listAlbum
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
        val query = context.contentResolver.query(collection, projection, null, null, null)
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
        val query = context.contentResolver.query(collection, projection, null, null, null)
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


    private fun getAllImagesAndVideos(context: Context): ArrayList<Item> {
        val list = ArrayList<Item>()
        list.addAll(loadImages(context))
        list.addAll(loadVideos(context))
        list.sortByDescending { it.createdDate }
        for ((index) in list.withIndex()) {
            list[index].position = index
        }
        return list
    }

    fun getAllItemView(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            _itemView.value = withContext(Dispatchers.IO) {
                getAllImagesAndVideos(context)
            }!!
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getAllItemsAndHeaders(context: Context): ArrayList<Item> {
        val list = getAllImagesAndVideos(context)
        if (list.size == 0) return list
        val date = Date(list[0].createdDate * 1000)
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val firstHeader =
            Item(-1, null, formatter.format(date), list[0].createdDate - 1, null, null)
        firstHeader.isHeader = true;
        list.add(0, firstHeader)
        var i = 1;
        while (i < list.size - 2) {
            val date1 = Date(list[i].createdDate * 1000)
            val date2 = Date(list[i + 1].createdDate * 1000)
            if (!formatter.format(date1).equals(formatter.format(date2))) {
                val headerItem =
                    Item(
                        -1,
                        null,
                        formatter.format(date2).toString(),
                        list[i + 1].createdDate + 1,
                        null,
                        null
                    )
                headerItem.isHeader = true
                list.add(i + 1, headerItem)
                i += 2
                continue
            }
            i++;
        }
        return list
    }

    private fun deleteItem(context: Context, item: Item) {
        item.uri?.let { context.contentResolver.delete(it, null, null) }
    }


    fun moveItem(context: Context, item: Item, album: Album) {
        if (item.isVideo) {
            val videoId = item.id
            val selection = "${MediaStore.Video.Media._ID} = ?"
            val selectionArgs = arrayOf(videoId.toString())
            val updateVideo = ContentValues().apply {
                put(MediaStore.Video.Media.RELATIVE_PATH, album.relativePath)
            }
            item.uri?.let {
                context.contentResolver.update(
                    it,
                    updateVideo,
                    selection,
                    selectionArgs
                )
            }
        } else {
            val imageId = item.id
            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(imageId.toString())
            val updateImage = ContentValues().apply {
                put(MediaStore.Images.Media.RELATIVE_PATH, album.relativePath)
            }
            item.uri?.let {
                context.contentResolver.update(
                    it,
                    updateImage,
                    selection,
                    selectionArgs
                )
            }
        }
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
                _itemList.value = getAllItemsAndHeaders(context)
                _onLoading.value = false
            }
        }
    }


    fun getAllItems(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            _itemList.value = withContext(Dispatchers.IO) {
                getAllItemsAndHeaders(context)
            }!!
        }
    }

    fun getAllAlbums(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            _albums.value = withContext(Dispatchers.IO) {
                loadAlbums(context)
            }!!
        }
    }
}