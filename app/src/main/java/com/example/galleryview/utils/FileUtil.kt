package com.example.galleryview.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.galleryview.models.Album
import com.example.galleryview.models.Item
import java.io.File

class FileUtil {
    companion object {
        fun deleteItem(context: Context, item: Item) {
            val file = File(item.absolutePath)
            if (file.exists()) {
                file.delete()
            }
            item.uri?.let { context.contentResolver.delete(it, null, null) }
        }

        fun copyItem(context: Context, item: Item, album: Album) {
            val newFile = File(album.relativePath, item.name)
            if (newFile.exists()) {
                return
            } else {
                if (item.isVideo) {
                    File(item.absolutePath).copyTo(newFile, false)
                    val newImage = ContentValues().apply {
                        put(MediaStore.Video.Media.DATA, "${album.relativePath}/${item.name}")
                        put(MediaStore.Video.Media.DURATION, item.duration)
                    }
                    context.contentResolver.insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        newImage
                    )
                } else {
                    File(item.absolutePath).copyTo(newFile, false)
                    val newImage = ContentValues().apply {
                        put(MediaStore.Images.Media.DATA, "${album.relativePath}/${item.name}")
                    }
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        newImage
                    )
                }
            }
        }

        fun moveItem(context: Context, item: Item, album: Album) {
            val newFile = File(album.relativePath, item.name)
            if (newFile.exists()) {
                return
            } else {
                if (item.isVideo) {
                    File(item.absolutePath).copyTo(newFile, false)
                    val newVideo = ContentValues().apply {
                        put(MediaStore.Video.Media.DATA, "${album.relativePath}/${item.name}")
                        put(MediaStore.Video.Media.DURATION, item.duration)
                    }
                    context.contentResolver.insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        newVideo
                    )
                } else {
                    File(item.absolutePath).copyTo(newFile, false)
                    deleteItem(context, item)
                    val newImage = ContentValues().apply {
                        put(MediaStore.Images.Media.DATA, "${album.relativePath}/${item.name}")
                    }
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        newImage
                    )
                }
            }
        }

        fun getAllAlbums(context: Context): MutableList<Album> {
            val list = mutableListOf<Item>()
            list.addAll(getAllImages(context))
            list.addAll(getAllVideos(context))
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
                listAlbum[index].relativePath =
                    value[0].absolutePath?.let {
                        value[0].absolutePath?.substring(
                            0,
                            it.lastIndexOf('/')
                        )
                    }
            }
            return listAlbum
        }

        fun getAllImages(context: Context): ArrayList<Item> {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
            )
            val query = context.contentResolver.query(collection, projection, null, null, null)
            var listOfImages = ArrayList<Item>()
            query.use { cursor ->
                val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                val albumNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val absolutePathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    val albumName = cursor.getString(albumNameColumn)
                    val date = cursor.getLong(dateColumn)
                    val name = cursor.getString(nameColumn)
                    val absolutePath = cursor.getString(absolutePathColumn)
                    listOfImages.add(Item(id, contentUri, albumName, date, name, absolutePath))
                }
            }
            return listOfImages
        }

        fun getAllVideos(context: Context): ArrayList<Item> {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DISPLAY_NAME,
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
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val absolutePathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    val albumName = cursor.getString(albumNameColumn)
                    val date = cursor.getLong(dateColumn)
                    val name = cursor.getString(nameColumn)
                    val absolutePath = cursor.getString(absolutePathColumn)
                    val duration = cursor.getInt(durationColumn)

                    val item = Item(id, contentUri, albumName, date, name, absolutePath)
                    item.isVideo = true
                    item.duration = duration
                    listOfVideos.add(item)
                }
            }
            return listOfVideos
        }

         fun getImagesByAlbum(context: Context, albumName: String): ArrayList<Item> {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
            )
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(albumName)
            val query =
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
            var listOfImages = ArrayList<Item>()
            query.use { cursor ->
                val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                val albumNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val absolutePathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    val albumName = cursor.getString(albumNameColumn)
                    val date = cursor.getLong(dateColumn)
                    val name = cursor.getString(nameColumn)
                    val absolutePath = cursor.getString(absolutePathColumn)
                    listOfImages.add(Item(id, contentUri, albumName, date, name, absolutePath))
                }
            }
            return listOfImages
        }

         fun getVideosByAlbum(context: Context, albumName: String): ArrayList<Item> {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.DURATION
            )
            val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(albumName)
            val query =
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
            var listOfVideos = ArrayList<Item>()
            query.use { cursor ->
                val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val albumNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val absolutePathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    val albumName = cursor.getString(albumNameColumn)
                    val date = cursor.getLong(dateColumn)
                    val name = cursor.getString(nameColumn)
                    val absolutePath = cursor.getString(absolutePathColumn)
                    val duration = cursor.getInt(durationColumn)

                    val item = Item(id, contentUri, albumName, date, name, absolutePath)
                    item.isVideo = true
                    item.duration = duration
                    listOfVideos.add(item)
                }
            }
            return listOfVideos
        }
    }


}