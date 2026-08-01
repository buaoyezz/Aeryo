package net.zzbuaoye.aeryo.downloads.model

enum class DownloadBackend {
    SYSTEM,
    BUILT_IN
}

data class DownloadItem(
    val id: Long,
    val fileName: String,
    val url: String,
    val mimeType: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: Int, // STATUS_RUNNING, STATUS_SUCCESSFUL, STATUS_FAILED, etc.
    val localUri: String? = null,
    val backend: DownloadBackend = DownloadBackend.SYSTEM,
    val speedBytesPerSecond: Long = 0L,
    val createdAt: Long = 0L,
    val failureReason: String? = null
) {
    val stableKey: String
        get() = "${backend.name}:$id"
}
