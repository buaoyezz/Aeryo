package net.zzbuaoye.aeryo.downloads

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import net.zzbuaoye.aeryo.downloads.model.DownloadBackend
import net.zzbuaoye.aeryo.downloads.model.DownloadItem
import net.zzbuaoye.aeryo.downloads.model.DownloadRequest
import org.json.JSONArray
import org.json.JSONObject

class AeryoDownloadManager(context: Context) {
    private val appContext = context.applicationContext
    private val systemDownloadManager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val internalDownloads = BuiltInDownloadEngine(appContext)
    private val systemSamples = mutableMapOf<Long, TransferSample>()

    fun createRequest(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        referer: String? = null,
        contentLength: Long = -1L
    ): DownloadRequest {
        return DownloadRequest(
            url = url,
            fileName = URLUtil.guessFileName(url, contentDisposition, mimeType),
            userAgent = userAgent,
            contentDisposition = contentDisposition,
            mimeType = mimeType,
            referer = referer,
            contentLength = contentLength
        )
    }

    fun findDuplicate(request: DownloadRequest): DownloadItem? {
        return getAllDownloads().firstOrNull { item ->
            item.status != DownloadManager.STATUS_FAILED &&
                (item.url == request.url || item.fileName == request.fileName)
        }
    }

    fun startDownload(request: DownloadRequest, useBuiltIn: Boolean): Long {
        if (!request.url.startsWith("http://") && !request.url.startsWith("https://")) {
            toast("暂不支持下载此类文件")
            return -1L
        }
        return if (useBuiltIn) {
            internalDownloads.enqueue(request).also { id ->
                if (id >= 0) toast("已添加到内置下载：${request.fileName}")
            }
        } else {
            startSystemDownload(request)
        }
    }

    fun getAllDownloads(): List<DownloadItem> {
        return (getSystemDownloads() + internalDownloads.getAll())
            .sortedByDescending(DownloadItem::createdAt)
    }

    fun pauseDownload(item: DownloadItem) {
        if (item.backend == DownloadBackend.BUILT_IN) {
            internalDownloads.pause(item.id)
        }
    }

    fun resumeDownload(item: DownloadItem) {
        if (item.backend == DownloadBackend.BUILT_IN) {
            internalDownloads.resume(item.id)
        }
    }

    fun removeDownload(item: DownloadItem) {
        when (item.backend) {
            DownloadBackend.SYSTEM -> systemDownloadManager.remove(item.id)
            DownloadBackend.BUILT_IN -> internalDownloads.remove(item.id)
        }
    }

    fun openDownload(item: DownloadItem) {
        when (item.backend) {
            DownloadBackend.SYSTEM -> openSystemDownload(item)
            DownloadBackend.BUILT_IN -> openBuiltInDownload(item)
        }
    }

