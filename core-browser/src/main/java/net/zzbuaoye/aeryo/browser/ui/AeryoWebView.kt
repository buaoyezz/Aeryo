package net.zzbuaoye.aeryo.browser.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.http.SslError
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
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
    onTabUpdated: (String, (WebTab) -> WebTab) -> Unit,
    onDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) -> Unit,
    onCustomViewShow: (View, WebChromeClient.CustomViewCallback) -> Unit = { _, _ -> },
    onCustomViewHide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var lastLoadedUrl by remember { mutableStateOf(tab.url) }
    val tabId = tab.id

    key(tab.id) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                (tab.webView ?: WebView(context)).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    setSupportMultipleWindows(true)
                    domStorageEnabled = !tab.isIncognito
                    databaseEnabled = !tab.isIncognito
                    saveFormData = !tab.isIncognito
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    allowFileAccess = !tab.isIncognito
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        forceDark = if (nightModeEnabled) android.webkit.WebSettings.FORCE_DARK_ON else android.webkit.WebSettings.FORCE_DARK_OFF
                    }
                }

                if (tab.isIncognito) {
                    clearHistory()
                    clearFormData()
                }

                setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    onDownloadRequested(url, userAgent, contentDisposition, mimetype, contentLength)
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        onTabUpdated(tabId) { current ->
                            current.copy(
                                progress = newProgress.coerceIn(1, 100),
                                isLoading = newProgress < 100
                            )
                        }
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        if (!title.isNullOrEmpty()) {
                            onTabUpdated(tabId) { current -> current.copy(title = title) }
                        }
                    }

                    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                        if (icon != null) {
                            onTabUpdated(tabId) { current -> current.copy(favicon = icon) }
                        }
                    }

                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        val sourceWebView = view ?: return false
                        val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                        val popupWebView = WebView(sourceWebView.context).apply {
                            settings.javaScriptEnabled = true
                            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                                onDownloadRequested(
                                    url,
                                    userAgent,
                                    contentDisposition,
                                    mimetype,
                                    contentLength
                                )
                                destroy()
                            }
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    popup: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val targetUrl = request?.url?.toString() ?: return false
                                    sourceWebView.loadUrl(targetUrl)
                                    popup?.destroy()
                                    return true
                                }
                            }
                        }
                        transport.webView = popupWebView
                        resultMsg.sendToTarget()
                        return true
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
                            return AdBlockEngine.createEmptyResponse()
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        val isSsl = url?.startsWith("https://") == true
                        val newUrl = url ?: ""
                        lastLoadedUrl = newUrl
                        onTabUpdated(tabId) { current ->
                            if (current.url == "about:blank" && newUrl != "about:blank") {
                                current
                            } else {
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
                        val finishedUrl = url ?: ""
                        if (finishedUrl != lastLoadedUrl) return
                        val isSsl = url?.startsWith("https://") == true
                        onTabUpdated(tabId) { current ->
                            if (current.url == "about:blank" && finishedUrl != "about:blank") {
                                current
                            } else {
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
                            current.copy(
                                canGoBack = view?.canGoBack() == true,
                                canGoForward = view?.canGoForward() == true
                            )
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
            },
            update = { webView ->
                tab.webView = webView
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

                if (tab.url == "about:blank" && lastLoadedUrl != "about:blank") {
                    lastLoadedUrl = "about:blank"
                    webView.stopLoading()
                    webView.loadUrl("about:blank")
                } else if (tab.url != "about:blank" && tab.url != lastLoadedUrl) {
                    lastLoadedUrl = tab.url
                    webView.loadUrl(tab.url)
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    webView.settings.forceDark = if (nightModeEnabled) android.webkit.WebSettings.FORCE_DARK_ON else android.webkit.WebSettings.FORCE_DARK_OFF
                }
            }
        )
    }
}
