package com.example.galleryview.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp_album_table")
data class TempAlbum(
    @ColumnInfo(name = "name")
    val name: String,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "absolute_path")
    var absolutePath: String? = null

    @ColumnInfo(name = "images_count")
    var imagesCount: Int = 0

    @ColumnInfo(name = "video_count")
    var videoCount: Int = 0

    @ColumnInfo(name = "item_count")
    var itemsCount: Int = 0
}