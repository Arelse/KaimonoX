package com.kaimono.app.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaimono.app.data.MangaEntity

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MangaCard(
    manga: MangaEntity,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.height(170.dp).fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)) {
            Cover(seed = manga.coverSeed, title = manga.title, modifier = Modifier.fillMaxSize())
            if (manga.favorite) {
                Icon(Icons.Filled.Star, contentDescription = "Favorite",
                    tint = Color(0xFFFFC107), modifier = Modifier
                        .align(Alignment.TopStart).padding(6.dp).size(20.dp))
            }
            if (manga.inLibrary) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "In library",
                    tint = Color(0xFF4CAF50), modifier = Modifier
                        .align(Alignment.TopEnd).padding(6.dp).size(20.dp))
            }
            badge?.let {
                Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)) {
                    Text(it, fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Medium)
                }
            }
        }
        Text(manga.title, style = MaterialTheme.typography.labelMedium,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp))
    }
}
