package com.kaimono.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaimono.app.ui.AppViewModel
import com.kaimono.app.ui.EmptyState
import com.kaimono.app.ui.LoadingScreen
import com.kaimono.app.ui.LocalSnack
import com.kaimono.app.ui.MangaCard
import kotlinx.coroutines.launch

@Composable
fun BrowseScreen(vm: AppViewModel, nav: NavHostController) {
    val sources by vm.sources.collectAsState()
    val loading by vm.initialLoading.collectAsState()
    var selectedSource by rememberSaveable { mutableStateOf<String?>(null) }

    val listFlow = remember(selectedSource) { vm.browse(selectedSource) }
    val manga by listFlow.collectAsState(initial = emptyList())
    val installedSources = sources.filter { it.installed }

    val snack = LocalSnack.current
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(selected = selectedSource == null,
                    onClick = { selectedSource = null }, label = { Text("All sources") })
            }
            items(installedSources) { s ->
                FilterChip(selected = selectedSource == s.id,
                    onClick = { selectedSource = if (selectedSource == s.id) null else s.id },
                    label = { Text("${s.name} (${s.lang})") })
            }
        }
        when {
            loading -> LoadingScreen("Fetching catalogues…")
            manga.isEmpty() -> EmptyState(icon = Icons.Filled.Explore, title = "Nothing here",
                message = "This source returned no titles. Try another source.")
            else -> LazyVerticalGrid(columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(manga, key = { it.id }) { m ->
                    MangaCard(manga = m, badge = "★ ${m.rating}",
                        onClick = { nav.navigate("manga/${m.id}") },
                        onLongClick = {
                            vm.setInLibrary(m.id, true)
                            scope.launch {
                                val res = snack.showSnackbar(
                                    "Added “${m.title}” to library", actionLabel = "Undo")
                                if (res == SnackbarResult.ActionPerformed) vm.setInLibrary(m.id, false)
                            }
                        })
                }
            }
        }
    }
}

