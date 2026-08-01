package net.zzbuaoye.aeryo.browser.model

import android.graphics.Bitmap
import android.webkit.WebView
import java.util.UUID

/**
 * 代表浏览器中的一个网页标签页 (Web Tab)
 */
data class WebTab(
    val id: String = UUID.randomUUID().toString(),
    var url: String = "about:blank",
    var title: String = "新标签页",
    var favicon: Bitmap? = null,
    var progress: Int = 0,
    var isLoading: Boolean = false,
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false,
    var isIncognito: Boolean = false,
    var isDesktopMode: Boolean = false,
    var isSslSecure: Boolean = false,
    var preview: Bitmap? = null,
    var lastSearchQuery: String = "",
    var webView: WebView? = null
)
