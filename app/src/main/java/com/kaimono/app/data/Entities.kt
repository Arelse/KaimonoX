package com.kaimono.app.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String,
    val status: String,
    val sourceId: String,
    val inLibrary: Boolean = false,
    val favorite: Boolean = false,
    val rating: Float = 0f,
    val genres: String = "",
    val coverSeed: Int = 0,
    val categoryId: Long? = null,
)

@Entity(tableName = "chapter")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val mangaId: String,
    val number: Double,
    val title: String,
    val pages: Int = 10,
    val read: Boolean = false,
    val bookmark: Boolean = false,
    val lastReadAt: Long? = null,
    val dateAdded: Long = 0L,
)

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sortOrder: Int = 0,
)

@Entity(tableName = "history", primaryKeys = ["mangaId"])
data class HistoryEntity(
    val mangaId: String,
    val chapterId: String,
    val readAt: Long,
)

@Entity(tableName = "source")
data class SourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lang: String,
    val version: String,
    val extensionId: String,
    val installed: Boolean = true,
    val nsfw: Boolean = false,
)

@Entity(tableName = "extension")
data class ExtensionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: String,
    val sourceCount: Int = 1,
    val installed: Boolean = true,
    val notes: String = "",
)

data class HistoryItem(
    val readAt: Long,
    @Embedded val manga: MangaEntity,
    @ColumnInfo(name = "cId") val chapterId: String,
    @ColumnInfo(name = "cNumber") val chapterNumber: Double,
    @ColumnInfo(name = "cTitle") val chapterTitle: String,
    @ColumnInfo(name = "cRead") val chapterRead: Boolean,
)

data class LibraryUpdate(
    @Embedded val chapter: ChapterEntity,
    @ColumnInfo(name = "mId") val mangaId: String,
    @ColumnInfo(name = "mTitle") val mangaTitle: String,
    @ColumnInfo(name = "mCover") val coverSeed: Int,
    @ColumnInfo(name = "mFavorite") val mangaFavorite: Boolean,
)

