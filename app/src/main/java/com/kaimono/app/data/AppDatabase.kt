package com.kaimono.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MangaEntity::class, ChapterEntity::class, CategoryEntity::class,
        HistoryEntity::class, SourceEntity::class, ExtensionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun categoryDao(): CategoryDao
    abstract fun historyDao(): HistoryDao
    abstract fun sourceDao(): SourceDao
    abstract fun extensionDao(): ExtensionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "kaimono.db"
                ).build().also { INSTANCE = it }
            }
    }
}

