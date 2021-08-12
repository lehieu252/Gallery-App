package com.example.galleryview.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp_album_table")
data class TempAlbum(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "absolute_path")
    val absolutePath: String,

    @ColumnInfo(name = "images_count")
    var imagesCount: Int = 0,

    @ColumnInfo(name = "video_count")
    val videoCount: Int = 0,

    @ColumnInfo(name = "item_count")
    val itemsCount: Int = 0,
)