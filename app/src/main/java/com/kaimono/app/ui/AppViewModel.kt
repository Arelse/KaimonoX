package com.kaimono.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaimono.app.data.CategoryEntity
import com.kaimono.app.data.ChapterEntity
import com.kaimono.app.data.ExtensionEntity
import com.kaimono.app.data.HistoryItem
import com.kaimono.app.data.LibraryUpdate
import com.kaimono.app.data.MangaEntity
import com.kaimono.app.data.Repository
import com.kaimono.app.data.SourceEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(app)

    val initialLoading = MutableStateFlow(true)

    val theme: StateFlow<String> = repo.theme
        .stateIn(viewModelScope, SharingStarted.Eagerly, "system")
    val user: StateFlow<String?> = repo.user
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val library: StateFlow<List<MangaEntity>> = repo.library
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allManga: StateFlow<List<MangaEntity>> = repo.mangaAll
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val categories: StateFlow<List<CategoryEntity>> = repo.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val history: StateFlow<List<HistoryItem>> = repo.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val updates: StateFlow<List<LibraryUpdate>> = repo.updates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sources: StateFlow<List<SourceEntity>> = repo.sources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val extensions: StateFlow<List<ExtensionEntity>> = repo.extensions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val libraryCount: StateFlow<Int> = repo.libraryCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val unreadCount: StateFlow<Int> = repo.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val readTodayCount: StateFlow<Int> = repo.readTodayCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val sourcesCount: StateFlow<Int> = repo.installedSourcesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val favoritesCount: StateFlow<Int> = repo.favoriteCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repo.ensureSeeded()
            delay(600)
            initialLoading.value = false
        }
    }

    fun manga(id: String) = repo.manga(id)
    fun chaptersFor(mangaId: String) = repo.chaptersFor(mangaId)
    fun chapter(id: String) = repo.chapter(id)
    fun mangaForChapter(chapterId: String) = repo.mangaForChapter(chapterId)
    fun browse(sourceId: String?) = repo.browse(sourceId)
    fun unreadForManga(mangaId: String) = repo.unreadForManga(mangaId)
    suspend fun resumeChapterId(mangaId: String) = repo.resumeChapterId(mangaId)
    suspend fun neighborChapterIds(chapterId: String) = repo.neighborChapterIds(chapterId)

    fun signIn(name: String) = launch { repo.setUser(name) }
    fun signOut() = launch { repo.setUser(null) }
    fun setTheme(key: String) = launch { repo.setTheme(key) }
    fun toggleFavorite(id: String) = launch { repo.toggleFavorite(id) }
    fun setInLibrary(id: String, value: Boolean) = launch { repo.setInLibrary(id, value) }
    fun setCategory(id: String, categoryId: Long?) = launch { repo.setCategory(id, categoryId) }
    fun markRead(chapterId: String, mangaId: String, read: Boolean) =
        launch { repo.markRead(chapterId, mangaId, read) }
    fun toggleBookmark(chapterId: String) = launch { repo.toggleBookmark(chapterId) }
    fun addCategory(name: String) = launch { repo.addCategory(name) }
    fun renameCategory(id: Long, name: String) = launch { repo.renameCategory(id, name) }
    fun deleteCategory(id: Long) = launch { repo.deleteCategory(id) }
    fun toggleExtension(ext: ExtensionEntity) = launch { repo.toggleExtension(ext) }
    fun clearHistory() = launch { repo.clearHistory() }
    fun removeHistoryEntry(mangaId: String) = launch { repo.removeHistoryEntry(mangaId) }
    fun reseed() = launch {
        initialLoading.value = true
        repo.reseed()
        delay(400)
        initialLoading.value = false
    }

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch { block() }
}
