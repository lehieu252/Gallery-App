package com.example.galleryview.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.galleryview.models.TempAlbum

@Database(entities = [TempAlbum::class], version = 1, exportSchema = false)
abstract class TempAlbumDatabase : RoomDatabase() {
    abstract val tempAlbumDao: TempAlbumDAO

    companion object {
        @Volatile
        private var INSTANCE: TempAlbumDatabase? = null

        fun getInstance(context: Context): TempAlbumDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TempAlbumDatabase::class.java,
                        "temp_album_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}