package net.zzbuaoye.aeryo.bookmarks.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkEntity>,
    onUrlSelected: (String) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "收藏",
                largeTitle = "我的书签",
                subtitle = "${bookmarks.size} 条记录",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                }
            )
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        AnimatedContent(
            targetState = bookmarks.isEmpty(),
            transitionSpec = {
                (
                    fadeIn(animationSpec = tween(220)) +
                        scaleIn(
                            initialScale = 0.97f,
                            animationSpec = folmeSpring(damping = 0.9f, response = 0.38f)
                        )
                    ).togetherWith(
                    fadeOut(animationSpec = tween(140)) +
                        scaleOut(targetScale = 0.97f, animationSpec = tween(160))
                )
            },
            label = "bookmark-content",
            modifier = Modifier.fillMaxSize()
        ) { isEmpty ->
            if (isEmpty) {
                EmptyState(
                    message = "还没有收藏任何网页",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding())
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bookmarks, key = { it.id }) { item ->
                        RecordCard(
                            title = item.title.ifBlank { item.url },
                            url = item.url,
                            time = formatTime(item.addedTime),
                            onClick = { onUrlSelected(item.url) },
                            onDelete = { onDeleteBookmark(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordCard(
    title: String,
    url: String,
    time: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = onClick
    ) {
        BasicComponent(
            title = title,
            summary = "$url\n$time",
            startAction = {
                Box(
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.take(1).uppercase(),
                        color = MiuixTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            endActions = {
                IconButton(onClick = onDelete) {
                    Icon(
                        MiuixIcons.Delete,
                        contentDescription = "删除",
                        tint = MiuixTheme.colorScheme.error
                    )
                }
            }
        )
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "空空如也",
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 14.sp
            )
        }
    }
}

private fun formatTime(timeMillis: Long): String {
    return SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(timeMillis))
}
