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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.bookmarks.data.HistoryEntity
import net.zzbuaoye.aeryo.bookmarks.data.PrivateHistoryEntity
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Link
import top.yukonga.miuix.kmp.icon.extended.Lock
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    history: List<HistoryEntity>,
    privateHistory: List<PrivateHistoryEntity> = emptyList(),
    isIncognito: Boolean = false,
    onUrlSelected: (String) -> Unit,
    onDeleteHistory: (HistoryEntity) -> Unit,
    onDeletePrivateHistory: (PrivateHistoryEntity) -> Unit = {},
    onFaviconLoaded: (HistoryEntity, ByteArray) -> Unit,
    onClearAllHistory: () -> Unit,
    onClearAllPrivateHistory: () -> Unit = {},
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (isIncognito) 0 else 0) }
    var query by remember { mutableStateOf("") }
    var searchExpanded by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var actionItem by remember { mutableStateOf<HistoryEntity?>(null) }
    var actionPrivateItem by remember { mutableStateOf<PrivateHistoryEntity?>(null) }

    val activeTab = if (isIncognito) selectedTab else 0

    val activeHistoryList = remember(history, privateHistory, activeTab) {
        if (activeTab == 0) history else privateHistory.map {
            HistoryEntity(
                id = it.id,
                title = it.title,
                url = it.url,
                visitTime = it.visitTime,
                favicon = it.favicon
            )
        }
    }

    val filteredHistory = remember(activeHistoryList, query) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            activeHistoryList
        } else {
            activeHistoryList.filter { item ->
                item.title.contains(normalizedQuery, ignoreCase = true) ||
                    item.url.contains(normalizedQuery, ignoreCase = true) ||
                    hostOf(item.url).contains(normalizedQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = if (activeTab == 1) "私密历史记录" else "历史记录",
                    largeTitle = "浏览历史",
                    subtitle = if (query.isBlank()) {
                        "${activeHistoryList.size} 条记录"
                    } else {
                        "找到 ${filteredHistory.size} 条记录"
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                    actions = {
                        if (activeHistoryList.isNotEmpty()) {
                            IconButton(onClick = { showClearConfirmation = true }) {
                                Icon(MiuixIcons.Delete, contentDescription = "清空历史")
                            }
                        }
                    }
                )
                if (isIncognito) {
                    // Segment Bar (常规 / 私密历史) - 仅在隐私模式下展示
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MiuixTheme.colorScheme.surfaceContainer)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedTab == 0) MiuixTheme.colorScheme.primaryContainer else MiuixTheme.colorScheme.surfaceContainer
                                )
                                .clickable { selectedTab = 0 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "常规历史 (${history.size})",
                                color = if (selectedTab == 0) MiuixTheme.colorScheme.onPrimaryContainer else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedTab == 1) MiuixTheme.colorScheme.primaryContainer else MiuixTheme.colorScheme.surfaceContainer
                                )
                                .clickable { selectedTab = 1 },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = MiuixIcons.Lock,
                                    contentDescription = null,
                                    tint = if (selectedTab == 1) MiuixTheme.colorScheme.onPrimaryContainer else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "私密历史 (${privateHistory.size})",
                                    color = if (selectedTab == 1) MiuixTheme.colorScheme.onPrimaryContainer else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
            ) {
                SearchBar(
                    inputField = {
                        InputField(
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = {},
                            expanded = searchExpanded,
                            onExpandedChange = { searchExpanded = it },
                            label = "搜索标题或网址"
                        )
                    },
                    onExpandedChange = { searchExpanded = it },
                    modifier = if (searchExpanded) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
                    expanded = searchExpanded,
                    outsideEndAction = {
                        Text(
                            text = "取消",
                            color = MiuixTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { searchExpanded = false }
                        )
                    }
                ) {
                    HistoryList(
                        history = filteredHistory,
                        onUrlSelected = onUrlSelected,
                        onMoreRequested = { item ->
                            if (selectedTab == 1) {
                                actionPrivateItem = privateHistory.find { it.id == item.id }
                            } else {
                                actionItem = item
                            }
                        },
                        onFaviconLoaded = onFaviconLoaded,
                        emptyMessage = if (query.isBlank()) "暂无历史记录" else "没有匹配的历史记录",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (!searchExpanded) {
                    HistoryList(
                        history = activeHistoryList,
                        onUrlSelected = onUrlSelected,
                        onMoreRequested = { item ->
                            if (selectedTab == 1) {
                                actionPrivateItem = privateHistory.find { it.id == item.id }
                            } else {
                                actionItem = item
                            }
                        },
                        onFaviconLoaded = onFaviconLoaded,
                        emptyMessage = if (selectedTab == 1) "暂无保留的隐私历史网页" else "还没有浏览历史",
                        modifier = Modifier.weight(1f),
                        bottomPadding = padding.calculateBottomPadding() + 16.dp
                    )
                }
            }

            OverlayDialog(
                show = showClearConfirmation,
                title = if (selectedTab == 1) "清空所有私密历史记录？" else "清空浏览历史？",
                summary = "此操作会完全删除所有相关记录，且无法撤销。",
                onDismissRequest = { showClearConfirmation = false }
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        text = "取消",
                        onClick = { showClearConfirmation = false },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        text = "清空",
                        onClick = {
                            if (selectedTab == 1) {
                                onClearAllPrivateHistory()
                            } else {
                                onClearAllHistory()
                            }
                            showClearConfirmation = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }

            // 常规历史菜单
            OverlayBottomSheet(
                show = actionItem != null,
                title = actionItem?.let(::displayTitle),
                onDismissRequest = { actionItem = null }
            ) {
                val selectedItem = actionItem
                BasicComponent(
                    title = "打开网页",
                    summary = selectedItem?.url,
                    startAction = {
                        Icon(
                            imageVector = MiuixIcons.Link,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    onClick = {
                        selectedItem?.let { onUrlSelected(it.url) }
                        actionItem = null
                    }
                )
                BasicComponent(
                    title = "删除此条记录",
                    summary = "仅从本机浏览历史中移除",
                    startAction = {
                        Icon(
                            imageVector = MiuixIcons.Delete,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    onClick = {
                        selectedItem?.let(onDeleteHistory)
                        actionItem = null
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 私密历史菜单
            OverlayBottomSheet(
                show = actionPrivateItem != null,
                title = actionPrivateItem?.title,
                onDismissRequest = { actionPrivateItem = null }
            ) {
                val selectedItem = actionPrivateItem
                BasicComponent(
                    title = "打开私密网页",
                    summary = selectedItem?.url,
                    startAction = {
                        Icon(
                            imageVector = MiuixIcons.Link,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    onClick = {
                        selectedItem?.let { onUrlSelected(it.url) }
                        actionPrivateItem = null
                    }
                )
                BasicComponent(
                    title = "删除此私密记录",
                    summary = "移除此保留的私密历史网页",
                    startAction = {
                        Icon(
                            imageVector = MiuixIcons.Delete,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    onClick = {
                        selectedItem?.let(onDeletePrivateHistory)
                        actionPrivateItem = null
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HistoryList(
    history: List<HistoryEntity>,
    onUrlSelected: (String) -> Unit,
    onMoreRequested: (HistoryEntity) -> Unit,
    onFaviconLoaded: (HistoryEntity, ByteArray) -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    bottomPadding: androidx.compose.ui.unit.Dp = 16.dp
) {
    val sections = remember(history) { historySections(history) }
    AnimatedContent(
        targetState = history.isEmpty(),
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
        label = "history-content",
        modifier = modifier.fillMaxWidth()
    ) { isEmpty ->
        if (isEmpty) {
            HistoryEmptyState(
                message = emptyMessage,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 4.dp,
                    bottom = bottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEach { section ->
                    item(key = "section-${section.date}") {
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
                                    HistoryRow(
                                        item = item,
                                        onClick = { onUrlSelected(item.url) },
                                        onMore = { onMoreRequested(item) },
                                        onFaviconLoaded = onFaviconLoaded
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    item: HistoryEntity,
    onClick: () -> Unit,
    onMore: () -> Unit,
    onFaviconLoaded: (HistoryEntity, ByteArray) -> Unit
) {
    val title = displayTitle(item)
    val host = hostOf(item.url).ifBlank { item.url }
    BasicComponent(
        title = title,
        summary = "$host · ${formatVisitTime(item.visitTime)}",
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        startAction = {
            HistoryFavicon(
                item = item,
                onFaviconLoaded = onFaviconLoaded
            )
        },
        endActions = {
            IconButton(onClick = onMore) {
                Icon(
                    imageVector = MiuixIcons.More,
                    contentDescription = "更多操作",
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        },
        onClick = onClick
    )
}

@Composable
private fun HistoryFavicon(
    item: HistoryEntity,
    onFaviconLoaded: (HistoryEntity, ByteArray) -> Unit
) {
    val latestOnFaviconLoaded by rememberUpdatedState(onFaviconLoaded)
    val storedFavicon = remember(item.id, item.favicon?.contentHashCode()) {
        item.favicon
            ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            ?.asImageBitmap()
    }
    val favicon by produceState(
        initialValue = storedFavicon,
        key1 = item.id,
        key2 = item.url,
        key3 = item.favicon?.contentHashCode()
    ) {
        if (value == null) {
            FaviconLoader.load(item.url)?.let { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                if (bitmap != null) {
                    value = bitmap
                    latestOnFaviconLoaded(item, bytes)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MiuixTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        if (favicon != null) {
            Image(
                bitmap = favicon!!,
                contentDescription = "${hostOf(item.url)} 网站图标",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
            )
        } else {
            Text(
                text = hostOf(item.url).firstOrNull()?.uppercase() ?: "W",
                color = MiuixTheme.colorScheme.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HistoryEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "暂无记录",
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

private data class HistorySection(
    val date: LocalDate,
    val title: String,
    val items: List<HistoryEntity>
)

private fun historySections(history: List<HistoryEntity>): List<HistorySection> {
    val zoneId = ZoneId.systemDefault()
    val today = LocalDate.now(zoneId)
    val yesterday = today.minusDays(1)
    return history
        .sortedByDescending(HistoryEntity::visitTime)
        .groupBy { Instant.ofEpochMilli(it.visitTime).atZone(zoneId).toLocalDate() }
        .map { (date, items) ->
            val title = when (date) {
                today -> "今天"
                yesterday -> "昨天"
                else -> date.format(java.time.format.DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.getDefault()))
            }
            HistorySection(date = date, title = title, items = items)
        }
}

private fun hostOf(url: String): String = runCatching {
    Uri.parse(url).host.orEmpty().removePrefix("www.")
}.getOrDefault("")

private fun displayTitle(item: HistoryEntity): String {
    val host = hostOf(item.url)
    val title = item.title.trim()
    return if (
        title.isBlank() ||
        title == "新标签页" ||
        title == "主页" ||
        title == "about:blank"
    ) {
        host.ifBlank { item.url }
    } else {
        title
    }
}

private fun formatVisitTime(timeMillis: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timeMillis))
}
