package net.zzbuaoye.aeryo.browser.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebView.HitTestResult
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.zzbuaoye.aeryo.browser.assignIncognitoProfile
import net.zzbuaoye.aeryo.browser.adblock.AdBlockEngine
import net.zzbuaoye.aeryo.browser.cookieManagerFor
import net.zzbuaoye.aeryo.browser.deleteIncognitoProfile
import net.zzbuaoye.aeryo.browser.model.ContextMenuTarget
import net.zzbuaoye.aeryo.browser.model.SniffedMediaItem
import net.zzbuaoye.aeryo.browser.model.WebTab
import net.zzbuaoye.aeryo.browser.supportsIsolatedIncognitoProfile

const val CHROME_DESKTOP_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

private fun inspectMediaStream(url: String, headers: Map<String, String>? = null): SniffedMediaItem? {
    val cleanUrl = url.substringBefore('?').lowercase()
    val isMediaExtension = cleanUrl.endsWith(".m3u8") ||
        cleanUrl.endsWith(".mp4") ||
        cleanUrl.endsWith(".flv") ||
        cleanUrl.endsWith(".webm") ||
        cleanUrl.endsWith(".m4s") ||
        cleanUrl.endsWith(".mpd") ||
        cleanUrl.endsWith(".mkv") ||
        cleanUrl.endsWith(".ts")

    val contentType = headers?.get("Content-Type") ?: headers?.get("content-type") ?: ""
    val isMediaMime = contentType.startsWith("video/", ignoreCase = true) ||
        contentType.contains("application/vnd.apple.mpegurl", ignoreCase = true) ||
        contentType.contains("application/x-mpegurl", ignoreCase = true)

    if (isMediaExtension || isMediaMime) {
        val ext = when {
            cleanUrl.endsWith(".m3u8") || contentType.contains("mpegurl", ignoreCase = true) -> "m3u8"
            cleanUrl.endsWith(".mp4") -> "mp4"
            cleanUrl.endsWith(".flv") -> "flv"
            cleanUrl.endsWith(".webm") -> "webm"
            cleanUrl.endsWith(".mpd") -> "mpd"
            cleanUrl.endsWith(".mkv") -> "mkv"
            cleanUrl.endsWith(".m4s") -> "m4s"
            cleanUrl.endsWith(".ts") -> "ts"
            else -> "stream"
        }
        val filename = url.substringBefore('?').substringAfterLast('/').ifBlank { "media_stream.$ext" }
        return SniffedMediaItem(
            url = url,
            title = filename,
            mimeType = contentType.ifBlank { "video/*" },
            extension = ext
        )
    }
    return null
}

