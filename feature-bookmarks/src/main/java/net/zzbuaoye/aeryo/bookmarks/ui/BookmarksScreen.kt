package net.zzbuaoye.aeryo.bookmarks.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.bookmarks.data.BookmarkEntity
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.FavoritesFill
import top.yukonga.miuix.kmp.icon.extended.Folder
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkEntity>,
    favorites: List<BookmarkEntity> = emptyList(),
    onUrlSelected: (String) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    onBack: () -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "书签与收藏",
                largeTitle = "书签与收藏",
                subtitle = if (selectedSection == 0) {
                    "${bookmarks.size} 个书签"
                } else {
                    "${favorites.size} 个收藏"
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                }
            )
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            SavedTypeSelector(
                selectedSection = selectedSection,
                bookmarkCount = bookmarks.size,
                favoriteCount = favorites.size,
                onSelected = { selectedSection = it }
            )

            AnimatedContent(
                targetState = selectedSection,
                transitionSpec = {
                    (
                        fadeIn(animationSpec = tween(220)) +
                            scaleIn(
                                initialScale = 0.98f,
                                animationSpec = folmeSpring(damping = 0.9f, response = 0.38f)
                            )
                        ).togetherWith(
                        fadeOut(animationSpec = tween(140)) +
                            scaleOut(targetScale = 0.98f, animationSpec = tween(160))
                    )
                },
                label = "saved-section-content",
                modifier = Modifier.weight(1f)
            ) { section ->
                val items = if (section == 0) bookmarks else favorites
                if (items.isEmpty()) {
                    SavedEmptyState(
                        isBookmarks = section == 0,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SavedItemsList(
                        sections = savedSections(items, isBookmarks = section == 0),
                        onUrlSelected = onUrlSelected,
                        onDeleteBookmark = onDeleteBookmark,
                        bottomPadding = padding.calculateBottomPadding() + 20.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedTypeSelector(
    selectedSection: Int,
    bookmarkCount: Int,
    favoriteCount: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SavedTypeCard(
            title = "书签",
            count = bookmarkCount,
            icon = MiuixIcons.Folder,
            selected = selectedSection == 0,
            onClick = { onSelected(0) },
            modifier = Modifier.weight(1f)
        )
        SavedTypeCard(
            title = "收藏",
            count = favoriteCount,
            icon = MiuixIcons.FavoritesFill,
            selected = selectedSection == 1,
            onClick = { onSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SavedTypeCard(
    title: String,
    count: Int,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) {
        MiuixTheme.colorScheme.onPrimaryContainer
    } else {
        MiuixTheme.colorScheme.onSurface
    }
    Card(
        modifier = modifier.height(76.dp),
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(
            color = if (selected) {
                MiuixTheme.colorScheme.primaryContainer
            } else {
                MiuixTheme.colorScheme.surfaceContainer
            }
        ),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MiuixTheme.colorScheme.primary else contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = contentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$count 项",
                    color = if (selected) {
                        MiuixTheme.colorScheme.primary
                    } else {
                        MiuixTheme.colorScheme.onSurfaceVariantSummary
                    },
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SavedItemsList(
    sections: List<SavedSection>,
    onUrlSelected: (String) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 2.dp,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sections.forEach { section ->
            item(key = "saved-${section.title}") {
                Column {
                    SmallTitle(text = section.title)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = PaddingValues(0.dp),
                        colors = CardDefaults.defaultColors(
                            color = MiuixTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        section.items.forEach { item ->
                            SavedItemRow(
                                item = item,
                                onClick = { onUrlSelected(item.url) },
                                onDelete = { onDeleteBookmark(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedItemRow(
    item: BookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val title = item.title.ifBlank { displayHost(item.url).ifBlank { item.url } }
    val host = displayHost(item.url).ifBlank { item.url }

    BasicComponent(
        title = title,
        summary = "$host · ${formatTime(item.addedTime)}",
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        startAction = {
            SavedFavicon(url = item.url, fallbackText = title)
        },
        endActions = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = MiuixIcons.Delete,
                    contentDescription = "删除 $title",
                    tint = MiuixTheme.colorScheme.error
                )
            }
        },
        onClick = onClick
    )
}

@Composable
private fun SavedFavicon(url: String, fallbackText: String) {
    val favicon by produceState<ImageBitmap?>(initialValue = null, key1 = url) {
        value = FaviconLoader.load(url)?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    }
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(MiuixTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        if (favicon != null) {
            Image(
                bitmap = favicon!!,
                contentDescription = "${displayHost(url)} 网站图标",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
            )
        } else {
            Text(
                text = fallbackText.take(1).uppercase(),
                color = MiuixTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SavedEmptyState(
    isBookmarks: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 14.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(0.dp),
            colors = CardDefaults.defaultColors(
                color = MiuixTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBookmarks) MiuixIcons.Folder else MiuixIcons.FavoritesFill,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isBookmarks) "暂无书签" else "暂无收藏",
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBookmarks) {
                        "从菜单中添加书签，稍后可以快速打开"
                    } else {
                        "点击地址栏末尾的心标收藏当前网页"
                    },
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private data class SavedSection(
    val title: String,
    val items: List<BookmarkEntity>
)

private fun savedSections(items: List<BookmarkEntity>, isBookmarks: Boolean): List<SavedSection> {
    if (!isBookmarks) return listOf(SavedSection("收藏网页", items))
    return items
        .groupBy { item -> item.folder.ifBlank { "默认书签" } }
        .map { (folder, folderItems) -> SavedSection(folder, folderItems) }
}

private fun displayHost(url: String): String {
    return runCatching {
        Uri.parse(url).host?.removePrefix("www.").orEmpty()
    }.getOrDefault("")
}

private fun formatTime(timeMillis: Long): String {
    return SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(timeMillis))
}
