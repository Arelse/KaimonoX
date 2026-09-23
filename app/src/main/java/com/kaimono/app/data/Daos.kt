package com.kaimono.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga ORDER BY title")
    fun observeAll(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE inLibrary = 1 ORDER BY title")
    fun observeLibrary(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE id = :id")
    fun observeById(id: String): Flow<MangaEntity?>

    @Query("SELECT * FROM manga WHERE sourceId = :sourceId ORDER BY rating DESC")
    fun observeBySource(sourceId: String): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE id = :id")
    suspend fun get(id: String): MangaEntity?

    @Query("SELECT * FROM manga WHERE id IN (SELECT mangaId FROM chapter WHERE id = :chapterId)")
    fun observeByChapter(chapterId: String): Flow<MangaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MangaEntity>)

    @Update
    suspend fun update(manga: MangaEntity)

    @Query("UPDATE manga SET favorite = :value WHERE id = :id")
    suspend fun setFavorite(id: String, value: Boolean)

    @Query("UPDATE manga SET inLibrary = :value WHERE id = :id")
    suspend fun setInLibrary(id: String, value: Boolean)

    @Query("UPDATE manga SET categoryId = :categoryId WHERE id = :id")
    suspend fun setCategory(id: String, categoryId: Long?)

    @Query("UPDATE manga SET categoryId = NULL WHERE categoryId = :categoryId")
    suspend fun clearCategory(categoryId: Long)

    @Query("SELECT COUNT(*) FROM manga WHERE inLibrary = 1")
    fun libraryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM manga WHERE favorite = 1 AND inLibrary = 1")
    fun favoriteCount(): Flow<Int>
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId ORDER BY number DESC")
    fun observeForManga(mangaId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapter WHERE id = :id")
    fun observeById(id: String): Flow<ChapterEntity?>

    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId ORDER BY number")
    suspend fun listForManga(mangaId: String): List<ChapterEntity>

    @Query("SELECT * FROM chapter WHERE id = :id")
    suspend fun get(id: String): ChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ChapterEntity>)

    @Query("UPDATE chapter SET read = :read, lastReadAt = :at WHERE id = :id")
    suspend fun setRead(id: String, read: Boolean, at: Long?)

    @Query("UPDATE chapter SET bookmark = :value WHERE id = :id")
    suspend fun setBookmark(id: String, value: Boolean)

    @Query("SELECT COUNT(*) FROM chapter WHERE read = 0 AND mangaId IN (SELECT id FROM manga WHERE inLibrary = 1)")
    fun unreadInLibraryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapter WHERE read = 1 AND lastReadAt > :from")
    fun readSinceCount(from: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapter WHERE mangaId = :mangaId AND read = 0")
    fun unreadForManga(mangaId: String): Flow<Int>

    @Query("SELECT chapter.*, manga.id AS mId, manga.title AS mTitle, manga.coverSeed AS mCover, manga.favorite AS mFavorite FROM chapter JOIN manga ON manga.id = chapter.mangaId WHERE manga.inLibrary = 1 ORDER BY chapter.dateAdded DESC LIMIT 25")
    fun observeLatestUpdates(): Flow<List<LibraryUpdate>>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY sortOrder, name")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM category WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun get(id: Long): CategoryEntity?
}

@Dao
interface HistoryDao {
    @Query("SELECT history.readAt AS readAt, manga.*, chapter.id AS cId, chapter.number AS cNumber, chapter.title AS cTitle, chapter.read AS cRead FROM history JOIN manga ON manga.id = history.mangaId JOIN chapter ON chapter.id = history.chapterId ORDER BY history.readAt DESC")
    fun observeRecent(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: HistoryEntity)

    @Query("DELETE FROM history WHERE mangaId = :mangaId")
    suspend fun delete(mangaId: String)

    @Query("DELETE FROM history")
    suspend fun clear()
}

@Dao
interface SourceDao {
    @Query("SELECT * FROM source ORDER BY name")
    fun observeAll(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM source WHERE installed = 1 ORDER BY name")
    fun observeInstalled(): Flow<List<SourceEntity>>

    @Query("SELECT COUNT(*) FROM source WHERE installed = 1")
    fun installedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SourceEntity>)

    @Query("UPDATE source SET installed = :value WHERE extensionId = :extensionId")
    suspend fun setInstalledForExtension(extensionId: String, value: Boolean)
}

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extension ORDER BY name")
    fun observeAll(): Flow<List<ExtensionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ExtensionEntity>)

    @Query("UPDATE extension SET installed = :value WHERE id = :id")
    suspend fun setInstalled(id: String, value: Boolean)
}