fun captureWebViewPreview(webView: WebView): Bitmap? {
    if (webView.width <= 0 || webView.height <= 0) return null
    return runCatching {
        val previewWidth = 320
        val previewHeight = (webView.height * (previewWidth.toFloat() / webView.width)).toInt().coerceAtLeast(180)
        Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.RGB_565).also { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.scale(
                previewWidth.toFloat() / webView.width,
                previewHeight.toFloat() / webView.height
            )
            webView.draw(canvas)
        }
    }.getOrNull()
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AeryoWebView(
    tab: WebTab,
    nightModeEnabled: Boolean = false,
    smartDarkModeEnabled: Boolean = true,
    doNotTrackEnabled: Boolean = true,
    blockThirdPartyCookies: Boolean = false,
    mediaSnifferEnabled: Boolean = true,
    backgroundPlaybackEnabled: Boolean = true,
    onTabUpdated: (String, (WebTab) -> WebTab) -> Unit,
    onDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) -> Unit,
    onOpenExternalLink: (String) -> Boolean = { false },
    onMediaSniffed: ((SniffedMediaItem) -> Unit)? = null,
    onContextMenuRequested: (ContextMenuTarget) -> Unit = {},
    onScrollChanged: (scrollY: Int, isScrollingUp: Boolean) -> Unit = { _, _ -> },
    onCustomViewShow: (View, WebChromeClient.CustomViewCallback) -> Unit = { _, _ -> },
    onCustomViewHide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var lastLoadedUrl by remember { mutableStateOf(tab.url) }
    var handledNavigationRequestId by remember(tab.id) { mutableStateOf(tab.navigationRequestId) }
    val tabId = tab.id

    LaunchedEffect(backgroundPlaybackEnabled) {
        net.zzbuaoye.aeryo.browser.script.UserScriptEngine.setScriptEnabled("page_visibility_spoof", backgroundPlaybackEnabled)
    }

    key(tab.id, tab.isIncognito) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val desiredIncognitoMode = tab.isIncognito
                val previousWebView = tab.webView
                val previousIncognitoMode = previousWebView?.tag as? Boolean
                val existingWebView = previousWebView?.takeIf {
                    previousIncognitoMode == desiredIncognitoMode
                }
                if (previousWebView != null && existingWebView == null) {
                    (previousWebView.parent as? ViewGroup)?.removeView(previousWebView)
                    previousWebView.destroy()
                    tab.webView = null
                    if (previousIncognitoMode == true && !desiredIncognitoMode) {
                        deleteIncognitoProfile()
                    }
                }
                val usesIncognitoProfile = tab.isIncognito && supportsIsolatedIncognitoProfile()
                val webView = (existingWebView ?: WebView(context).also { newWebView ->
                    if (tab.isIncognito) assignIncognitoProfile(newWebView)
                    newWebView.tag = desiredIncognitoMode
                }).apply {
                    (parent as? ViewGroup)?.removeView(this)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        javaScriptEnabled = true
                        javaScriptCanOpenWindowsAutomatically = true
                        setSupportMultipleWindows(false)
                        // Incognito WebViews use a separate profile, so modern sites may use
                        // localStorage/IndexedDB without leaking data into the default profile.
                        domStorageEnabled = !tab.isIncognito || usesIncognitoProfile
                        databaseEnabled = !tab.isIncognito || usesIncognitoProfile
                        saveFormData = !tab.isIncognito
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = false
                        allowContentAccess = false
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            allowFileAccessFromFileURLs = false
                            allowUniversalAccessFromFileURLs = false
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            forceDark = if (nightModeEnabled) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
                        }
                    }

                    if (tab.isIncognito) {
                        clearHistory()
                        clearFormData()
                    }
                    val configuredWebView = this
                    cookieManagerFor(configuredWebView, usesIncognitoProfile).apply {
                        setAcceptCookie(true)
                        setAcceptThirdPartyCookies(configuredWebView, !blockThirdPartyCookies)
                    }

                    setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                        onDownloadRequested(url, userAgent, contentDisposition, mimetype, contentLength)
                    }

                    setOnLongClickListener {
                        val result = hitTestResult
                        val extra = result.extra
                        val target: ContextMenuTarget? = when (result.type) {
                            HitTestResult.IMAGE_TYPE -> {
                                extra?.let { ContextMenuTarget.Image(imageUrl = it) }
                            }
                            HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                                extra?.let { ContextMenuTarget.Image(imageUrl = it) }
                            }
                            HitTestResult.SRC_ANCHOR_TYPE -> {
                                extra?.let { ContextMenuTarget.Link(url = it) }
                            }
                            HitTestResult.PHONE_TYPE -> {
                                extra?.let { ContextMenuTarget.Action(actionUrl = it, actionType = ContextMenuTarget.ActionType.PHONE, label = "拨打电话") }
                            }
                            HitTestResult.EMAIL_TYPE -> {
                                extra?.let { ContextMenuTarget.Action(actionUrl = it, actionType = ContextMenuTarget.ActionType.EMAIL, label = "发送邮件") }
                            }
                            HitTestResult.GEO_TYPE -> {
                                extra?.let { ContextMenuTarget.Action(actionUrl = it, actionType = ContextMenuTarget.ActionType.GEO, label = "查看地图") }
                            }
                            else -> null
                        }
                        if (target != null) {
                            onContextMenuRequested(target)
                            true
                        } else {
                            false
                        }
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                            val isScrollingUp = scrollY < oldScrollY
                            onScrollChanged(scrollY, isScrollingUp)
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onPermissionRequest(request: PermissionRequest?) {
                            if (request == null) return

                            val requestedResources = request.resources
                            val isSecureOrigin = request.origin?.scheme.equals("https", ignoreCase = true)
                            val requestsOnlyProtectedMedia = requestedResources.isNotEmpty() &&
                                requestedResources.all { it == PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID }

                            // EME/Widevine playback needs the protected-media identifier. Incognito
                            // pages may use it only when their data is isolated in a separate profile.
                            // Camera, microphone, MIDI, and future resource types remain denied.
                            val protectedMediaAllowed = !tab.isIncognito || usesIncognitoProfile
                            if (isSecureOrigin && protectedMediaAllowed && requestsOnlyProtectedMedia) {
                                request.grant(requestedResources)
                            } else {
                                request.deny()
                            }
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            onTabUpdated(tabId) { current ->
                                if (current.url == "about:blank") current else {
                                    current.copy(
                                        progress = newProgress.coerceIn(1, 100),
                                        isLoading = newProgress < 100
                                    )
                                }
                            }
                        }

                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            if (!title.isNullOrEmpty()) {
                                onTabUpdated(tabId) { current ->
                                    if (current.url == "about:blank") current else current.copy(title = title)
                                }
                            }
                        }

                        override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                            if (icon != null) {
                                onTabUpdated(tabId) { current ->
                                    if (current.url == "about:blank") current else current.copy(favicon = icon)
                                }
                            }
                        }

                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            if (view != null && callback != null) {
                                onCustomViewShow(view, callback)
                            }
                        }

                        override fun onHideCustomView() {
                            onCustomViewHide()
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            val targetUrl = url ?: return false
                            canonicalBingSearchUrl(targetUrl)?.let { canonicalUrl ->
                                view?.loadUrl(canonicalUrl)
                                return true
                            }
                            return if (isBrowserUrl(targetUrl)) {
                                false
                            } else {
                                onOpenExternalLink(targetUrl)
                                true
                            }
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val targetUrl = request?.url?.toString() ?: return false
                            if (request.isForMainFrame) {
                                canonicalBingSearchUrl(targetUrl)?.let { canonicalUrl ->
                                    view?.loadUrl(canonicalUrl)
                                    return true
                                }
                            }
                            return if (isBrowserUrl(targetUrl)) {
                                false
                            } else {
                                onOpenExternalLink(targetUrl)
                                true
                            }
                        }

                        override fun onLoadResource(view: WebView?, url: String?) {
                            super.onLoadResource(view, url)
                            if (mediaSnifferEnabled && url != null) {
                                inspectMediaStream(url)?.let { sniffed ->
                                    view?.post {
                                        onMediaSniffed?.invoke(sniffed)
                                    }
                                }
                            }
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val url = request?.url?.toString()
                            if (url != null && url.startsWith("aeryo://")) {
                                view?.context?.let { ctx ->
                                    val response = net.zzbuaoye.aeryo.browser.scheme.AeryoSchemeHandler.handleRequest(ctx, url)
                                    if (response != null) return response
                                }
                            }
                            if (request != null && AdBlockEngine.isAdRequest(request)) {
                                android.util.Log.d("AeryoWebView", "AdBlock blocked request: ${request.url}")
                                return AdBlockEngine.createEmptyResponse()
                            }
                            if (mediaSnifferEnabled && url != null) {
                                inspectMediaStream(url, request?.requestHeaders)?.let { sniffed ->
                                    view?.post {
                                        onMediaSniffed?.invoke(sniffed)
                                    }
                                }
                            }
                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            android.util.Log.d("AeryoWebView", "onPageStarted: url=$url")
                            val isSsl = url?.startsWith("https://") == true
                            val newUrl = url ?: ""
                            lastLoadedUrl = newUrl
                            onTabUpdated(tabId) { current ->
                                if (current.url == "about:blank" && newUrl != "about:blank") current else {
                                    current.copy(
                                        url = newUrl,
                                        progress = 5,
                                        isLoading = true,
                                        isSslSecure = isSsl,
                                        canGoBack = view?.canGoBack() == true,
                                        canGoForward = view?.canGoForward() == true
                                    )
                                }
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            android.util.Log.d("AeryoWebView", "onPageFinished: url=$url, lastLoadedUrl=$lastLoadedUrl")
                            val finishedUrl = url ?: ""
                            lastLoadedUrl = finishedUrl
                            val isSsl = url?.startsWith("https://") == true
                            onTabUpdated(tabId) { current ->
                                if (current.url == "about:blank" && finishedUrl != "about:blank") current else {
                                    current.copy(
                                        url = finishedUrl,
                                        progress = 100,
                                        isLoading = false,
                                        isSslSecure = isSsl,
                                        canGoBack = view?.canGoBack() == true,
                                        canGoForward = view?.canGoForward() == true
                                    )
                                }
                            }
                            // 注入 CSS 广告拦截脚本与自定义 UserScript 引擎
                            view?.evaluateJavascript(AdBlockEngine.COSMETIC_CSS_SCRIPT, null)
                            if (view != null && !url.isNullOrEmpty()) {
                                net.zzbuaoye.aeryo.browser.script.UserScriptEngine.injectScriptsForUrl(view, url)
                                if (doNotTrackEnabled) {
                                    view.evaluateJavascript(
                                        "try { Object.defineProperty(navigator, 'doNotTrack', { get: function() { return '1'; } }); } catch (_) {}",
                                        null
                                    )
                                }
                                if (nightModeEnabled && smartDarkModeEnabled) {
                                    net.zzbuaoye.aeryo.browser.script.UserScriptEngine.applySmartDarkMode(view, true)
                                }
                            }
                            view?.postDelayed({
                                captureWebViewPreview(view)?.let { preview ->
                                    onTabUpdated(tabId) { current ->
                                        current.copy(preview = preview)
                                    }
                                }
                            }, 180L)
                        }

                        override fun doUpdateVisitedHistory(
                            view: WebView?,
                            url: String?,
                            isReload: Boolean
                        ) {
                            onTabUpdated(tabId) { current ->
                                if (current.url == "about:blank") {
                                    current.copy(canGoBack = false, canGoForward = false)
                                } else {
                                    current.copy(
                                        canGoBack = view?.canGoBack() == true,
                                        canGoForward = view?.canGoForward() == true
                                    )
                                }
                            }
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            onTabUpdated(tabId) { current -> current.copy(isSslSecure = false) }
                            handler?.cancel()
                        }
                    }

                    tab.webView = this

                    if (tab.url != "about:blank" && this.url != tab.url) {
                        lastLoadedUrl = tab.url
                        loadUrl(tab.url)
                    }
                }

                SwipeRefreshLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setColorSchemeColors(0xFF4D8DFF.toInt())
                    setOnChildScrollUpCallback { _, _ ->
                        webView.scrollY > 0
                    }
                    setOnRefreshListener {
                        webView.reload()
                    }
                    addView(webView)
                }
            },
            update = { swipeLayout ->
                val webView = (0 until swipeLayout.childCount)
                    .map { swipeLayout.getChildAt(it) }
                    .filterIsInstance<WebView>()
                    .firstOrNull() ?: return@AndroidView

                tab.webView = webView
                swipeLayout.isRefreshing = tab.isLoading && tab.url != "about:blank"
                swipeLayout.isEnabled = tab.url != "about:blank"
                if (tab.isDesktopMode) {
                    if (webView.settings.userAgentString != CHROME_DESKTOP_UA) {
                        webView.settings.userAgentString = CHROME_DESKTOP_UA
                        webView.reload()
                    }
                } else {
                    if (webView.settings.userAgentString == CHROME_DESKTOP_UA) {
                        webView.settings.userAgentString = null
                        webView.reload()
                    }
                }

                if (tab.navigationRequestId != handledNavigationRequestId) {
                    handledNavigationRequestId = tab.navigationRequestId
                    lastLoadedUrl = tab.url
                    webView.stopLoading()
                    if (tab.url == "about:blank") {
                        webView.loadUrl("about:blank")
                    } else if (webView.url != tab.url) {
                        webView.loadUrl(tab.url)
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    webView.settings.forceDark = if (nightModeEnabled) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
                }
                net.zzbuaoye.aeryo.browser.script.UserScriptEngine.applySmartDarkMode(webView, nightModeEnabled && smartDarkModeEnabled)
                val usesIncognitoProfile = tab.isIncognito && supportsIsolatedIncognitoProfile()
                cookieManagerFor(webView, usesIncognitoProfile)
                    .setAcceptThirdPartyCookies(webView, !blockThirdPartyCookies)
            }
        )
    }
}

