package net.zzbuaoye.aeryo.downloads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.downloads.model.DownloadItem
import net.zzbuaoye.aeryo.downloads.model.DownloadRequest
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Download
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun DownloadConfirmationDialog(
    request: DownloadRequest,
    duplicate: DownloadItem?,
    useBuiltIn: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    OverlayDialog(
        show = true,
        title = if (duplicate == null) "开始下载？" else "检测到重复任务",
        summary = if (duplicate == null) {
            "确认后将开始下载这个文件"
        } else {
            "列表中已有相同链接或同名文件，你仍然可以再次下载"
        },
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MiuixTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MiuixIcons.Download,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = request.fileName,
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = request.contentLength.takeIf { it > 0L }?.let(::formatBytes) ?: "文件大小未知",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Text(
                text = if (useBuiltIn) {
                    "将使用内置下载，可在下载管理中暂停或继续。"
                } else {
                    "将交给系统下载服务，可在系统通知中查看进度，但系统任务不支持暂停。"
                },
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            if (duplicate != null) {
                Text(
                    text = "已存在：${duplicate.fileName} · ${duplicate.backend.duplicateBackendName()}",
                    color = MiuixTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(text = "取消", onClick = onDismiss, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(16.dp))
                TextButton(
                    text = if (duplicate == null) "下载" else "仍要下载",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun net.zzbuaoye.aeryo.downloads.model.DownloadBackend.duplicateBackendName(): String = when (this) {
    net.zzbuaoye.aeryo.downloads.model.DownloadBackend.SYSTEM -> "系统下载"
    net.zzbuaoye.aeryo.downloads.model.DownloadBackend.BUILT_IN -> "内置下载"
}
