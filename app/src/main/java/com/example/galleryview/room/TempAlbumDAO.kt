package com.example.galleryview.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.galleryview.models.TempAlbum

@Dao
interface TempAlbumDAO {
    @Insert
    suspend fun insert(tempAlbum: TempAlbum)

    @Query("DELETE FROM temp_album_table WHERE name = :albumName")
    fun delete(albumName: String)

    @Query("SELECT * FROM temp_album_table")
    fun getAll(): List<TempAlbum>
}