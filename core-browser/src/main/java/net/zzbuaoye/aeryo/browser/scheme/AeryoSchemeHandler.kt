package net.zzbuaoye.aeryo.browser.scheme

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceResponse
import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import android.util.Log

object AeryoSchemeHandler {
    private const val TAG = "AeryoSchemeHandler"
    private const val SCHEME = "aeryo"
    private const val DISPLAY_VERSION_KEY = "net.zzbuaoye.aeryo.DISPLAY_VERSION"
    private const val CHANNEL_KEY = "net.zzbuaoye.aeryo.CHANNEL"
    private const val BUILD_LABEL_KEY = "net.zzbuaoye.aeryo.BUILD_LABEL"

    fun handleRequest(context: Context, url: String): WebResourceResponse? {
        val uri = Uri.parse(url)
        if (uri.scheme != SCHEME) return null

        val host = uri.host.takeIf { !it.isNullOrEmpty() } ?: "index"
        var path = uri.path?.removePrefix("/") ?: ""
        if (path.isEmpty() || path == "/") {
            path = "index.html"
        }

        // Load from assets/aeryo/<host>/<path>
        // Example: aeryo://about -> assets/aeryo/about/index.html
        val assetPath = "aeryo/$host/$path"

        if (host == "version" && path == "index.html") {
            return createVersionResponse(context, assetPath)
        }

        return try {
            val inputStream = context.assets.open(assetPath)
            val mimeType = getMimeType(assetPath)
            WebResourceResponse(mimeType, "UTF-8", inputStream)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Asset not found: $assetPath")
            createErrorResponse("Page not found: $url")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading asset: $assetPath", e)
            createErrorResponse("Error loading page: $url")
        }
    }

    private fun createVersionResponse(
        context: Context,
        assetPath: String
    ): WebResourceResponse {
        val template = context.assets.open(assetPath)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        val applicationInfo = packageInfo.applicationInfo
        val rawVersionName = packageInfo.versionName.orEmpty()
        val metadata = applicationInfo?.metaData
        val versionName = metadata?.getString(DISPLAY_VERSION_KEY)
            ?.takeIf { it.isNotBlank() }
            ?: rawVersionName.substringBefore('-').removePrefix("V")

        @Suppress("DEPRECATION")
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }

        val applicationName = applicationInfo
            ?.loadLabel(packageManager)
            ?.toString()
            .orEmpty()
            .ifBlank { "Aeryo Browser" }

        val channel = metadata?.getString(CHANNEL_KEY).orEmpty().ifBlank {
            when {
                rawVersionName.contains("alpha", ignoreCase = true) -> "Alpha"
                rawVersionName.contains("beta", ignoreCase = true) -> "Beta"
                rawVersionName.contains("rc", ignoreCase = true) -> "RC"
                else -> ""
            }
        }

        val displayVersion = buildString {
            append("V")
            append(versionName.removePrefix("V"))
            if (channel.isNotBlank()) {
                append(' ')
                append(channel)
            }
        }

        val buildLabel = metadata?.getString(BUILD_LABEL_KEY)
            ?.takeIf { it.isNotBlank() }
            ?: "aeryo${versionCode / 100}.${versionCode % 100}"

        val engineVersion = WebView.getCurrentWebViewPackage()
            ?.versionName
            .orEmpty()
            .ifBlank { "Unknown" }

        val values = mapOf(
            "__AERYO_APP_NAME__" to applicationName,
            "__AERYO_PACKAGE__" to context.packageName,
            "__AERYO_VERSION__" to displayVersion,
            "__AERYO_CHANNEL__" to channel.ifBlank { "Stable" },
            "__AERYO_BUILD__" to buildLabel,
            "__AERYO_ENGINE__" to engineVersion
        )

        var html = template
        values.forEach { (placeholder, value) ->
            html = html.replace(placeholder, TextUtils.htmlEncode(value))
        }

        return WebResourceResponse(
            "text/html",
            "UTF-8",
            ByteArrayInputStream(html.toByteArray(Charsets.UTF_8))
        )
    }

    private fun getMimeType(path: String): String {
        return when {
            path.endsWith(".html") -> "text/html"
            path.endsWith(".css") -> "text/css"
            path.endsWith(".js") -> "application/javascript"
            path.endsWith(".png") -> "image/png"
            path.endsWith(".jpg") || path.endsWith(".jpeg") -> "image/jpeg"
            path.endsWith(".svg") -> "image/svg+xml"
            path.endsWith(".json") -> "application/json"
            else -> "text/plain"
        }
    }

    private fun createErrorResponse(message: String): WebResourceResponse {
        val html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Error</title>
                <style>
                    body {
                        font-family: system-ui, -apple-system, sans-serif;
                        padding: 24px;
                        text-align: center;
                        background-color: #f5f5f5;
                        color: #333;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: white;
                        padding: 32px;
                        border-radius: 12px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                    }
                    h1 { color: #d32f2f; margin-top: 0; }
                    p { font-size: 16px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Oops!</h1>
                    <p>$message</p>
                </div>
            </body>
            </html>
        """.trimIndent()
        return WebResourceResponse(
            "text/html",
            "UTF-8",
            ByteArrayInputStream(html.toByteArray(Charsets.UTF_8))
        )
    }
}
