package com.example.galleryview.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.galleryview.models.TempAlbum

@Dao
interface TempAlbumDAO {
    @Insert
    fun insert(tempAlbum: TempAlbum)

    @Delete
    fun delete(tempAlbum: TempAlbum)

    @Query("SELECT * FROM temp_album_table")
    fun getAll(): ArrayList<TempAlbum>
}