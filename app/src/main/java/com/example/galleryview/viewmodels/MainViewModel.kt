package com.example.galleryview.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryview.models.Album
import com.example.galleryview.models.Item
import com.example.galleryview.models.TempAlbum
import com.example.galleryview.repository.TempAlbumRepository
import com.example.galleryview.room.TempAlbumDatabase
import com.example.galleryview.utilities.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class MainViewModel(val context: Context) : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val repository: TempAlbumRepository

    init {
        val tempAlbumDAO = TempAlbumDatabase.getInstance(context).tempAlbumDao()
        repository = TempAlbumRepository(tempAlbumDAO)
    }

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

    private var _hideAlbumFunctionNav = MutableLiveData<Boolean>()
    val hideAlbumFunctionNav: LiveData<Boolean>
        get() = _hideAlbumFunctionNav


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

    private var _isCreatedAlbum = MutableLiveData<Boolean>()
    val isCreatedAlbum: LiveData<Boolean>
        get() = _isCreatedAlbum

    var selectedList = ArrayList<Item>()
    var selectedAlbum = ArrayList<Album>()

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

    fun hideAlbumFNav() {
        _hideAlbumFunctionNav.value = true;
    }

    fun showAlbumFNav() {
        _hideAlbumFunctionNav.value = false;
    }


    private fun getAllImagesAndVideos(context: Context): ArrayList<Item> {
        val list = ArrayList<Item>()
        list.addAll(FileUtil.getAllImages(context))
        list.addAll(FileUtil.getAllVideos(context))
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


    fun moveSelectedItems(context: Context, list: ArrayList<Item>, album: Album) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(10)
            for (item in list) {
                val worker = Runnable {
                    FileUtil.moveItem(context, item, album)
                    if(album.isTempAlbum){
                        repository.delete(album.name)
                    }
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

    fun copySelectedItems(context: Context, list: ArrayList<Item>, album: Album) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(10)
            for (item in list) {
                val worker = Runnable {
                    FileUtil.copyItem(context, item, album)
                    if(album.isTempAlbum){
                        repository.delete(album.name)
                    }
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
                _itemList.value = getAllItemsAndHeaders(context)
                _onLoading.value = false
            }
        }
    }

    fun insertAlbum(albumName: String) {
        val folder = File("/storage/emulated/0/DCIM/", albumName)
        if (!folder.exists()) {
            folder.mkdirs()
            viewModelScope.launch {
                val tempAlbum = TempAlbum(albumName)
                tempAlbum.absolutePath = folder.absolutePath
                repository.insert(tempAlbum)
            }
            Toast.makeText(context,"Create album successfully", Toast.LENGTH_SHORT).show()
            getAllAlbums(context)
        } else {
            Toast.makeText(context,"Album is exist, try another name", Toast.LENGTH_SHORT).show()
            getAllAlbums(context)
        }
    }

    fun deleteSelectedAlbum(context: Context, list: ArrayList<Album>) {
        viewModelScope.launch {
            val executor = Executors.newFixedThreadPool(5)
            for (album in list) {
                val worker = Runnable {
                    if (album.isTempAlbum) {
                        repository.delete(album.name)
                        File(album.absolutePath).delete()
                    } else {
                        FileUtil.deleteAlbum(context, album)
                    }
                }
                executor.execute(worker)
            }
            executor.shutdown()
            while (!executor.isTerminated) {
            }
            if (executor.isTerminated) {
                _albums.value = FileUtil.getAllAlbums(context)
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
                val listAlbums = FileUtil.getAllAlbums(context)
                val listTempAlbum = repository.getAllTempAlbum()
                for (item in listTempAlbum) {
                    val album = Album(item.name)
                    album.absolutePath = item.absolutePath
                    album.isTempAlbum = true
                    listAlbums.add(album)
                }
                listAlbums
            }!!
        }
    }

}