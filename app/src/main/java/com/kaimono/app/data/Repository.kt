package com.kaimono.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class Repository(context: Context) {
    private val db = AppDatabase.get(context)
    private val prefs = Prefs(context)

    val theme = prefs.themeFlow
    val user = prefs.userFlow

    val mangaAll: Flow<List<MangaEntity>> = db.mangaDao().observeAll()
    val library: Flow<List<MangaEntity>> = db.mangaDao().observeLibrary()
    val categories: Flow<List<CategoryEntity>> = db.categoryDao().observeAll()
    val history: Flow<List<HistoryItem>> = db.historyDao().observeRecent()
    val updates: Flow<List<LibraryUpdate>> = db.chapterDao().observeLatestUpdates()
    val sources: Flow<List<SourceEntity>> = db.sourceDao().observeAll()
    val sourcesInstalled: Flow<List<SourceEntity>> = db.sourceDao().observeInstalled()
    val extensions: Flow<List<ExtensionEntity>> = db.extensionDao().observeAll()
    val libraryCount: Flow<Int> = db.mangaDao().libraryCount()
    val unreadCount: Flow<Int> = db.chapterDao().unreadInLibraryCount()
    val installedSourcesCount: Flow<Int> = db.sourceDao().installedCount()
    val favoriteCount: Flow<Int> = db.mangaDao().favoriteCount()

    fun readTodayCount(): Flow<Int> {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return db.chapterDao().readSinceCount(cal.timeInMillis)
    }

    fun manga(id: String): Flow<MangaEntity?> = db.mangaDao().observeById(id)
    fun chaptersFor(mangaId: String): Flow<List<ChapterEntity>> = db.chapterDao().observeForManga(mangaId)
    fun chapter(id: String): Flow<ChapterEntity?> = db.chapterDao().observeById(id)
    fun mangaForChapter(chapterId: String): Flow<MangaEntity?> = db.mangaDao().observeByChapter(chapterId)
    fun browse(sourceId: String?): Flow<List<MangaEntity>> =
        if (sourceId == null) mangaAll else db.mangaDao().observeBySource(sourceId)
    fun unreadForManga(mangaId: String): Flow<Int> = db.chapterDao().unreadForManga(mangaId)

    suspend fun ensureSeeded() {
        if (!prefs.seededFlow.first()) {
            Seed.insert(db)
            prefs.setSeeded(true)
        }
    }

    suspend fun reseed() {
        db.clearAllTables()
        Seed.insert(db)
    }

    suspend fun setTheme(value: String) = prefs.setTheme(value)
    suspend fun setUser(value: String?) = prefs.setUser(value)

    suspend fun toggleFavorite(id: String) {
        val current = db.mangaDao().get(id) ?: return
        db.mangaDao().setFavorite(id, !current.favorite)
    }

    suspend fun setInLibrary(id: String, value: Boolean) {
        db.mangaDao().setInLibrary(id, value)
        if (!value) db.mangaDao().setCategory(id, null)
    }

    suspend fun setCategory(id: String, categoryId: Long?) = db.mangaDao().setCategory(id, categoryId)

    suspend fun markRead(chapterId: String, mangaId: String, read: Boolean) {
        val now = System.currentTimeMillis()
        db.chapterDao().setRead(chapterId, read, if (read) now else null)
        if (read) db.historyDao().upsert(HistoryEntity(mangaId, chapterId, now))
        else db.historyDao().delete(mangaId)
    }

    suspend fun toggleBookmark(chapterId: String) {
        val current = db.chapterDao().get(chapterId) ?: return
        db.chapterDao().setBookmark(chapterId, !current.bookmark)
    }

    suspend fun addCategory(name: String) = db.categoryDao().insert(CategoryEntity(name = name.trim()))

    suspend fun renameCategory(id: Long, name: String) {
        val current = db.categoryDao().get(id) ?: return
        db.categoryDao().update(current.copy(name = name.trim()))
    }

    suspend fun deleteCategory(id: Long) {
        db.mangaDao().clearCategory(id)
        db.categoryDao().delete(id)
    }

    suspend fun toggleExtension(ext: ExtensionEntity) {
        val newValue = !ext.installed
        db.extensionDao().setInstalled(ext.id, newValue)
        db.sourceDao().setInstalledForExtension(ext.id, newValue)
    }

    suspend fun resumeChapterId(mangaId: String): String? {
        val chapters = db.chapterDao().listForManga(mangaId)
        val historyChapter = chapters.firstOrNull { it.read && it.lastReadAt != null }
            ?.let { read -> chapters.filter { !it.read && it.number > read.number }.minByOrNull { it.number } }
        return (historyChapter ?: chapters.firstOrNull { !it.read })?.id
    }

    suspend fun neighborChapterIds(chapterId: String): Pair<String?, String?>? {
        val chapter = db.chapterDao().get(chapterId) ?: return null
        val chapters = db.chapterDao().listForManga(chapter.mangaId)
        val idx = chapters.indexOfFirst { it.id == chapterId }
        if (idx < 0) return null
        return chapters.getOrNull(idx - 1)?.id to chapters.getOrNull(idx + 1)?.id
    }

    suspend fun clearHistory() = db.historyDao().clear()
    suspend fun removeHistoryEntry(mangaId: String) = db.historyDao().delete(mangaId)
}
