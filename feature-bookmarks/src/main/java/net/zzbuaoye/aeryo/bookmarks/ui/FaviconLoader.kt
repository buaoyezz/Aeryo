package net.zzbuaoye.aeryo.bookmarks.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

internal object FaviconLoader {
    private const val MAX_ICON_BYTES = 512 * 1024
    private val cache = ConcurrentHashMap<String, ByteArray>()
    private val failedHosts = ConcurrentHashMap.newKeySet<String>()

    suspend fun load(url: String): ByteArray? = withContext(Dispatchers.IO) {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return@withContext null
        val host = uri.host?.lowercase()?.takeIf(String::isNotBlank) ?: return@withContext null
        cache[host]?.let { return@withContext it }
        if (host in failedHosts) return@withContext null

        val authority = buildString {
            append(host)
            if (uri.port != -1) append(":${uri.port}")
        }
        val preferredScheme = uri.scheme?.takeIf { it == "http" || it == "https" } ?: "https"
        val origins = buildList {
            add("$preferredScheme://$authority")
            if (preferredScheme != "https") add("https://$authority")
        }
        val candidates = origins.flatMap { origin ->
            listOf(
                "$origin/favicon.ico",
                "$origin/favicon.png",
                "$origin/apple-touch-icon.png"
            )
        }

        candidates.firstNotNullOfOrNull(::downloadAndNormalize)?.also {
            cache[host] = it
        } ?: run {
            failedHosts += host
            null
        }
    }

    private fun downloadAndNormalize(iconUrl: String): ByteArray? {
        val connection = runCatching {
            (URL(iconUrl).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = 2_500
                readTimeout = 2_500
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android) Aeryo/1.0")
                setRequestProperty("Accept", "image/avif,image/webp,image/png,image/*,*/*;q=0.8")
            }
        }.getOrNull() ?: return null

        return try {
            if (connection.responseCode !in 200..299) return null
            val declaredLength = connection.contentLengthLong
            if (declaredLength > MAX_ICON_BYTES) return null
            val bytes = connection.inputStream.use { input ->
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(8 * 1024)
                var total = 0
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    total += count
                    if (total > MAX_ICON_BYTES) return null
                    output.write(buffer, 0, count)
                }
                output.toByteArray()
            }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
            ByteArrayOutputStream().use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) return null
                output.toByteArray()
            }
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
