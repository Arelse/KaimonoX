package com.kaimono.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaimono.app.data.CategoryEntity
import com.kaimono.app.data.MangaEntity
import com.kaimono.app.ui.AppViewModel
import com.kaimono.app.ui.EmptyState
import com.kaimono.app.ui.LoadingScreen
import com.kaimono.app.ui.LocalSnack
import com.kaimono.app.ui.MangaCard
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(vm: AppViewModel, nav: NavHostController) {
    val library by vm.library.collectAsState()
    val categories by vm.categories.collectAsState()
    val loading by vm.initialLoading.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<Long?>(null) }
    var manageCategories by remember { mutableStateOf(false) }
    var actionManga by remember { mutableStateOf<MangaEntity?>(null) }
    var pendingRemove by remember { mutableStateOf<MangaEntity?>(null) }

    val snack = LocalSnack.current
    val scope = rememberCoroutineScope()

    val filtered = library.filter { m ->
        (selectedCategory == null || m.categoryId == selectedCategory) &&
            (query.isBlank() || m.title.contains(query, ignoreCase = true))
    }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = { query = it },
            placeholder = { Text("Search your library") },
            leadingIcon = { Icon(Icons.Filled.Search, null) }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All (${library.size})") })
            }
            items(categories) { c ->
                FilterChip(selected = selectedCategory == c.id,
                    onClick = { selectedCategory = if (selectedCategory == c.id) null else c.id },
                    label = { Text(c.name) })
            }
            item {
                FilterChip(selected = false, onClick = { manageCategories = true },
                    label = { Text("⚙ Manage") })
            }
        }
        when {
            loading -> LoadingScreen()
            filtered.isEmpty() -> EmptyState(
                icon = if (library.isEmpty()) Icons.Filled.LibraryBooks else Icons.Filled.FolderOff,
                title = if (library.isEmpty()) "Your library is empty" else "Nothing matches",
                message = if (library.isEmpty()) "Browse your sources and add titles with the + action."
                          else "Try a different search or category filter.",
                actionLabel = if (library.isEmpty()) "Browse sources" else null,
                onAction = if (library.isEmpty()) ({ nav.navigate("browse") }) else null)
            else -> LazyVerticalGrid(columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.id }) { m ->
                    MangaCard(manga = m,
                        badge = categories.firstOrNull { it.id == m.categoryId }?.name,
                        onClick = { nav.navigate("manga/${m.id}") },
                        onLongClick = { actionManga = m })
                }
            }
        }
    }

    actionManga?.let { m ->
        AlertDialog(onDismissRequest = { actionManga = null }, title = { Text(m.title) },
            text = {
                Column {
                    TextButton(onClick = { vm.toggleFavorite(m.id); actionManga = null }) {
                        Icon(Icons.Filled.Star, null); Spacer(Modifier.width(8.dp))
                        Text(if (m.favorite) "Remove from favorites" else "Add to favorites")
                    }
                    TextButton(onClick = { pendingRemove = m; actionManga = null }) {
                        Icon(Icons.Filled.Delete, null); Spacer(Modifier.width(8.dp))
                        Text("Remove from library")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { actionManga = null }) { Text("Close") } })
    }

    pendingRemove?.let { m ->
        AlertDialog(onDismissRequest = { pendingRemove = null },
            title = { Text("Remove from library?") },
            text = { Text("“${m.title}” will be removed. This can be undone immediately.") },
            confirmButton = {
                TextButton(onClick = {
                    pendingRemove = null
                    vm.setInLibrary(m.id, false)
                    scope.launch {
                        val res = snack.showSnackbar("Removed “${m.title}”", actionLabel = "Undo")
                        if (res == SnackbarResult.ActionPerformed) vm.setInLibrary(m.id, true)
                    }
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { pendingRemove = null }) { Text("Cancel") } })
    }

    if (manageCategories) {
        var newName by remember { mutableStateOf("") }
        var editing by remember { mutableStateOf<CategoryEntity?>(null) }
        AlertDialog(onDismissRequest = { manageCategories = false },
            title = { Text("Categories") },
            text = {
                Column {
                    categories.forEach { c ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()) {
                            Text(c.name, modifier = Modifier.weight(1f))
                            IconButton(onClick = { editing = c; newName = c.name }) {
                                Icon(Icons.Filled.Edit, "Rename") }
                            IconButton(onClick = {
                                vm.deleteCategory(c.id)
                                if (selectedCategory == c.id) selectedCategory = null
                            }) { Icon(Icons.Filled.Delete, "Delete") }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = newName, onValueChange = { newName = it },
                        label = { Text(if (editing == null) "New category" else "Rename category") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                val name = newName.trim()
                                if (name.isNotEmpty()) {
                                    if (editing == null) vm.addCategory(name)
                                    else vm.renameCategory(editing!!.id, name)
                                    newName = ""; editing = null
                                }
                            }) { Icon(Icons.Filled.Add, "Save") }
                        })
                }
            },
            confirmButton = { TextButton(onClick = { manageCategories = false }) { Text("Done") } })
    }
}
