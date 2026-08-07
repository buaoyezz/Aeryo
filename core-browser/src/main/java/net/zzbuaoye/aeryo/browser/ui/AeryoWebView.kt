package net.zzbuaoye.aeryo.browser.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.zzbuaoye.aeryo.browser.adblock.AdBlockEngine
import net.zzbuaoye.aeryo.browser.model.WebTab

const val CHROME_DESKTOP_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

fun captureWebViewPreview(webView: WebView): Bitmap? {
    if (webView.width <= 0 || webView.height <= 0) return null

    val targetWidth = webView.width.coerceAtMost(360)
    val targetHeight = (
        webView.height.toFloat() * targetWidth / webView.width
        ).toInt().coerceIn(1, 640)
    return runCatching {
        Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.scale(
                targetWidth.toFloat() / webView.width,
                targetHeight.toFloat() / webView.height
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
    doNotTrackEnabled: Boolean = true,
    blockThirdPartyCookies: Boolean = false,
    onTabUpdated: (String, (WebTab) -> WebTab) -> Unit,
    onDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) -> Unit,
    onOpenExternalLink: (String) -> Boolean = { false },
    onCustomViewShow: (View, WebChromeClient.CustomViewCallback) -> Unit = { _, _ -> },
    onCustomViewHide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var lastLoadedUrl by remember { mutableStateOf(tab.url) }
    var handledNavigationRequestId by remember(tab.id) { mutableStateOf(tab.navigationRequestId) }
    val tabId = tab.id

    key(tab.id) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val webView = (tab.webView ?: WebView(context)).apply {
                    (parent as? ViewGroup)?.removeView(this)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    // 将 target="_blank" 和 window.open() 作为当前标签的顶层导航处理，
                    // 避免新窗口进入未挂载的 WebView 后停留在空白页。
                    setSupportMultipleWindows(false)
                    domStorageEnabled = !tab.isIncognito
                    databaseEnabled = !tab.isIncognito
                    saveFormData = !tab.isIncognito
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    allowFileAccess = false
                    allowContentAccess = false
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        allowFileAccessFromFileURLs = false
                        allowUniversalAccessFromFileURLs = false
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        forceDark = if (nightModeEnabled) android.webkit.WebSettings.FORCE_DARK_ON else android.webkit.WebSettings.FORCE_DARK_OFF
                    }
                }

                if (tab.isIncognito) {
                    clearHistory()
                    clearFormData()
                }
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, !blockThirdPartyCookies)

                setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    onDownloadRequested(url, userAgent, contentDisposition, mimetype, contentLength)
                }

                webChromeClient = object : WebChromeClient() {
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
                    webView.settings.forceDark = if (nightModeEnabled) android.webkit.WebSettings.FORCE_DARK_ON else android.webkit.WebSettings.FORCE_DARK_OFF
                }
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, !blockThirdPartyCookies)
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
