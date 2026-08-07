package net.zzbuaoye.aeryo.downloads.ui

import android.app.DownloadManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Download
import top.yukonga.miuix.kmp.icon.extended.Ok
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
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
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
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MiuixTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MiuixIcons.Download,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    text = if (activeCount > 0) "下载正在进行" else "下载任务",
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = when {
                        activeCount > 0 && totalSpeed > 0L -> "当前总速度 ${formatRate(totalSpeed)}"
                        activeCount > 0 -> "正在连接下载源"
                        else -> "共 $totalCount 个任务"
                    },
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Text(
                text = activeCount.toString(),
                color = MiuixTheme.colorScheme.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
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
    modifier: Modifier = Modifier
) {
    val complete = item.status == DownloadManager.STATUS_SUCCESSFUL
    val active = item.isActiveDownload()
    val paused = item.status == DownloadManager.STATUS_PAUSED
    val failed = item.status == DownloadManager.STATUS_FAILED
    val progress = item.progress()
    val accent by animateColorAsState(
        targetValue = when {
            complete -> MiuixTheme.colorScheme.primary
            failed -> MiuixTheme.colorScheme.error
            paused -> MiuixTheme.colorScheme.onSurfaceVariantSummary
            else -> MiuixTheme.colorScheme.primary
        },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.38f),
        label = "download-accent"
    )
    val statusBackground by animateColorAsState(
        targetValue = when {
            complete -> MiuixTheme.colorScheme.primary.copy(alpha = 0.14f)
            failed -> MiuixTheme.colorScheme.error.copy(alpha = 0.12f)
            paused -> MiuixTheme.colorScheme.surfaceContainerHighest
            else -> MiuixTheme.colorScheme.primaryContainer
        },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.38f),
        label = "download-status-background"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(color = aeryoCardColor()),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = { if (complete) onOpen() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(statusBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (complete) MiuixIcons.Ok else MiuixIcons.Download,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        text = item.fileName,
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = downloadStatusText(item),
                        color = accent,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        MiuixIcons.Delete,
                        contentDescription = "删除下载",
                        tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                    )
                }
            }

            if (progress != null) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (progress == null) 12.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = progressDescription(item),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = item.backend.displayName(),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                when {
                    complete -> TextButton(text = "打开", onClick = onOpen)
                    item.backend == DownloadBackend.BUILT_IN && active ->
                        TextButton(text = "暂停", onClick = onPause)
                    item.backend == DownloadBackend.BUILT_IN && (paused || failed) ->
                        TextButton(text = "继续", onClick = onResume)
                    else -> Text(
                        text = "系统不支持暂停",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp
                    )
                }
            }
        }
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

private fun DownloadBackend.displayName(): String = when (this) {
    DownloadBackend.SYSTEM -> "系统下载"
    DownloadBackend.BUILT_IN -> "内置下载"
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