private fun isBrowserUrl(url: String): Boolean {
    val scheme = runCatching { android.net.Uri.parse(url).scheme?.lowercase() }.getOrNull()
    return scheme == "http" || scheme == "https" || scheme == "aeryo" || scheme == "about" || scheme == "javascript" || scheme == "data" || scheme == "blob"
}

private fun canonicalBingSearchUrl(url: String): String? {
    val uri = runCatching { android.net.Uri.parse(url) }.getOrNull() ?: return null
    if (!uri.isHierarchical) return null

    val host = uri.host?.lowercase().orEmpty()
    if (host != "www.bing.com" && host != "cn.bing.com") return null

    val query = runCatching { uri.getQueryParameter("q") }.getOrNull()
        ?.takeIf(String::isNotBlank)
        ?: return null
    val marketCount = runCatching { uri.getQueryParameters("mkt").size }.getOrDefault(0)
    val isStableSearchPath = host == "www.bing.com" && uri.path == "/search" && marketCount <= 1
    if (isStableSearchPath) return null

    return android.net.Uri.Builder()
        .scheme("https")
        .authority("www.bing.com")
        .path("search")
        .appendQueryParameter("setlang", "zh-hans")
        .appendQueryParameter("cc", "cn")
        .appendQueryParameter("q", query)
        .build()
        .toString()
}
