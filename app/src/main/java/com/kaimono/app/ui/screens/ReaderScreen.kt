package com.kaimono.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kaimono.app.ui.AppViewModel
import com.kaimono.app.ui.coverColors

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(vm: AppViewModel, nav: NavHostController, chapterId: String) {
    val chapter by vm.chapter(chapterId).collectAsState(initial = null)
    val manga by vm.mangaForChapter(chapterId).collectAsState(initial = null)
    var neighbors by remember { mutableStateOf<Pair<String?, String?>?>(null) }

    LaunchedEffect(chapterId) { neighbors = vm.neighborChapterIds(chapterId) }

    val ch = chapter ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val mg = manga

    DisposableEffect(chapterId) {
        onDispose { mg?.let { vm.markRead(chapterId, it.id, true) } }
    }

    val pagerState = rememberPagerState(pageCount = { ch.pages })

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Column(Modifier.weight(1f)) {
                Text(mg?.title ?: "", style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text("Ch. ${ch.number.toInt()} — ${ch.title}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Text("${pagerState.currentPage + 1} / ${ch.pages}",
                style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(end = 12.dp))
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val (c1, c2) = coverColors((mg?.coverSeed ?: 0) + page)
            Box(modifier = Modifier.fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        c2.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.surfaceVariant)),
                    shape = MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(mg?.title ?: "", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = c1, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Text("Page ${page + 1}", style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("Demo chapter content\n(real page images stream in from the source extension)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton(
                onClick = { neighbors?.first?.let { nav.navigate("reader/$it") } },
                enabled = neighbors?.first != null, modifier = Modifier.weight(1f)) {
                Icon(Icons.AutoMirrored.Filled.NavigateBefore, null)
                Spacer(Modifier.size(4.dp)); Text("Prev chapter")
            }
            FilledTonalButton(
                onClick = { neighbors?.second?.let { nav.navigate("reader/$it") } },
                enabled = neighbors?.second != null, modifier = Modifier.weight(1f)) {
                Text("Next chapter"); Spacer(Modifier.size(4.dp))
                Icon(Icons.AutoMirrored.Filled.NavigateNext, null)
            }
        }
    }
}

