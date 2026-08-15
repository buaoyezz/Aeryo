package net.zzbuaoye.aeryo.browser.model

import java.util.UUID

/**
 * 网页视频与媒体流嗅探捕获结果模型
 */
data class SniffedMediaItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String = "",
    val mimeType: String = "",
    val extension: String = "",
    val size: Long = 0L,
    val sniffedAt: Long = System.currentTimeMillis()
)
