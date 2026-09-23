package com.kaimono.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaimono.app.ui.AppViewModel
import com.kaimono.app.ui.Cover
import com.kaimono.app.ui.LoadingScreen
import com.kaimono.app.ui.timeAgo
import kotlinx.coroutines.launch

@Composable
fun MangaDetailScreen(vm: AppViewModel, nav: NavHostController, mangaId: String) {
    val manga by vm.manga(mangaId).collectAsState(initial = null)
    val chapters by vm.chaptersFor(mangaId).collectAsState(initial = emptyList())
    val unread by vm.unreadForManga(mangaId).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()

    val m = manga ?: run { LoadingScreen("Opening title…"); return }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Row(modifier = Modifier.padding(16.dp)) {
                Cover(m.coverSeed, m.title, Modifier.size(width = 110.dp, height = 160.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(m.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(m.author, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    AssistChip(onClick = {}, label = { Text(m.status) })
                    Spacer(Modifier.height(8.dp))
                    Text("★ ${m.rating}  •  ${chapters.size} chapters  •  $unread unread",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (m.inLibrary) {
                    OutlinedButton(onClick = { vm.setInLibrary(m.id, false) },
                        modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.CheckCircle, null); Spacer(Modifier.width(6.dp))
                        Text("In library")
                    }
                } else {
                    Button(onClick = { vm.setInLibrary(m.id, true) },
                        modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp))
                        Text("Add to library")
                    }
                }
                IconButton(onClick = { vm.toggleFavorite(m.id) }) {
                    Icon(if (m.favorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (m.favorite) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(onClick = {
                    scope.launch { vm.resumeChapterId(m.id)?.let { nav.navigate("reader/$it") } }
                }, enabled = unread > 0, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.PlayArrow, null); Spacer(Modifier.width(6.dp))
                    Text("Resume")
                }
            }
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                m.genres.split(",").forEach { g -> AssistChip(onClick = {}, label = { Text(g.trim()) }) }
            }
            Text(m.description, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            Text("Chapters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(4.dp))
        }
        items(chapters, key = { it.id }) { ch ->
            Card(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { nav.navigate("reader/${ch.id}") },
                colors = CardDefaults.cardColors(
                    containerColor = if (ch.read)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { vm.markRead(ch.id, m.id, !ch.read) }) {
                        Icon(Icons.Filled.CheckCircle,
                            contentDescription = if (ch.read) "Mark unread" else "Mark read",
                            tint = if (ch.read) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Ch. ${ch.number.toInt()} — ${ch.title}",
                            style = MaterialTheme.typography.bodyMedium)
                        Text("${ch.pages} pages  •  ${timeAgo(ch.dateAdded)}" +
                            if (ch.bookmark) "  •  🔖 bookmarked" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { vm.toggleBookmark(ch.id) }) {
                        Icon(if (ch.bookmark) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (ch.bookmark) MaterialTheme.colorScheme.tertiary
                                   else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
