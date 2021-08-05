package com.example.galleryview.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.galleryview.models.Item
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class PictureViewModel : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var _itemList = MutableLiveData<ArrayList<Item>>()
    val itemList: LiveData<ArrayList<Item>>
        get() = _itemList

    private fun loadImages(context: Context): ArrayList<Item> {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val query = context.contentResolver.query(uri, projection, null, null, sortOrder)
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
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val query = context.contentResolver.query(uri, projection, null, null, sortOrder)
        var listOfVideos = ArrayList<Item>()
        query.use { cursor ->
            val pathColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val nameColumn = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
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

    @SuppressLint("SimpleDateFormat")
    private fun getAllItemAndHeader(context: Context): ArrayList<Item> {
        val list = ArrayList<Item>()
        list.addAll(loadImages(context))
        list.addAll(loadVideos(context))
        list.sortByDescending { it.createdDate }
        val date = Date(list[0].createdDate * 1000)
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val firstHeader = Item(formatter.format(date), list[0].createdDate - 1, null,0)
        firstHeader.isHeader = true;
        list.add(0,firstHeader)
        var i = 1;
        while (i < list.size - 2) {
            val date1 = Date(list[i].createdDate * 1000)
            val date2 = Date(list[i + 1].createdDate  * 1000)
            if (!formatter.format(date1).equals(formatter.format(date2))) {
                val headerItem = Item(formatter.format(date2).toString(), list[i + 1].createdDate + 1, null,0)
                headerItem.isHeader = true
                list.add(i + 1, headerItem)
                i += 2
                continue
            }
            i++;
        }
//        for (item in list) {
//            item.name?.let { Log.d("item : ", it) }
//        }
        return list
    }

    fun getAllImages(context: Context) {
        launch(Dispatchers.Main) {
            _itemList.value?.clear()
            _itemList.value = withContext(Dispatchers.IO) {
                getAllItemAndHeader(context)
            }
        }
    }
}