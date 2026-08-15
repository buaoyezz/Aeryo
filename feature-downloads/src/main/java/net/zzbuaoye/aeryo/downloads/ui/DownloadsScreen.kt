package net.zzbuaoye.aeryo.downloads.ui

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.Archive
import compose.icons.tablericons.BrandAndroid
import compose.icons.tablericons.Code
import compose.icons.tablericons.Copy
import compose.icons.tablericons.File
import compose.icons.tablericons.FileText
import compose.icons.tablericons.ExternalLink
import compose.icons.tablericons.Movie
import compose.icons.tablericons.Music
import compose.icons.tablericons.Photo
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Share
import kotlin.math.pow
import net.zzbuaoye.aeryo.downloads.model.DownloadBackend
import net.zzbuaoye.aeryo.downloads.model.DownloadItem
import net.zzbuaoye.aeryo.core.ui.aeryoBackdropSource
import net.zzbuaoye.aeryo.core.ui.aeryoBlurEffect
import net.zzbuaoye.aeryo.core.ui.aeryoCardColor
import net.zzbuaoye.aeryo.core.ui.aeryoTopBarColor
import net.zzbuaoye.aeryo.core.ui.aeryoWindowColor
import net.zzbuaoye.aeryo.core.ui.rememberAeryoWindowBackdrop
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Download
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    onOpenDownload: (DownloadItem) -> Unit,
    onDeleteDownload: (DownloadItem) -> Unit,
    onPauseDownload: (DownloadItem) -> Unit,
    onResumeDownload: (DownloadItem) -> Unit,
    onBack: () -> Unit
) {
    var actionMenuItem by remember { mutableStateOf<DownloadItem?>(null) }
    var actionMenuVisible by remember { mutableStateOf(false) }
    val activeDownloads = downloads.filter(DownloadItem::isActiveDownload)
    val pausedDownloads = downloads.filter { it.status == DownloadManager.STATUS_PAUSED }
    val finishedDownloads = downloads.filter {
        !it.isActiveDownload() && it.status != DownloadManager.STATUS_PAUSED
    }
    val totalSpeed = activeDownloads.sumOf(DownloadItem::speedBytesPerSecond)
    val scrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberAeryoWindowBackdrop()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.aeryoBlurEffect(topBarBackdrop),
                color = topBarBackdrop.aeryoTopBarColor(),
                title = "下载管理",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = aeryoWindowColor(),
    ) { padding ->
        AnimatedContent(
            targetState = downloads.isEmpty(),
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
            label = "download-content",
            modifier = Modifier
                .fillMaxSize()
                .aeryoBackdropSource(topBarBackdrop)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { isEmpty ->
            if (isEmpty) {
                EmptyDownloads(modifier = Modifier.fillMaxSize().padding(padding))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollEndHaptic()
                        .overScrollVertical(),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "summary") {
                        DownloadSummaryCard(
                            activeCount = activeDownloads.size,
                            totalCount = downloads.size,
                            totalSpeed = totalSpeed
                        )
                    }

                    if (activeDownloads.isNotEmpty()) {
                        item(key = "active-title") {
                            SectionTitle("正在下载", "${activeDownloads.size} 个任务")
                        }
                        items(activeDownloads, key = DownloadItem::stableKey) { item ->
                            DownloadCard(
                                item = item,
                                onOpen = { onOpenDownload(item) },
                                onDelete = { onDeleteDownload(item) },
                                onPause = { onPauseDownload(item) },
                                onResume = { onResumeDownload(item) },
                                onShowMenu = {
                                    actionMenuItem = item
                                    actionMenuVisible = true
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

                    if (pausedDownloads.isNotEmpty()) {
                        item(key = "paused-title") {
                            SectionTitle("已暂停", "${pausedDownloads.size} 个任务")
                        }
                        items(pausedDownloads, key = DownloadItem::stableKey) { item ->
                            DownloadCard(
                                item = item,
                                onOpen = { onOpenDownload(item) },
                                onDelete = { onDeleteDownload(item) },
                                onPause = { onPauseDownload(item) },
                                onResume = { onResumeDownload(item) },
                                onShowMenu = {
                                    actionMenuItem = item
                                    actionMenuVisible = true
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

                    if (finishedDownloads.isNotEmpty()) {
                        item(key = "finished-title") {
                            SectionTitle("已完成与其他任务", "${finishedDownloads.size} 个任务")
                        }
                        items(finishedDownloads, key = DownloadItem::stableKey) { item ->
                            DownloadCard(
                                item = item,
                                onOpen = { onOpenDownload(item) },
                                onDelete = { onDeleteDownload(item) },
                                onPause = { onPauseDownload(item) },
                                onResume = { onResumeDownload(item) },
                                onShowMenu = {
                                    actionMenuItem = item
                                    actionMenuVisible = true
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }

    DownloadTaskActionSheet(
        show = actionMenuVisible,
        item = actionMenuItem,
        onDismissRequest = { actionMenuVisible = false },
        onDismissFinished = { actionMenuItem = null },
        onOpen = {
            actionMenuItem?.let(onOpenDownload)
            actionMenuVisible = false
        },
        onPause = {
            actionMenuItem?.let(onPauseDownload)
            actionMenuVisible = false
        },
        onResume = {
            actionMenuItem?.let(onResumeDownload)
            actionMenuVisible = false
        },
        onDelete = {
            actionMenuItem?.let(onDeleteDownload)
            actionMenuVisible = false
        }
    )
}

private data class FileTypeVisual(
    val icon: ImageVector,
    val iconColor: Color,
    val containerColor: Color,
    val typeName: String
)

private fun getFileTypeVisual(fileName: String, mimeType: String?): FileTypeVisual {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    val mime = mimeType?.lowercase().orEmpty()
    return when {
        ext == "apk" || mime.contains("vnd.android.package-archive") -> FileTypeVisual(
            icon = TablerIcons.BrandAndroid,
            iconColor = Color(0xFF10B981),
            containerColor = Color(0xFF10B981).copy(alpha = 0.12f),
            typeName = "APK"
        )
        ext in listOf("mp4", "mkv", "mov", "avi", "flv", "webm", "m4s", "m3u8", "ts", "3gp") || mime.startsWith("video/") -> FileTypeVisual(
            icon = TablerIcons.Movie,
            iconColor = Color(0xFF8B5CF6),
            containerColor = Color(0xFF8B5CF6).copy(alpha = 0.12f),
            typeName = "视频"
        )
        ext in listOf("mp3", "flac", "wav", "aac", "m4a", "ogg", "wma", "opus") || mime.startsWith("audio/") -> FileTypeVisual(
            icon = TablerIcons.Music,
            iconColor = Color(0xFFF59E0B),
            containerColor = Color(0xFFF59E0B).copy(alpha = 0.12f),
            typeName = "音频"
        )
        ext in listOf("jpg", "jpeg", "png", "webp", "gif", "svg", "bmp", "ico") || mime.startsWith("image/") -> FileTypeVisual(
            icon = TablerIcons.Photo,
            iconColor = Color(0xFF3B82F6),
            containerColor = Color(0xFF3B82F6).copy(alpha = 0.12f),
            typeName = "图片"
        )
        ext in listOf("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso", "7zip") || mime.contains("zip") || mime.contains("compressed") -> FileTypeVisual(
            icon = TablerIcons.Archive,
            iconColor = Color(0xFFD97706),
            containerColor = Color(0xFFD97706).copy(alpha = 0.12f),
            typeName = "压缩包"
        )
        ext in listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "epub", "md", "csv") || mime.contains("pdf") || mime.contains("document") -> FileTypeVisual(
            icon = TablerIcons.FileText,
            iconColor = Color(0xFFEF4444),
            containerColor = Color(0xFFEF4444).copy(alpha = 0.12f),
            typeName = "文档"
        )
        ext in listOf("kt", "java", "py", "c", "cpp", "h", "js", "ts", "html", "css", "json", "xml", "sh") -> FileTypeVisual(
            icon = TablerIcons.Code,
            iconColor = Color(0xFF06B6D4),
            containerColor = Color(0xFF06B6D4).copy(alpha = 0.12f),
            typeName = "代码"
        )
        else -> FileTypeVisual(
            icon = TablerIcons.File,
            iconColor = Color(0xFF64748B),
            containerColor = Color(0xFF64748B).copy(alpha = 0.12f),
            typeName = "文件"
        )
    }
}

@Composable
private fun DownloadSummaryCard(
    activeCount: Int,
    totalCount: Int,
    totalSpeed: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(color = aeryoCardColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiuixTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MiuixIcons.Download,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = if (activeCount > 0) "下载正在进行" else "下载任务",
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = when {
                        activeCount > 0 && totalSpeed > 0L -> "当前总速度 ${formatRate(totalSpeed)}"
                        activeCount > 0 -> "正在连接下载源"
                        else -> "共 $totalCount 个任务"
                    },
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            if (activeCount > 0) {
                Text(
                    text = "$activeCount 个进行中",
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, summary: String) {
    SmallTitle(text = "$title · $summary")
}

@Composable
private fun DownloadCard(
    item: DownloadItem,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onShowMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val complete = item.status == DownloadManager.STATUS_SUCCESSFUL
    val active = item.isActiveDownload()
    val paused = item.status == DownloadManager.STATUS_PAUSED
    val failed = item.status == DownloadManager.STATUS_FAILED
    val progress = item.progress()
    val visual = getFileTypeVisual(item.fileName, item.mimeType)

    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(color = aeryoCardColor()),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = { if (complete) onOpen() },
        onLongPress = onShowMenu
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(visual.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = null,
                        tint = visual.iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 11.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = item.fileName,
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${visual.typeName} · ${downloadStatusText(item)}",
                        color = if (failed) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
                when {
                    complete -> DownloadActionButton(
                        icon = TablerIcons.ExternalLink,
                        contentDescription = "打开文件",
                        onClick = onOpen
                    )
                    item.backend == DownloadBackend.BUILT_IN && active -> DownloadActionButton(
                        icon = TablerIcons.PlayerPause,
                        contentDescription = "暂停下载",
                        onClick = onPause
                    )
                    item.backend == DownloadBackend.BUILT_IN && (paused || failed) -> DownloadActionButton(
                        icon = TablerIcons.PlayerPlay,
                        contentDescription = "继续下载",
                        onClick = onResume
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        MiuixIcons.Delete,
                        contentDescription = "删除任务",
                        tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (progress != null) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                )
            }

            Text(
                text = progressDescription(item),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (progress == null) 6.dp else 4.dp)
            )
        }
    }
}

@Composable
private fun DownloadTaskActionSheet(
    show: Boolean,
    item: DownloadItem?,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
    onOpen: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit
) {
    if (item == null) return
    val context = LocalContext.current
    val complete = item.status == DownloadManager.STATUS_SUCCESSFUL
    val active = item.isActiveDownload()
    val canResume = item.backend == DownloadBackend.BUILT_IN &&
        (item.status == DownloadManager.STATUS_PAUSED || item.status == DownloadManager.STATUS_FAILED)

    OverlayBottomSheet(
        show = show,
        title = null,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val visual = getFileTypeVisual(item.fileName, item.mimeType)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(visual.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = null,
                        tint = visual.iconColor,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = item.fileName,
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.url,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = MiuixIcons.Close,
                        contentDescription = "关闭",
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                if (complete) {
                    DownloadMenuItem(
                        icon = TablerIcons.ExternalLink,
                        title = "打开文件",
                        onClick = onOpen
                    )
                } else if (item.backend == DownloadBackend.BUILT_IN && active) {
                    DownloadMenuItem(
                        icon = TablerIcons.PlayerPause,
                        title = "暂停下载",
                        onClick = onPause
                    )
                } else if (canResume) {
                    DownloadMenuItem(
                        icon = TablerIcons.PlayerPlay,
                        title = "继续下载",
                        onClick = onResume
                    )
                }
                DownloadMenuItem(
                    icon = TablerIcons.Copy,
                    title = "复制下载链接",
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Download URL", item.url))
                        Toast.makeText(context, "已复制下载链接", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    }
                )
                DownloadMenuItem(
                    icon = TablerIcons.Share,
                    title = "分享下载链接",
                    onClick = {
                        runCatching {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, item.fileName)
                                putExtra(Intent.EXTRA_TEXT, item.url)
                            }
                            context.startActivity(Intent.createChooser(intent, "分享下载链接"))
                        }.onFailure {
                            Toast.makeText(context, "没有可用的分享应用", Toast.LENGTH_SHORT).show()
                        }
                        onDismissRequest()
                    }
                )
                DownloadMenuItem(
                    icon = TablerIcons.FileText,
                    title = "复制文件名",
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("File name", item.fileName))
                        Toast.makeText(context, "已复制文件名", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                DownloadMenuItem(
                    icon = MiuixIcons.Delete,
                    title = "删除任务与文件",
                    tint = MiuixTheme.colorScheme.error,
                    onClick = onDelete
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun DownloadMenuItem(
    icon: ImageVector,
    title: String,
    tint: Color = MiuixTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            color = tint,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun DownloadActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(30.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun EmptyDownloads(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(bottom = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.11f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MiuixIcons.Download,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(27.dp)
                )
            }
            Text(
                text = "暂无下载任务",
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "网页请求下载时，会先由你确认后再开始",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, start = 32.dp, end = 32.dp)
            )
        }
    }
}

private fun DownloadItem.isActiveDownload(): Boolean =
    status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING

private fun DownloadItem.progress(): Float? =
    totalBytes.takeIf { it > 0L }
        ?.let { total -> (downloadedBytes.toFloat() / total.toFloat()).coerceIn(0f, 1f) }

private fun downloadStatusText(item: DownloadItem): String = when (item.status) {
    DownloadManager.STATUS_RUNNING -> {
        if (item.speedBytesPerSecond > 0L) "下载中 · ${formatRate(item.speedBytesPerSecond)}" else "正在下载"
    }
    DownloadManager.STATUS_PENDING -> "等待开始"
    DownloadManager.STATUS_PAUSED -> "已暂停"
    DownloadManager.STATUS_SUCCESSFUL -> "下载完成"
    DownloadManager.STATUS_FAILED -> item.failureReason?.let { "下载失败 · $it" } ?: "下载失败"
    else -> "准备下载"
}

private fun progressDescription(item: DownloadItem): String {
    val amount = if (item.totalBytes > 0L) {
        "${formatBytes(item.downloadedBytes)} / ${formatBytes(item.totalBytes)}"
    } else {
        "已下载 ${formatBytes(item.downloadedBytes)}"
    }
    if (item.status == DownloadManager.STATUS_RUNNING && item.speedBytesPerSecond > 0L && item.totalBytes > item.downloadedBytes) {
        val remainingSeconds = (item.totalBytes - item.downloadedBytes) / item.speedBytesPerSecond
        return "$amount · 剩余约 ${formatDuration(remainingSeconds)}"
    }
    return amount
}

internal fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val labels = arrayOf("B", "KB", "MB", "GB", "TB")
    val index = (kotlin.math.ln(bytes.toDouble()) / kotlin.math.ln(1024.0))
        .toInt()
        .coerceIn(0, labels.lastIndex)
    return "%.1f %s".format(bytes / 1024.0.pow(index.toDouble()), labels[index])
}

private fun formatRate(bytesPerSecond: Long): String = "${formatBytes(bytesPerSecond)}/s"

private fun formatDuration(seconds: Long): String = when {
    seconds < 60L -> "${seconds.coerceAtLeast(1L)} 秒"
    seconds < 3_600L -> "${seconds / 60L} 分"
    else -> "${seconds / 3_600L} 小时"
}