    private fun startSystemDownload(request: DownloadRequest): Long {
        val downloadRequest = DownloadManager.Request(Uri.parse(request.url)).apply {
            setMimeType(request.mimeType)
            request.userAgent?.takeIf(String::isNotBlank)?.let { addRequestHeader("User-Agent", it) }
            CookieManager.getInstance().getCookie(request.url)
                ?.takeIf(String::isNotBlank)
                ?.let { addRequestHeader("Cookie", it) }
            request.referer?.takeIf(String::isNotBlank)?.let { addRequestHeader("Referer", it) }
            setTitle(request.fileName)
            setDescription("Aeryo 浏览器下载中…")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, request.fileName)
        }
        return try {
            systemDownloadManager.enqueue(downloadRequest).also {
                toast("已交给系统下载：${request.fileName}")
            }
        } catch (_: Exception) {
            toast("无法开始下载，请稍后重试")
            -1L
        }
    }

    private fun getSystemDownloads(): List<DownloadItem> {
        val downloads = mutableListOf<DownloadItem>()
        val cursor = systemDownloadManager.query(DownloadManager.Query()) ?: return downloads
        val currentTime = SystemClock.elapsedRealtime()

        cursor.use { c ->
            val idIndex = c.getColumnIndex(DownloadManager.COLUMN_ID)
            val titleIndex = c.getColumnIndex(DownloadManager.COLUMN_TITLE)
            val uriIndex = c.getColumnIndex(DownloadManager.COLUMN_URI)
            val mimeIndex = c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)
            val totalBytesIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val downloadedBytesIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val statusIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val localUriIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val updatedAtIndex = c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)

            while (c.moveToNext()) {
                val id = c.longAt(idIndex)
                val title = c.stringAt(titleIndex).orEmpty().ifBlank { "未知文件" }
                val downloadedBytes = c.longAt(downloadedBytesIndex)
                val status = c.intAt(statusIndex)
                val previous = synchronized(systemSamples) { systemSamples[id] }
                val speed = if (status == DownloadManager.STATUS_RUNNING && previous != null) {
                    val elapsed = (currentTime - previous.timestampMs).coerceAtLeast(1L)
                    ((downloadedBytes - previous.bytes) * 1_000L / elapsed).coerceAtLeast(0L)
                } else {
                    0L
                }
                synchronized(systemSamples) {
                    systemSamples[id] = TransferSample(downloadedBytes, currentTime)
                }

                downloads += DownloadItem(
                    id = id,
                    fileName = title,
                    url = c.stringAt(uriIndex).orEmpty(),
                    mimeType = c.stringAt(mimeIndex).orEmpty(),
                    totalBytes = c.longAt(totalBytesIndex),
                    downloadedBytes = downloadedBytes,
                    status = status,
                    localUri = c.stringAt(localUriIndex),
                    backend = DownloadBackend.SYSTEM,
                    speedBytesPerSecond = speed,
                    createdAt = c.longAt(updatedAtIndex)
                )
            }
        }
        synchronized(systemSamples) {
            systemSamples.keys.retainAll(downloads.mapTo(mutableSetOf(), DownloadItem::id))
        }
        return downloads
    }

    private fun openSystemDownload(item: DownloadItem) {
        if (item.status != DownloadManager.STATUS_SUCCESSFUL) {
            toast("文件尚未下载完成")
            return
        }
        val uri = systemDownloadManager.getUriForDownloadedFile(item.id)
        if (uri == null) {
            toast("下载文件不存在")
            return
        }
        openUri(uri, systemDownloadManager.getMimeTypeForDownloadedFile(item.id) ?: item.mimeType)
    }

    private fun openBuiltInDownload(item: DownloadItem) {
        if (item.status != DownloadManager.STATUS_SUCCESSFUL) {
            toast("文件尚未下载完成")
            return
        }
        val path = item.localUri ?: run {
            toast("下载文件不存在")
            return
        }
        val file = File(path)
        if (!file.exists()) {
            toast("下载文件不存在")
            return
        }
        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
        openUri(uri, item.mimeType)
    }

    private fun openUri(uri: Uri, mimeType: String?) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType?.takeIf(String::isNotBlank) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            appContext.startActivity(intent)
        } catch (_: Exception) {
            toast("没有可打开此文件的应用")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }

    private data class TransferSample(val bytes: Long, val timestampMs: Long)
}

private class BuiltInDownloadEngine(private val context: Context) {
    private val preferences = context.getSharedPreferences("aeryo_internal_downloads", Context.MODE_PRIVATE)
    private val tasks = ConcurrentHashMap<Long, InternalTask>()
    private val jobs = ConcurrentHashMap<Long, Job>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val nextId = AtomicLong(-System.currentTimeMillis())

    init {
        loadTasks()
    }

    fun enqueue(request: DownloadRequest): Long {
        val targetFile = nextAvailableFile(request.fileName)
        val task = InternalTask(
            id = nextTaskId(),
            fileName = targetFile.name,
            url = request.url,
            mimeType = request.mimeType.orEmpty(),
            totalBytes = request.contentLength.coerceAtLeast(0L),
            downloadedBytes = 0L,
            status = DownloadManager.STATUS_PENDING,
            localPath = targetFile.absolutePath,
            createdAt = System.currentTimeMillis(),
            userAgent = request.userAgent,
            referer = request.referer
        )
        tasks[task.id] = task
        persist()
        start(task.id)
        return task.id
    }

    fun getAll(): List<DownloadItem> = tasks.values
        .sortedByDescending(InternalTask::createdAt)
        .map(InternalTask::toDownloadItem)

    fun pause(id: Long) {
        update(id) { task ->
            if (task.status == DownloadManager.STATUS_RUNNING || task.status == DownloadManager.STATUS_PENDING) {
                task.copy(status = DownloadManager.STATUS_PAUSED, speedBytesPerSecond = 0L)
            } else {
                task
            }
        }
        jobs.remove(id)?.cancel()
    }

