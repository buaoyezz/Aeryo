package net.zzbuaoye.aeryo.downloads.model

data class DownloadRequest(
    val url: String,
    val fileName: String,
    val userAgent: String? = null,
    val contentDisposition: String? = null,
    val mimeType: String? = null,
    val referer: String? = null,
    val contentLength: Long = -1L
)
