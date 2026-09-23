package com.kaimono.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.kaimono.app.ui.timeAgo

@Composable
fun HistoryScreen(vm: AppViewModel, nav: NavHostController) {
    val history by vm.history.collectAsState()

    if (history.isEmpty()) {
        EmptyState(icon = Icons.Filled.History, title = "No reading history",
            message = "Chapters you read will show up here so you can jump back in.",
            actionLabel = "Open library", onAction = { nav.navigate("library") })
        return
    }

    LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
        items(history, key = { it.manga.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                .clickable { nav.navigate("reader/${item.chapterId}") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                Row(modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Cover(item.manga.coverSeed, item.manga.title,
                        Modifier.size(width = 44.dp, height = 62.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.manga.title, style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold, maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        Text("Ch. ${item.chapterNumber.toInt()} — ${item.chapterTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(timeAgo(item.readAt), style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { nav.navigate("reader/${item.chapterId}") }) {
                        Icon(Icons.Filled.PlayArrow, "Resume") }
                    IconButton(onClick = { vm.removeHistoryEntry(item.manga.id) }) {
                        Icon(Icons.Filled.Delete, "Remove",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
    }
}
