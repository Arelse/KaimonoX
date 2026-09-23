package com.kaimono.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaimono.app.ui.AppViewModel
import com.kaimono.app.ui.Cover
import com.kaimono.app.ui.EmptyState
import com.kaimono.app.ui.LoadingScreen
import com.kaimono.app.ui.MangaCard
import com.kaimono.app.ui.SectionHeader
import com.kaimono.app.ui.StatCard
import com.kaimono.app.ui.timeAgo

@Composable
fun DashboardScreen(vm: AppViewModel, nav: NavHostController) {
    val loading by vm.initialLoading.collectAsState()
    val user by vm.user.collectAsState()
    val library by vm.library.collectAsState()
    val unread by vm.unreadCount.collectAsState()
    val readToday by vm.readTodayCount.collectAsState()
    val sources by vm.sourcesCount.collectAsState()
    val history by vm.history.collectAsState()
    val updates by vm.updates.collectAsState()

    if (loading) { LoadingScreen("Setting up your reading world…"); return }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Welcome back, ${user ?: "Reader"}",
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(if (unread > 0) "You have $unread unread chapters waiting."
                     else "All caught up. Time to find something new!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("In library", "$library", Icons.Filled.LibraryBooks, Modifier.weight(1f))
                StatCard("Unread", "$unread", Icons.Filled.NewReleases, Modifier.weight(1f))
                StatCard("Read today", "$readToday", Icons.Filled.History, Modifier.weight(1f))
                StatCard("Sources", "$sources", Icons.Filled.CloudDownload, Modifier.weight(1f))
            }
        }
        if (history.isNotEmpty()) {
            item { SectionHeader("Continue reading") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(history) { item ->
                        Column(modifier = Modifier.width(120.dp)) {
                            Box(modifier = Modifier.clickable {
                                nav.navigate("reader/${item.chapterId}")
                            }) {
                                Cover(item.manga.coverSeed, item.manga.title,
                                    Modifier.height(160.dp).width(120.dp))
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(item.manga.title, style = MaterialTheme.typography.labelMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Ch. ${item.chapterNumber.toInt()} • ${timeAgo(item.readAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        if (library.isNotEmpty()) {
            item { SectionHeader("Your library") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(library) { m ->
                        MangaCard(m, Modifier.width(120.dp), badge = m.status,
                            onClick = { nav.navigate("manga/${m.id}") })
                    }
                }
            }
        }
        item { SectionHeader("Latest updates") }
        if (updates.isEmpty()) {
            item {
                EmptyState(icon = Icons.Filled.NewReleases, title = "No updates yet",
                    message = "Add titles to your library and new chapter releases will appear here.",
                    actionLabel = "Browse sources", onAction = { nav.navigate("browse") })
            }
        } else {
            items(updates.take(12)) { u ->
                Card(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { nav.navigate("manga/${u.mangaId}") },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                    Row(modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Cover(u.coverSeed, u.mangaTitle,
                            Modifier.size(width = 40.dp, height = 56.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(u.mangaTitle, style = MaterialTheme.typography.titleSmall,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Ch. ${u.chapter.number.toInt()} — ${u.chapter.title}" +
                                if (u.chapter.read) "  ✓ read" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text(timeAgo(u.chapter.dateAdded),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
