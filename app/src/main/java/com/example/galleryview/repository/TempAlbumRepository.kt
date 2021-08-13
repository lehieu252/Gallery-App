package com.example.galleryview.repository

import com.example.galleryview.models.TempAlbum
import com.example.galleryview.room.TempAlbumDAO

class TempAlbumRepository(private val tempAlbumDAO: TempAlbumDAO) {
    suspend fun insert(album: TempAlbum) = tempAlbumDAO.insert(album)

    fun delete(name: String) = tempAlbumDAO.delete(name)

    fun getAllTempAlbum() = tempAlbumDAO.getAll()
}