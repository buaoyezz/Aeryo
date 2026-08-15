package net.zzbuaoye.aeryo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.Download
import compose.icons.tablericons.ExternalLink
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Search
import compose.icons.tablericons.Share
import net.zzbuaoye.aeryo.browser.model.ContextMenuTarget
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AeryoContextMenuBottomSheet(
    show: Boolean,
    target: ContextMenuTarget?,
    onDismissRequest: () -> Unit,
    onOpenInNewTab: (url: String, isBackground: Boolean) -> Unit,
    onDownloadImage: (imageUrl: String) -> Unit,
    onSearchImage: (imageUrl: String) -> Unit
) {
    if (target == null) return
    val context = LocalContext.current

    OverlayBottomSheet(
        show = show,
        title = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (target) {
                                is ContextMenuTarget.Image -> TablerIcons.Photo
                                is ContextMenuTarget.Link -> TablerIcons.ExternalLink
                                is ContextMenuTarget.Media -> TablerIcons.Download
                                is ContextMenuTarget.Action -> TablerIcons.ExternalLink
                            },
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (target) {
                                is ContextMenuTarget.Image -> "图片操作"
                                is ContextMenuTarget.Link -> "网页链接"
                                is ContextMenuTarget.Media -> "媒体资源"
                                is ContextMenuTarget.Action -> target.label.ifBlank { "快捷操作" }
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (target) {
                                is ContextMenuTarget.Image -> target.imageUrl
                                is ContextMenuTarget.Link -> target.url
                                is ContextMenuTarget.Media -> target.mediaUrl
                                is ContextMenuTarget.Action -> target.actionUrl
                            },
                            fontSize = 12.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = MiuixIcons.Close,
                        contentDescription = "关闭",
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Items
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    when (target) {
                        is ContextMenuTarget.Image -> {
                            ContextMenuItemRow(
                                icon = TablerIcons.Download,
                                title = "保存图片至相册",
                                onClick = {
                                    onDownloadImage(target.imageUrl)
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Plus,
                                title = "在新标签页中查看大图",
                                onClick = {
                                    onOpenInNewTab(target.imageUrl, false)
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Search,
                                title = "以图搜图 (Bing 视觉搜索)",
                                onClick = {
                                    onSearchImage(target.imageUrl)
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Copy,
                                title = "复制图片直链",
                                onClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText("Image URL", target.imageUrl))
                                    Toast.makeText(context, "已复制图片链接", Toast.LENGTH_SHORT).show()
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Share,
                                title = "分享图片链接",
                                onClick = {
                                    runCatching {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, target.imageUrl)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "分享图片链接"))
                                    }
                                    onDismissRequest()
                                }
                            )
                            val linkUrl = target.linkUrl
                            if (!linkUrl.isNullOrBlank()) {
                                ContextMenuItemRow(
                                    icon = TablerIcons.ExternalLink,
                                    title = "打开所指链接",
                                    onClick = {
                                        onOpenInNewTab(linkUrl, false)
                                        onDismissRequest()
                                    }
                                )
                            }
                        }

                        is ContextMenuTarget.Link -> {
                            ContextMenuItemRow(
                                icon = TablerIcons.Plus,
                                title = "在新标签页中打开",
                                onClick = {
                                    onOpenInNewTab(target.url, false)
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.ExternalLink,
                                title = "在后台标签页打开",
                                onClick = {
                                    onOpenInNewTab(target.url, true)
                                    Toast.makeText(context, "已在后台打开新标签页", Toast.LENGTH_SHORT).show()
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Copy,
                                title = "复制链接地址",
                                onClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText("Link URL", target.url))
                                    Toast.makeText(context, "已复制链接地址", Toast.LENGTH_SHORT).show()
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Share,
                                title = "分享链接",
                                onClick = {
                                    runCatching {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, target.url)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "分享链接"))
                                    }
                                    onDismissRequest()
                                }
                            )
                        }

                        is ContextMenuTarget.Media -> {
                            ContextMenuItemRow(
                                icon = TablerIcons.Download,
                                title = "下载媒体文件",
                                onClick = {
                                    onDownloadImage(target.mediaUrl)
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Copy,
                                title = "复制媒体直链",
                                onClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText("Media URL", target.mediaUrl))
                                    Toast.makeText(context, "已复制媒体直链", Toast.LENGTH_SHORT).show()
                                    onDismissRequest()
                                }
                            )
                        }

                        is ContextMenuTarget.Action -> {
                            ContextMenuItemRow(
                                icon = TablerIcons.ExternalLink,
                                title = "立即前往 / 呼出",
                                onClick = {
                                    runCatching {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target.actionUrl))
                                        context.startActivity(intent)
                                    }
                                    onDismissRequest()
                                }
                            )
                            ContextMenuItemRow(
                                icon = TablerIcons.Copy,
                                title = "复制内容",
                                onClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText("Action Text", target.actionUrl))
                                    Toast.makeText(context, "已复制内容", Toast.LENGTH_SHORT).show()
                                    onDismissRequest()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ContextMenuItemRow(
    icon: ImageVector,
    title: String,
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
            tint = MiuixTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MiuixTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