    fun resume(id: Long) {
        val task = tasks[id] ?: return
        if (task.status == DownloadManager.STATUS_PAUSED || task.status == DownloadManager.STATUS_FAILED) {
            update(id) { it.copy(status = DownloadManager.STATUS_PENDING, failureReason = null) }
            start(id)
        }
    }

    fun remove(id: Long) {
        jobs.remove(id)?.cancel()
        val task = tasks.remove(id) ?: return
        File(task.localPath).takeIf(File::exists)?.delete()
        persist()
    }

    private fun start(id: Long) {
        if (jobs[id]?.isActive == true) return
        jobs[id] = scope.launch {
            download(id)
        }
    }

    private suspend fun download(id: Long) {
        var connection: HttpURLConnection? = null
        try {
            val initialTask = tasks[id] ?: return
            val targetFile = File(initialTask.localPath)
            targetFile.parentFile?.mkdirs()

            var existingBytes = targetFile.takeIf(File::exists)?.length() ?: 0L
            update(id) {
                it.copy(
                    downloadedBytes = existingBytes,
                    status = DownloadManager.STATUS_RUNNING,
                    speedBytesPerSecond = 0L,
                    failureReason = null
                )
            }

            connection = (URL(initialTask.url).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = 15_000
                readTimeout = 20_000
                requestMethod = "GET"
                initialTask.userAgent?.takeIf(String::isNotBlank)?.let { setRequestProperty("User-Agent", it) }
                initialTask.referer?.takeIf(String::isNotBlank)?.let { setRequestProperty("Referer", it) }
                CookieManager.getInstance().getCookie(initialTask.url)
                    ?.takeIf(String::isNotBlank)
                    ?.let { setRequestProperty("Cookie", it) }
                if (existingBytes > 0L) {
                    setRequestProperty("Range", "bytes=$existingBytes-")
                }
            }
            val responseCode = connection.responseCode
            if (responseCode !in 200..299 && responseCode != HttpURLConnection.HTTP_PARTIAL) {
                throw IOException("HTTP $responseCode")
            }

            val append = existingBytes > 0L && responseCode == HttpURLConnection.HTTP_PARTIAL
            if (!append) {
                existingBytes = 0L
            }
            val contentLength = connection.contentLengthLong.takeIf { it >= 0L } ?: 0L
            val totalBytes = if (contentLength > 0L) existingBytes + contentLength else 0L
            update(id) {
                it.copy(
                    downloadedBytes = existingBytes,
                    totalBytes = totalBytes.takeIf { total -> total > 0L } ?: it.totalBytes,
                    mimeType = connection.contentType?.substringBefore(';').orEmpty().ifBlank { it.mimeType }
                )
            }

            BufferedInputStream(connection.inputStream).use { input ->
                FileOutputStream(targetFile, append).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var downloadedBytes = existingBytes
                    var sampleBytes = downloadedBytes
                    var sampleTime = SystemClock.elapsedRealtime()

                    while (true) {
                        coroutineContext.ensureActive()
                        if (tasks[id]?.status == DownloadManager.STATUS_PAUSED) return
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        downloadedBytes += count

                        val now = SystemClock.elapsedRealtime()
                        if (now - sampleTime >= 250L) {
                            val elapsed = (now - sampleTime).coerceAtLeast(1L)
                            val speed = ((downloadedBytes - sampleBytes) * 1_000L / elapsed).coerceAtLeast(0L)
                            update(id) {
                                it.copy(
                                    downloadedBytes = downloadedBytes,
                                    speedBytesPerSecond = speed,
                                    totalBytes = totalBytes.takeIf { total -> total > 0L } ?: it.totalBytes
                                )
                            }
                            sampleBytes = downloadedBytes
                            sampleTime = now
                        }
                    }
                    output.flush()
                    update(id) {
                        it.copy(
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes.takeIf { total -> total > 0L } ?: downloadedBytes,
                            status = DownloadManager.STATUS_SUCCESSFUL,
                            speedBytesPerSecond = 0L,
                            failureReason = null
                        )
                    }
                }
            }
        } catch (_: CancellationException) {
            // pause/remove cancels the coroutine after the task state has been updated.
        } catch (error: Exception) {
            val task = tasks[id]
            if (task?.status != DownloadManager.STATUS_PAUSED) {
                update(id) {
                    it.copy(
                        status = DownloadManager.STATUS_FAILED,
                        speedBytesPerSecond = 0L,
                        failureReason = error.message ?: "下载失败"
                    )
                }
            }
        } finally {
            connection?.disconnect()
            jobs.remove(id)
        }
    }

    private fun update(id: Long, transform: (InternalTask) -> InternalTask) {
        val current = tasks[id] ?: return
        tasks[id] = transform(current)
        persist()
    }

    private fun nextTaskId(): Long {
        var id = nextId.decrementAndGet()
        while (tasks.containsKey(id)) {
            id = nextId.decrementAndGet()
        }
        return id
    }

    private fun nextAvailableFile(requestedName: String): File {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        directory.mkdirs()
        val safeName = requestedName.replace(Regex("[\\\\/:*?\"<>|]"), "_").ifBlank { "download" }
        val dotIndex = safeName.lastIndexOf('.')
        val base = if (dotIndex > 0) safeName.substring(0, dotIndex) else safeName
        val extension = if (dotIndex > 0) safeName.substring(dotIndex) else ""
        var suffix = 0
        var file = File(directory, safeName)
        while (file.exists() || tasks.values.any { it.localPath == file.absolutePath }) {
            suffix += 1
            file = File(directory, "$base ($suffix)$extension")
        }
        return file
    }

    private fun loadTasks() {
        val serialized = preferences.getString(TASKS_KEY, null) ?: return
        runCatching {
            val array = JSONArray(serialized)
            repeat(array.length()) { index ->
                val task = InternalTask.fromJson(array.getJSONObject(index))
                val restored = if (task.status == DownloadManager.STATUS_RUNNING || task.status == DownloadManager.STATUS_PENDING) {
                    task.copy(status = DownloadManager.STATUS_PAUSED, speedBytesPerSecond = 0L)
                } else {
                    task
                }
                tasks[restored.id] = restored
            }
        }
    }

    @Synchronized
    private fun persist() {
        val array = JSONArray()
        tasks.values.sortedByDescending(InternalTask::createdAt).forEach { array.put(it.toJson()) }
        preferences.edit().putString(TASKS_KEY, array.toString()).apply()
    }

    private data class InternalTask(
        val id: Long,
        val fileName: String,
        val url: String,
        val mimeType: String,
        val totalBytes: Long,
        val downloadedBytes: Long,
        val status: Int,
        val localPath: String,
        val createdAt: Long,
        val speedBytesPerSecond: Long = 0L,
        val failureReason: String? = null,
        val userAgent: String? = null,
        val referer: String? = null
    ) {
        fun toDownloadItem() = DownloadItem(
            id = id,
            fileName = fileName,
            url = url,
            mimeType = mimeType,
            totalBytes = totalBytes,
            downloadedBytes = downloadedBytes,
            status = status,
            localUri = localPath,
            backend = DownloadBackend.BUILT_IN,
            speedBytesPerSecond = speedBytesPerSecond,
            createdAt = createdAt,
            failureReason = failureReason
        )

        fun toJson() = JSONObject().apply {
            put("id", id)
            put("fileName", fileName)
            put("url", url)
            put("mimeType", mimeType)
            put("totalBytes", totalBytes)
            put("downloadedBytes", downloadedBytes)
            put("status", status)
            put("localPath", localPath)
            put("createdAt", createdAt)
            put("speed", speedBytesPerSecond)
            put("failureReason", failureReason)
            put("userAgent", userAgent)
            put("referer", referer)
        }

        companion object {
            fun fromJson(json: JSONObject) = InternalTask(
                id = json.getLong("id"),
                fileName = json.getString("fileName"),
                url = json.getString("url"),
                mimeType = json.optString("mimeType"),
                totalBytes = json.optLong("totalBytes"),
                downloadedBytes = json.optLong("downloadedBytes"),
                status = json.optInt("status", DownloadManager.STATUS_PAUSED),
                localPath = json.getString("localPath"),
                createdAt = json.optLong("createdAt", System.currentTimeMillis()),
                speedBytesPerSecond = json.optLong("speed"),
                failureReason = json.optString("failureReason").takeIf(String::isNotBlank),
                userAgent = json.optString("userAgent").takeIf(String::isNotBlank),
                referer = json.optString("referer").takeIf(String::isNotBlank)
            )
        }
    }

    private companion object {
        const val TASKS_KEY = "tasks"
    }
}

private fun android.database.Cursor.longAt(index: Int): Long =
    if (index >= 0) getLong(index) else 0L

private fun android.database.Cursor.intAt(index: Int): Int =
    if (index >= 0) getInt(index) else 0

private fun android.database.Cursor.stringAt(index: Int): String? =
    if (index >= 0) getString(index) else null
