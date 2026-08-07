package net.zzbuaoye.aeryo.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebViewDatabase
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import net.zzbuaoye.aeryo.R
import net.zzbuaoye.aeryo.bookmarks.data.BookmarkDatabase
import net.zzbuaoye.aeryo.bookmarks.data.BookmarkEntity
import net.zzbuaoye.aeryo.bookmarks.data.HistoryEntity
import net.zzbuaoye.aeryo.bookmarks.data.PrivateHistoryEntity
import net.zzbuaoye.aeryo.bookmarks.ui.BookmarksScreen
import net.zzbuaoye.aeryo.bookmarks.ui.HistoryScreen
import net.zzbuaoye.aeryo.browser.TabManager
import net.zzbuaoye.aeryo.browser.adblock.AdBlockEngine
import net.zzbuaoye.aeryo.browser.adblock.AdBlockRuleManager
import net.zzbuaoye.aeryo.browser.model.WebTab
import net.zzbuaoye.aeryo.browser.ui.AeryoWebView
import net.zzbuaoye.aeryo.core.ui.aeryoWindowColor
import net.zzbuaoye.aeryo.browser.ui.captureWebViewPreview
import net.zzbuaoye.aeryo.downloads.AeryoDownloadManager
import net.zzbuaoye.aeryo.downloads.model.DownloadItem
import net.zzbuaoye.aeryo.downloads.model.DownloadRequest
import net.zzbuaoye.aeryo.downloads.ui.DownloadConfirmationDialog
import net.zzbuaoye.aeryo.downloads.ui.DownloadsScreen
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import net.zzbuaoye.aeryo.settings.ui.AboutScreen
import net.zzbuaoye.aeryo.settings.ui.AdBlockSettingsScreen
import net.zzbuaoye.aeryo.settings.ui.SettingsScreen
import net.zzbuaoye.aeryo.util.BiometricAuthHelper
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.Badge
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarDisplayMode
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.icon.extended.ChevronForward
import top.yukonga.miuix.kmp.icon.extended.Favorites
import top.yukonga.miuix.kmp.icon.extended.FavoritesFill
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.ListView
import top.yukonga.miuix.kmp.icon.extended.Lock
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Sidebar
import top.yukonga.miuix.kmp.shader.RuntimeShader
import top.yukonga.miuix.kmp.shader.asBrush
import top.yukonga.miuix.kmp.shader.isRenderEffectSupported
import top.yukonga.miuix.kmp.shader.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.ByteArrayOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun AeryoMainScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val rootView = LocalView.current
    val supportsVisualEffects = remember(context, rootView) {
        val activityManager = context.getSystemService(android.app.ActivityManager::class.java)
        rootView.isHardwareAccelerated && activityManager?.isLowRamDevice != true
    }
    val supportsLiquidGlass = supportsVisualEffects && isRuntimeShaderSupported()
    val supportsBackgroundBlur = supportsVisualEffects && isRenderEffectSupported()
    val scope = rememberCoroutineScope()

    val tabManager = remember { TabManager() }
    val downloadManager = remember { AeryoDownloadManager(context) }
    val dao = remember { BookmarkDatabase.getDatabase(context).bookmarkDao() }
    val preferences = remember { UserPreferences(context) }
    val recordedHistoryUrls = remember { mutableMapOf<String, String>() }
    val packageInfo = remember(context) {
        val flags = android.content.pm.PackageManager.GET_META_DATA
        context.packageManager.getPackageInfo(context.packageName, flags)
    }
    val appVersionName = packageInfo.versionName.orEmpty().ifBlank { "1.0.0" }
    @Suppress("DEPRECATION")
    val appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        packageInfo.versionCode.toLong()
    }
    val versionChannelName = remember(packageInfo, appVersionName) {
        val metadata = packageInfo.applicationInfo?.metaData
        val rawChannel = metadata?.getString("net.zzbuaoye.aeryo.CHANNEL").orEmpty().trim()
        val inferredChannel = rawChannel.ifBlank {
            when {
                appVersionName.contains("alpha", ignoreCase = true) -> "Alpha"
                appVersionName.contains("beta", ignoreCase = true) -> "Beta"
                appVersionName.contains("rc", ignoreCase = true) -> "RC"
                else -> ""
            }
        }
        inferredChannel.ifBlank { "Stable" }
    }
    val webViewVersion = remember {
        WebView.getCurrentWebViewPackage()?.versionName ?: "系统 WebView"
    }
    val appIcon = painterResource(id = R.drawable.aeryo_app_icon)

    val tabState by tabManager.state.collectAsState()
    val tabs = tabState.tabs
    val activeTabIndex = tabState.activeTabIndex
    val currentTab = tabState.currentTab
    val bookmarks by dao.getAllBookmarks().collectAsState(initial = emptyList())
    val favorites by dao.getAllFavorites().collectAsState(initial = emptyList())
    val history by dao.getAllHistory().collectAsState(initial = emptyList())
    val searchEngine by preferences.searchEngine.collectAsState(initial = UserPreferences.ENGINE_BING)
    val adBlockEnabled by preferences.adBlockEnabled.collectAsState(initial = true)
    val adBlockSources by preferences.adBlockSources.collectAsState(initial = emptyList())
    val adBlockAutoUpdateInterval by preferences.adBlockAutoUpdateInterval.collectAsState(initial = UserPreferences.AD_BLOCK_AUTO_UPDATE_3D)
    val menuOrder by preferences.menuOrder.collectAsState(initial = emptyList())
    val addressBarAnimationEnabled by preferences.addressBarAnimationEnabled.collectAsState(initial = true)
    val nightModeEnabled by preferences.nightModeEnabled.collectAsState(initial = false)
    val downloadMode by preferences.downloadMode.collectAsState(initial = UserPreferences.DOWNLOAD_MODE_BUILT_IN)
    val tabViewMode by preferences.tabViewMode.collectAsState(initial = UserPreferences.TAB_VIEW_MODE_GRID)
    val effectiveTabViewMode = when (tabViewMode) {
        UserPreferences.TAB_VIEW_MODE_HALF -> UserPreferences.TAB_VIEW_MODE_HALF
        else -> UserPreferences.TAB_VIEW_MODE_GRID
    }
    val themeMode by preferences.themeMode.collectAsState(initial = UserPreferences.THEME_MODE_SYSTEM)
    val themePalette by preferences.themePalette.collectAsState(initial = UserPreferences.THEME_PALETTE_TONAL_SPOT)
    val themeKeyColor by preferences.themeKeyColor.collectAsState(initial = UserPreferences.DEFAULT_THEME_KEY_COLOR)
    val glassEffectEnabled by preferences.glassEffectEnabled.collectAsState(initial = true)
    val blurEffectEnabled by preferences.blurEffectEnabled.collectAsState(initial = false)
    val effectiveGlassEffectEnabled = glassEffectEnabled && supportsLiquidGlass
    val effectiveBlurEffectEnabled = blurEffectEnabled && supportsBackgroundBlur

    LaunchedEffect(Unit) {
        restoreDefaultLauncherIcon(context)
        preferences.migrateSearchEngine()
    }

    LaunchedEffect(tabViewMode) {
        if (tabViewMode != UserPreferences.TAB_VIEW_MODE_GRID &&
            tabViewMode != UserPreferences.TAB_VIEW_MODE_HALF
        ) {
            preferences.setTabViewMode(UserPreferences.TAB_VIEW_MODE_GRID)
        }
    }
    val privacyBiometricEnabled by preferences.privacyBiometricEnabled.collectAsState(initial = false)
    val doNotTrackEnabled by preferences.doNotTrackEnabled.collectAsState(initial = true)
    val blockThirdPartyCookies by preferences.blockThirdPartyCookies.collectAsState(initial = false)
    val clearOnExit by preferences.clearOnExit.collectAsState(initial = false)
    val privateHistory by dao.getAllPrivateHistory().collectAsState(initial = emptyList())
    val activity = context as? FragmentActivity

    var showMenu by remember { mutableStateOf(false) }
    var showTabs by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showDownloads by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showAdBlockSettings by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showFind by remember { mutableStateOf(false) }
    var addressText by remember { mutableStateOf("") }
    var addressExpanded by remember { mutableStateOf(false) }
    val downloadItems = remember { mutableStateListOf<DownloadItem>() }
    var pendingDownloadRequest by remember { mutableStateOf<DownloadRequest?>(null) }
    var duplicateDownload by remember { mutableStateOf<DownloadItem?>(null) }

    var lastPrivacyAuthTime by remember { androidx.compose.runtime.mutableLongStateOf(0L) }

    val requestPrivacyAuth: (() -> Unit) -> Unit = { onSuccess ->
        val now = System.currentTimeMillis()
        if (privacyBiometricEnabled && activity != null) {
            if (now - lastPrivacyAuthTime < 60_000L) {
                onSuccess()
            } else {
                BiometricAuthHelper.authenticate(
                    activity = activity,
                    title = "隐私验证",
                    subtitle = "请验证设备指纹或凭据以继续",
                    onSuccess = {
                        lastPrivacyAuthTime = System.currentTimeMillis()
                        onSuccess()
                    }
                )
            }
        } else {
            onSuccess()
        }
    }

    val saveCurrentTabToPrivateHistory: () -> Unit = {
        currentTab?.let { tab ->
            if (tab.url.isNotBlank() && tab.url != "about:blank") {
                scope.launch {
                    dao.insertPrivateHistory(
                        PrivateHistoryEntity(
                            title = tab.title.takeUnless { it.isBlank() || it == "新标签页" || it == "主页" } ?: tab.url,
                            url = tab.url,
                            favicon = tab.favicon?.toPngBytes()
                        )
                    )
                    android.widget.Toast.makeText(context, "已保存至私密历史", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val saveCurrentTabToBookmark: () -> Unit = {
        currentTab?.let { tab ->
            if (tab.url.isNotBlank() && tab.url != "about:blank") {
                scope.launch {
                    dao.insertBookmark(
                        BookmarkEntity(
                            title = tab.title,
                            url = tab.url,
                            kind = BookmarkEntity.KIND_BOOKMARK
                        )
                    )
                    android.widget.Toast.makeText(context, "已添加到书签", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val navigateCurrentTab: (String) -> Unit = { input ->
        val target = processUrlInput(input, searchEngine)
        when (target) {
            "aeryo://settings" -> showSettings = true
            "aeryo://history" -> requestPrivacyAuth { showHistory = true }
            "aeryo://bookmarks" -> showBookmarks = true
            "aeryo://downloads" -> showDownloads = true
            "aeryo://about" -> showAbout = true
            "aeryo://adblock" -> showAdBlockSettings = true
            else -> {
                tabManager.updateCurrentTab {
                    it.copy(
                        url = target,
                        progress = 5,
                        isLoading = true,
                        navigationRequestId = it.navigationRequestId + 1
                    )
                }
            }
        }
    }

    val navigateHome: () -> Unit = {
        focusManager.clearFocus()
        showFind = false
        showMenu = false
        currentTab?.webView?.stopLoading()
        tabManager.updateCurrentTab {
            it.copy(
                url = "about:blank",
                title = "主页",
                progress = 0,
                isLoading = false,
                canGoBack = false,
                canGoForward = false,
                isSslSecure = false,
                preview = null,
                navigationRequestId = it.navigationRequestId + 1
            )
        }
    }

    LaunchedEffect(currentTab?.url, currentTab?.title, currentTab?.isLoading, searchEngine, addressExpanded) {
        if (!addressExpanded) {
            addressText = collapsedAddressText(currentTab)
        }
    }

    LaunchedEffect(addressExpanded) {
        if (!addressExpanded) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            context.getSystemService(InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(rootView.windowToken, 0)
        }
    }

    LaunchedEffect(currentTab?.id, currentTab?.url, currentTab?.title, currentTab?.isLoading) {
        currentTab?.let { tab ->
            if (tab.isLoading) {
                recordedHistoryUrls.remove(tab.id)
            } else if (
                tab.url.isNotBlank() &&
                tab.url != "about:blank" &&
                !tab.isIncognito &&
                recordedHistoryUrls[tab.id] != tab.url
            ) {
                dao.insertHistory(
                    HistoryEntity(
                        title = tab.title.takeUnless {
                            it.isBlank() || it == "新标签页" || it == "主页"
                        } ?: tab.url,
                        url = tab.url,
                        favicon = tab.favicon?.toPngBytes()
                    )
                )
                recordedHistoryUrls[tab.id] = tab.url
            }
        }
    }

    LaunchedEffect(currentTab?.id, currentTab?.url, currentTab?.favicon) {
        currentTab?.let { tab ->
            if (!tab.isIncognito && tab.url != "about:blank") {
                tab.favicon?.toPngBytes()?.let { favicon ->
                    dao.updateHistoryFaviconByUrl(tab.url, favicon)
                }
            }
        }
    }

    LaunchedEffect(adBlockEnabled) {
        AdBlockEngine.setEnabled(adBlockEnabled)
    }

    LaunchedEffect(adBlockSources) {
        val enabledIds = adBlockSources.filter { it.isEnabled }.map { it.id }
        AdBlockRuleManager.loadEnabledRules(context, enabledIds)
    }

    LaunchedEffect(adBlockEnabled, adBlockSources, adBlockAutoUpdateInterval) {
        if (adBlockEnabled && adBlockAutoUpdateInterval != UserPreferences.AD_BLOCK_AUTO_UPDATE_NEVER) {
            val intervalMs = when (adBlockAutoUpdateInterval) {
                UserPreferences.AD_BLOCK_AUTO_UPDATE_12H -> 12 * 3600 * 1000L
                UserPreferences.AD_BLOCK_AUTO_UPDATE_3D -> 3 * 24 * 3600 * 1000L
                UserPreferences.AD_BLOCK_AUTO_UPDATE_7D -> 7 * 24 * 3600 * 1000L
                UserPreferences.AD_BLOCK_AUTO_UPDATE_15D -> 15 * 24 * 3600 * 1000L
                UserPreferences.AD_BLOCK_AUTO_UPDATE_30D -> 30 * 24 * 3600 * 1000L
                else -> Long.MAX_VALUE
            }
            val now = System.currentTimeMillis()
            val expiredSources = adBlockSources.filter { it.isEnabled && (it.lastUpdated == 0L || now - it.lastUpdated > intervalMs) }
            if (expiredSources.isNotEmpty()) {
                var updatedSources = adBlockSources.toList()
                var hasUpdatedAny = false
                for (source in expiredSources) {
                    val success = AdBlockRuleManager.downloadRuleFile(context, source.id, source.url)
                    if (success) {
                        updatedSources = updatedSources.map {
                            if (it.id == source.id) it.copy(lastUpdated = System.currentTimeMillis()) else it
                        }
                        hasUpdatedAny = true
                    }
                }
                if (hasUpdatedAny) {
                    preferences.setAdBlockSources(updatedSources)
                    AdBlockRuleManager.loadEnabledRules(context, updatedSources.filter { it.isEnabled }.map { it.id })
                }
            }
        }
    }

    DisposableEffect(clearOnExit) {
        onDispose {
            if (clearOnExit) {
                scope.launch {
                    dao.clearHistory()
                    dao.clearPrivateHistory()
                    currentTab?.webView?.clearCache(true)
                    CookieManager.getInstance().removeAllCookies(null)
                    CookieManager.getInstance().flush()
                    WebStorage.getInstance().deleteAllData()
                }
            }
        }
    }

    LaunchedEffect(showDownloads) {
        if (showDownloads) {
            while (true) {
                downloadItems.clear()
                downloadItems.addAll(downloadManager.getAllDownloads())
                delay(1_000)
            }
        }
    }

    LaunchedEffect(currentTab?.isIncognito) {
        val isIncognito = currentTab?.isIncognito == true
        activity?.window?.let { window ->
            if (isIncognito) {
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    val hasCurrentPage = currentTab?.url?.let { it.isNotBlank() && it != "about:blank" } == true
    BackHandler(
        enabled = showMenu || showTabs || showBookmarks || showHistory || showDownloads ||
            showSettings || showAdBlockSettings || showAbout || showFind ||
            currentTab?.canGoBack == true || hasCurrentPage
    ) {
        when {
            showMenu -> showMenu = false
            showTabs -> showTabs = false
            showAbout -> showAbout = false
            showBookmarks -> showBookmarks = false
            showHistory -> showHistory = false
            showDownloads -> showDownloads = false
            showAdBlockSettings -> showAdBlockSettings = false
            showSettings -> showSettings = false
            showFind -> {
                currentTab?.webView?.clearMatches()
                showFind = false
            }
            currentTab?.canGoBack == true || hasCurrentPage -> {
                val navigated = currentTab?.webView?.let(::safeGoBack) == true
                if (!navigated) navigateHome()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    visible = currentTab?.url != "about:blank",
                    enter = if (addressBarAnimationEnabled) {
                        slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = folmeSpring(damping = 0.9f, response = 0.36f)
                        ) + fadeIn(animationSpec = tween(180))
                    } else {
                        EnterTransition.None
                    },
                    exit = if (addressBarAnimationEnabled) {
                        slideOutVertically(
                            targetOffsetY = { -it / 3 },
                            animationSpec = folmeSpring(damping = 1f, response = 0.28f)
                        ) + fadeOut(animationSpec = tween(140))
                    } else {
                        ExitTransition.None
                    }
                ) {
                    BrowserAddressBar(
                        tab = currentTab,
                        isHome = false,
                        text = addressText,
                        expanded = addressExpanded,
                        isBookmarked = rememberBookmarkState(dao, currentTab?.url.orEmpty()),
                        searchEngine = searchEngine,
                        glassEffectEnabled = effectiveGlassEffectEnabled,
                        blurEffectEnabled = effectiveBlurEffectEnabled,
                        onSearchEngineChange = { scope.launch { preferences.setSearchEngine(it) } },
                        onTextChange = { addressText = it },
                        onExpandedChange = { shouldExpand ->
                            if (shouldExpand && !addressExpanded) {
                                addressText = currentTab?.url
                                    ?.takeUnless { it.isBlank() || it == "about:blank" }
                                    .orEmpty()
                            }
                            addressExpanded = shouldExpand
                        },
                        onSubmit = {
                            val submittedText = it.trim()
                            addressExpanded = false
                            keyboardController?.hide()
                            focusManager.clearFocus(force = true)
                            navigateCurrentTab(submittedText)
                        },
                        onToggleBookmark = {
                            currentTab?.let { tab ->
                                if (tab.url != "about:blank" && tab.url.isNotBlank()) {
                                    scope.launch {
                                        if (dao.isBookmarkedNow(tab.url)) {
                                            dao.deleteBookmarkByUrl(tab.url)
                                        } else {
                                            dao.insertBookmark(
                                                BookmarkEntity(
                                                    title = tab.title,
                                                    url = tab.url,
                                                    kind = BookmarkEntity.KIND_FAVORITE
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        onSavePrivateHistory = saveCurrentTabToPrivateHistory
                    )
                }
            },
            bottomBar = {
                BrowserNavigationBar(
                    tab = currentTab,
                    tabCount = tabs.size,
                    onBack = {
                        val navigated = currentTab?.webView?.let(::safeGoBack) == true
                        if (!navigated) navigateHome()
                    },
                    onForward = {
                        currentTab?.let { tab ->
                            tab.webView?.takeIf(WebView::canGoForward)?.goForward()
                        }
                    },
                    onHome = navigateHome,
                    onTabs = {
                        addressExpanded = false
                        showTabs = true
                        currentTab?.webView?.let { webView ->
                            webView.post {
                                captureWebViewPreview(webView)?.let { preview ->
                                    tabManager.updateTab(currentTab.id) {
                                        it.copy(preview = preview)
                                    }
                                }
                            }
                        }
                    },
                    onNewIncognitoTab = {
                        requestPrivacyAuth {
                            tabManager.createNewTab(isIncognito = true)
                            android.widget.Toast.makeText(context, "已为您开启无痕新标签页", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    onMenu = {
                        addressExpanded = false
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        showMenu = true
                    }
                )
            },
            containerColor = if (currentTab?.isIncognito == true) {
                androidx.compose.ui.graphics.Color(0xFF0F0E17)
            } else {
                aeryoWindowColor()
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab?.url == "about:blank",
                    transitionSpec = {
                        if (addressBarAnimationEnabled) {
                            (
                                fadeIn(animationSpec = tween(220)) +
                                    scaleIn(
                                        initialScale = 0.985f,
                                        animationSpec = folmeSpring(damping = 0.92f, response = 0.36f)
                                    )
                                ).togetherWith(
                                fadeOut(animationSpec = tween(140)) +
                                    scaleOut(targetScale = 1.015f, animationSpec = tween(180))
                            )
                        } else {
                            EnterTransition.None.togetherWith(ExitTransition.None)
                        }
                    },
                    label = "browser-content",
                    modifier = Modifier.fillMaxSize()
                ) { isHome ->
                    when {
                        currentTab == null -> Unit
                        isHome -> AeryoHomeScreen(
                            animationEnabled = addressBarAnimationEnabled,
                            glassEffectEnabled = effectiveGlassEffectEnabled,
                            blurEffectEnabled = effectiveBlurEffectEnabled,
                            searchEngine = searchEngine,
                            onSearchEngineChange = { scope.launch { preferences.setSearchEngine(it) } },
                            onSearchSubmit = navigateCurrentTab
                        )
                        else -> AeryoWebView(
                            tab = currentTab,
                            onTabUpdated = tabManager::updateTab,
                            doNotTrackEnabled = doNotTrackEnabled,
                            blockThirdPartyCookies = blockThirdPartyCookies,
                            onOpenExternalLink = { url ->
                                val opened = openExternalLink(context, url)
                                if (!opened) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "没有可打开此链接的应用",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                                opened
                            },
                            onDownloadRequested = { url, userAgent, disposition, mimeType, contentLength ->
                                val request = downloadManager.createRequest(
                                    url = url,
                                    userAgent = userAgent,
                                    contentDisposition = disposition,
                                    mimeType = mimeType,
                                    referer = currentTab?.webView?.url,
                                    contentLength = contentLength
                                )
                                duplicateDownload = downloadManager.findDuplicate(request)
                                pendingDownloadRequest = request
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showFind,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = folmeSpring(damping = 0.88f, response = 0.36f)
                    ) + fadeIn(animationSpec = tween(180)),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = folmeSpring(damping = 1f, response = 0.28f)
                    ) + fadeOut(animationSpec = tween(120)),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    FindInPageBar(
                        onQueryChanged = { currentTab?.webView?.findAllAsync(it) },
                        onFindNext = { currentTab?.webView?.findNext(true) },
                        onFindPrevious = { currentTab?.webView?.findNext(false) },
                        onClose = {
                            currentTab?.webView?.clearMatches()
                            showFind = false
                        }
                    )
                }

                AeryoMenuBottomSheet(
                    show = showMenu,
                    isIncognito = currentTab?.isIncognito == true,
                    isDesktopMode = currentTab?.isDesktopMode == true,
                    isAdBlockEnabled = adBlockEnabled,
                    isNightModeEnabled = nightModeEnabled,
                    currentSearchEngine = searchEngine,
                    currentSearchQuery = extractSearchQueryFromUrl(currentTab?.url.orEmpty()) ?: if (addressText.isNotBlank() && !addressText.startsWith("http") && !addressText.startsWith("aeryo://")) addressText else "",
                    menuOrder = menuOrder,
                    onNewTab = {
                        tabManager.createNewTab()
                        showMenu = false
                    },
                    onNavigateToBookmarks = {
                        showMenu = false
                        showBookmarks = true
                    },
                    onNavigateToHistory = {
                        showMenu = false
                        requestPrivacyAuth {
                            showHistory = true
                        }
                    },
                    onNavigateToDownloads = {
                        showMenu = false
                        showDownloads = true
                    },
                    onNavigateToSettings = {
                        showMenu = false
                        showSettings = true
                    },
                    onSavePrivateHistory = saveCurrentTabToPrivateHistory,
                    onSaveBookmark = saveCurrentTabToBookmark,
                    onSharePage = {
                        currentTab?.let { tab ->
                            if (tab.url.isNotBlank() && tab.url != "about:blank") {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TITLE, tab.title)
                                    putExtra(Intent.EXTRA_TEXT, "${tab.title}\n${tab.url}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "分享网页"))
                            }
                        }
                    },
                    onIncognitoChanged = { enabled ->
                        if (enabled) {
                            requestPrivacyAuth {
                                tabManager.updateCurrentTab { it.copy(isIncognito = true) }
                            }
                        } else {
                            tabManager.updateCurrentTab { it.copy(isIncognito = false) }
                        }
                    },
                    onDesktopModeChanged = { enabled ->
                        tabManager.updateCurrentTab { it.copy(isDesktopMode = enabled) }
                    },
                    onAdBlockChanged = { enabled ->
                        scope.launch { preferences.setAdBlockEnabled(enabled) }
                    },
                    onNightModeChanged = { enabled ->
                        scope.launch { preferences.setNightModeEnabled(enabled) }
                    },
                    onSearchEngineSelected = { engine ->
                        scope.launch { preferences.setSearchEngine(engine) }
                        val query = extractSearchQueryFromUrl(currentTab?.url.orEmpty()) ?: if (addressText.isNotBlank() && !addressText.startsWith("http") && !addressText.startsWith("aeryo://")) addressText else ""
                        if (query.isNotBlank()) {
                            val target = buildSearchUrl(engine, query)
                            tabManager.updateCurrentTab {
                                it.copy(
                                    url = target,
                                    progress = 5,
                                    isLoading = true,
                                    navigationRequestId = it.navigationRequestId + 1
                                )
                            }
                        }
                    },
                    onMenuOrderChanged = {
                        scope.launch { preferences.setMenuOrder(it) }
                    },
                    onRefresh = {
                        currentTab?.webView?.reload()
                        showMenu = false
                    },
                    onFindInPage = {
                        showMenu = false
                        showFind = true
                    },
                    onDismiss = { showMenu = false }
                )

                pendingDownloadRequest?.let { request ->
                    DownloadConfirmationDialog(
                        request = request,
                        duplicate = duplicateDownload,
                        useBuiltIn = downloadMode == UserPreferences.DOWNLOAD_MODE_BUILT_IN,
                        onDismiss = {
                            pendingDownloadRequest = null
                            duplicateDownload = null
                        },
                        onConfirm = {
                            downloadManager.startDownload(
                                request = request,
                                useBuiltIn = downloadMode == UserPreferences.DOWNLOAD_MODE_BUILT_IN
                            )
                            pendingDownloadRequest = null
                            duplicateDownload = null
                        }
                    )
                }

                OverlayBottomSheet(
                    show = showTabs && effectiveTabViewMode == UserPreferences.TAB_VIEW_MODE_HALF,
                    title = null,
                    onDismissRequest = { showTabs = false }
                ) {
                    TabSwitcherBottomSheetContent(
                        tabs = tabs,
                        activeTabIndex = activeTabIndex,
                        tabViewMode = effectiveTabViewMode,
                        onTabViewModeChanged = { mode ->
                            scope.launch { preferences.setTabViewMode(mode) }
                        },
                        onTabSelected = { index ->
                            val targetTab = tabs.getOrNull(index)
                            if (targetTab?.isIncognito == true) {
                                requestPrivacyAuth {
                                    tabManager.selectTab(index)
                                    showTabs = false
                                }
                            } else {
                                tabManager.selectTab(index)
                                showTabs = false
                            }
                        },
                        onTabClosed = tabManager::closeTab,
                        onNewTab = { isIncognito ->
                            if (isIncognito) {
                                requestPrivacyAuth {
                                    tabManager.createNewTab(isIncognito = true)
                                    showTabs = false
                                }
                            } else {
                                tabManager.createNewTab(isIncognito = false)
                                showTabs = false
                            }
                        },
                        onCloseAllTabs = {
                            tabManager.closeAllTabs()
                        },
                        onCloseIncognitoTabs = {
                            tabManager.closeIncognitoTabs()
                            android.widget.Toast.makeText(context, "所有无痕痕迹与会话已安全抹去", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onDismiss = { showTabs = false }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showTabs && effectiveTabViewMode == UserPreferences.TAB_VIEW_MODE_GRID,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            TabSwitcherScreen(
                tabs = tabs,
                activeTabIndex = activeTabIndex,
                tabViewMode = effectiveTabViewMode,
                onTabViewModeChanged = { mode ->
                    scope.launch { preferences.setTabViewMode(mode) }
                },
                onTabSelected = { index ->
                    val targetTab = tabs.getOrNull(index)
                    if (targetTab?.isIncognito == true) {
                        requestPrivacyAuth {
                            tabManager.selectTab(index)
                            showTabs = false
                        }
                    } else {
                        tabManager.selectTab(index)
                        showTabs = false
                    }
                },
                onTabClosed = tabManager::closeTab,
                onNewTab = { isIncognito ->
                    if (isIncognito) {
                        requestPrivacyAuth {
                            tabManager.createNewTab(isIncognito = true)
                            showTabs = false
                        }
                    } else {
                        tabManager.createNewTab(isIncognito = false)
                        showTabs = false
                    }
                },
                onCloseAllTabs = {
                    tabManager.closeAllTabs()
                },
                onCloseIncognitoTabs = {
                    tabManager.closeIncognitoTabs()
                    android.widget.Toast.makeText(context, "所有无痕痕迹与会话已安全抹去", android.widget.Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showTabs = false }
            )
        }

        AnimatedVisibility(
            visible = showBookmarks,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            BookmarksScreen(
                bookmarks = bookmarks,
                favorites = favorites,
                onUrlSelected = {
                    navigateCurrentTab(it)
                    showBookmarks = false
                },
                onDeleteBookmark = { scope.launch { dao.deleteBookmark(it) } },
                onBack = { showBookmarks = false }
            )
        }

        AnimatedVisibility(
            visible = showHistory,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            HistoryScreen(
                history = history,
                privateHistory = privateHistory,
                onUrlSelected = {
                    navigateCurrentTab(it)
                    showHistory = false
                },
                onDeleteHistory = { scope.launch { dao.deleteHistory(it) } },
                onDeletePrivateHistory = { scope.launch { dao.deletePrivateHistory(it) } },
                onFaviconLoaded = { item, favicon ->
                    scope.launch { dao.updateHistoryFaviconByUrl(item.url, favicon) }
                },
                onClearAllHistory = { scope.launch { dao.clearHistory() } },
                onClearAllPrivateHistory = { scope.launch { dao.clearPrivateHistory() } },
                onBack = { showHistory = false }
            )
        }

        AnimatedVisibility(
            visible = showDownloads,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            DownloadsScreen(
                downloads = downloadItems,
                onOpenDownload = downloadManager::openDownload,
                onDeleteDownload = { item ->
                    downloadManager.removeDownload(item)
                    downloadItems.remove(item)
                },
                onPauseDownload = downloadManager::pauseDownload,
                onResumeDownload = downloadManager::resumeDownload,
                onBack = { showDownloads = false }
            )
        }

        AnimatedVisibility(
            visible = showSettings,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(
                currentSearchEngine = searchEngine,
                currentAddressBarAnimationEnabled = addressBarAnimationEnabled,
                currentDownloadMode = downloadMode,
                currentThemeMode = themeMode,
                currentThemePalette = themePalette,
                currentThemeKeyColor = themeKeyColor,
                currentPrivacyBiometricEnabled = privacyBiometricEnabled,
                currentDoNotTrackEnabled = doNotTrackEnabled,
                currentBlockThirdPartyCookies = blockThirdPartyCookies,
                currentClearOnExit = clearOnExit,
                onSearchEngineChanged = { scope.launch { preferences.setSearchEngine(it) } },
                onAdBlockSettingsClicked = { showAdBlockSettings = true },
                onAddressBarAnimationToggled = {
                    scope.launch { preferences.setAddressBarAnimationEnabled(it) }
                },
                onDownloadModeChanged = { mode ->
                    scope.launch { preferences.setDownloadMode(mode) }
                },
                onThemeModeChanged = { mode ->
                    scope.launch { preferences.setThemeMode(mode) }
                },
                onThemePaletteChanged = { palette ->
                    scope.launch { preferences.setThemePalette(palette) }
                },
                onThemeKeyColorChanged = { color ->
                    scope.launch { preferences.setThemeKeyColor(color) }
                },
                onPrivacyBiometricToggled = { enabled ->
                    requestPrivacyAuth {
                        scope.launch { preferences.setPrivacyBiometricEnabled(enabled) }
                    }
                },
                onDoNotTrackToggled = { enabled ->
                    scope.launch { preferences.setDoNotTrackEnabled(enabled) }
                },
                onBlockThirdPartyCookiesToggled = { enabled ->
                    scope.launch { preferences.setBlockThirdPartyCookies(enabled) }
                },
                onClearOnExitToggled = { enabled ->
                    scope.launch { preferences.setClearOnExit(enabled) }
                },
                onClearData = {
                    scope.launch {
                        dao.clearHistory()
                        dao.clearPrivateHistory()
                        currentTab?.webView?.apply {
                            clearHistory()
                            clearFormData()
                            clearCache(true)
                        }
                        CookieManager.getInstance().removeAllCookies(null)
                        CookieManager.getInstance().flush()
                        WebStorage.getInstance().deleteAllData()
                        WebViewDatabase.getInstance(context).clearFormData()
                        WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword()
                        android.widget.Toast.makeText(
                            context,
                            "浏览数据已清除",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onOpenAbout = { showAbout = true },
                onBack = { showSettings = false }
            )
        }

        AnimatedVisibility(
            visible = showAdBlockSettings,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            AdBlockSettingsScreen(
                currentAdBlockEnabled = adBlockEnabled,
                sources = adBlockSources,
                currentAutoUpdateInterval = adBlockAutoUpdateInterval,
                blockedRequestCount = AdBlockEngine.getBlockedRequestCount(),
                onAdBlockToggled = { scope.launch { preferences.setAdBlockEnabled(it) } },
                onSourcesUpdated = { scope.launch { preferences.setAdBlockSources(it) } },
                onAutoUpdateIntervalChanged = { scope.launch { preferences.setAdBlockAutoUpdateInterval(it) } },
                onRequestUpdateAll = {
                    scope.launch {
                        val enabledSources = adBlockSources.filter { it.isEnabled }
                        var updatedSources = adBlockSources.toList()
                        for (source in enabledSources) {
                            val success = AdBlockRuleManager.downloadRuleFile(context, source.id, source.url)
                            if (success) {
                                updatedSources = updatedSources.map {
                                    if (it.id == source.id) it.copy(lastUpdated = System.currentTimeMillis()) else it
                                }
                            }
                        }
                        preferences.setAdBlockSources(updatedSources)
                        AdBlockRuleManager.loadEnabledRules(context, enabledSources.map { it.id })
                    }
                },
                onRequestDownload = { source ->
                    scope.launch {
                        val success = AdBlockRuleManager.downloadRuleFile(context, source.id, source.url)
                        if (success) {
                            val updatedSources = adBlockSources.map {
                                if (it.id == source.id) it.copy(lastUpdated = System.currentTimeMillis()) else it
                            }
                            preferences.setAdBlockSources(updatedSources)
                            AdBlockRuleManager.loadEnabledRules(context, updatedSources.filter { it.isEnabled }.map { it.id })
                        }
                    }
                },
                onBack = { showAdBlockSettings = false }
            )
        }

        AnimatedVisibility(
            visible = showAbout,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            AboutScreen(
                appIcon = appIcon,
                versionName = appVersionName,
                versionCode = appVersionCode,
                packageName = context.packageName,
                webViewVersion = webViewVersion,
                versionChannelName = versionChannelName,
                onBack = { showAbout = false }
            )
        }

    }
}

private fun android.graphics.Bitmap.toPngBytes(): ByteArray? {
    return ByteArrayOutputStream().use { output ->
        if (compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output)) {
            output.toByteArray()
        } else {
            null
        }
    }
}

private const val AERYO_GLASS_SHADER = """
uniform float2 uResolution;
layout(color) uniform float4 uSurface;
layout(color) uniform float4 uAccent;
uniform float uLiquid;
uniform float uFrost;

half4 main(float2 fragCoord) {
    float2 resolution = max(uResolution, float2(1.0));
    float2 uv = fragCoord / resolution;
    float diagonal = smoothstep(1.05, 0.08, uv.x + uv.y * 0.55);
    float lowerGlow = smoothstep(0.98, 0.28, distance(uv, float2(0.78, 1.08)));
    float grain = fract(sin(dot(floor(fragCoord * 0.55), float2(12.9898, 78.233))) * 43758.5453);
    float3 color = uSurface.rgb;
    color += uAccent.rgb * diagonal * 0.12 * uLiquid;
    color += float3(1.0) * lowerGlow * 0.055 * uLiquid;
    color += (grain - 0.5) * 0.032 * uFrost;
    float alpha = clamp(uSurface.a + 0.04 * uLiquid + 0.06 * uFrost, 0.0, 1.0);
    return half4(half3(clamp(color, 0.0, 1.0)), half(alpha));
}
"""

@Composable
private fun AeryoGlassSurface(
    liquidGlassEnabled: Boolean,
    blurEnabled: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val surfaceColor = when {
        blurEnabled -> MiuixTheme.colorScheme.surface.copy(alpha = 0.78f)
        liquidGlassEnabled -> MiuixTheme.colorScheme.surface.copy(alpha = 0.86f)
        else -> MiuixTheme.colorScheme.surfaceContainerHigh
    }
    val accentColor = MiuixTheme.colorScheme.primary
    val shader = remember(liquidGlassEnabled, blurEnabled) {
        if ((liquidGlassEnabled || blurEnabled) && isRuntimeShaderSupported()) {
            runCatching { RuntimeShader(AERYO_GLASS_SHADER) }.getOrNull()
        } else {
            null
        }
    }
    val shaderBrush = remember(shader) { shader?.asBrush() }
    val fallbackBrush = remember(surfaceColor, accentColor, liquidGlassEnabled, blurEnabled) {
        Brush.linearGradient(
            colors = listOf(
                if (liquidGlassEnabled) accentColor.copy(alpha = 0.18f) else surfaceColor,
                surfaceColor,
                if (blurEnabled) Color.White.copy(alpha = 0.08f) else surfaceColor
            ),
            start = Offset.Zero,
            end = Offset(900f, 420f)
        )
    }
    Box(
        modifier = modifier
            .clip(shape)
            .background(surfaceColor)
            .border(
                width = 0.75.dp,
                color = Color.White.copy(alpha = if (liquidGlassEnabled) 0.26f else if (blurEnabled) 0.14f else 0f),
                shape = shape
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            if (shader != null && shaderBrush != null) {
                shader.setFloatUniform("uResolution", size.width, size.height)
                shader.setColorUniform("uSurface", surfaceColor)
                shader.setColorUniform("uAccent", accentColor)
                shader.setFloatUniform("uLiquid", if (liquidGlassEnabled) 1f else 0f)
                shader.setFloatUniform("uFrost", if (blurEnabled) 1f else 0f)
                drawRect(shaderBrush)
            } else if (liquidGlassEnabled || blurEnabled) {
                drawRect(fallbackBrush)
            }
        }
        content()
    }
}

@Composable
private fun AeryoSearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    selectAllOnExpand: Boolean = false,
    liquidGlassEnabled: Boolean = false,
    blurEnabled: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var fieldValue by remember { mutableStateOf(TextFieldValue(query, selection = TextRange(query.length))) }

    LaunchedEffect(query) {
        if (fieldValue.text != query) {
            fieldValue = TextFieldValue(query, selection = TextRange(query.length))
        }
    }
    LaunchedEffect(expanded) {
        if (expanded) {
            fieldValue = fieldValue.copy(
                selection = if (selectAllOnExpand) TextRange(0, fieldValue.text.length) else TextRange(fieldValue.text.length)
            )
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    BasicTextField(
        value = fieldValue,
        onValueChange = { value ->
            fieldValue = value
            onQueryChange(value.text)
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                if (state.isFocused && !expanded) onExpandedChange(true)
            },
        enabled = enabled,
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main.copy(
            color = MiuixTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        ),
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(fieldValue.text) }),
        decorationBox = { innerTextField ->
            AeryoGlassSurface(
                liquidGlassEnabled = liquidGlassEnabled,
                blurEnabled = blurEnabled,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leadingIcon?.invoke()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (fieldValue.text.isEmpty() && !expanded) {
                            Text(
                                text = label,
                                color = MiuixTheme.colorScheme.onSurfaceContainerHigh,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                    trailingIcon?.invoke()
                }
            }
        }
    )
}

@Composable
private fun BrowserAddressBar(
    tab: WebTab?,
    isHome: Boolean,
    text: String,
    expanded: Boolean,
    isBookmarked: Boolean,
    searchEngine: String = UserPreferences.ENGINE_BING,
    glassEffectEnabled: Boolean = true,
    blurEffectEnabled: Boolean = false,
    onSearchEngineChange: (String) -> Unit = {},
    onTextChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onSubmit: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    onSavePrivateHistory: () -> Unit = {}
) {
    val isIncognito = tab?.isIncognito == true
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(expanded, text, searchEngine) {
        if (!expanded || !isSearchQueryInput(text)) {
            suggestions = emptyList()
        } else {
            delay(220)
            suggestions = fetchSearchSuggestions(searchEngine, text)
        }
    }
    AeryoGlassSurface(
        liquidGlassEnabled = glassEffectEnabled,
        blurEnabled = blurEffectEnabled,
        shape = androidx.compose.ui.graphics.RectangleShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isIncognito) androidx.compose.ui.graphics.Color(0xFF181524).copy(alpha = if (glassEffectEnabled || blurEffectEnabled) 0.88f else 1f)
                    else if (glassEffectEnabled || blurEffectEnabled) Color.Transparent
                    else MiuixTheme.colorScheme.background
                )
                .statusBarsPadding()
        ) {
        AeryoSearchInputField(
            query = text,
            onQueryChange = onTextChange,
            onSearch = onSubmit,
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            selectAllOnExpand = true,
            label = if (tab?.url == "about:blank") {
                if (isIncognito) "无痕模式 - 搜索或输入网址" else "搜索或输入网址"
            } else collapsedAddressText(tab),
            liquidGlassEnabled = glassEffectEnabled,
            blurEnabled = blurEffectEnabled,
                leadingIcon = {
                    Icon(
                        imageVector = when {
                            isIncognito -> MiuixIcons.Lock
                            tab?.isSslSecure == true -> MiuixIcons.Lock
                            else -> MiuixIcons.Search
                        },
                        contentDescription = when {
                            isIncognito -> "无痕保护"
                            tab?.isSslSecure == true -> "安全连接"
                            else -> "搜索"
                        },
                        tint = when {
                            isIncognito -> androidx.compose.ui.graphics.Color(0xFFB69DF8)
                            tab?.isSslSecure == true -> MiuixTheme.colorScheme.primary
                            else -> MiuixTheme.colorScheme.onSurfaceContainerHigh
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .padding(start = 16.dp, end = 8.dp)
                    )
                },
            trailingIcon = if (isHome) {
                    {
                        SearchEngineSwitchButton(
                            currentEngine = searchEngine,
                            isInputting = expanded,
                            onEngineChanged = onSearchEngineChange
                        )
                    }
                } else {
                    {
                        if (expanded) {
                            SearchEngineSwitchButton(
                                currentEngine = searchEngine,
                                isInputting = expanded,
                                onEngineChanged = onSearchEngineChange
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isIncognito) {
                                    IconButton(
                                        onClick = onSavePrivateHistory,
                                        modifier = Modifier.padding(end = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = MiuixIcons.Lock,
                                            contentDescription = "存至私密历史",
                                            tint = androidx.compose.ui.graphics.Color(0xFFB69DF8)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = onToggleBookmark,
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    AnimatedContent(
                                        targetState = isBookmarked,
                                        transitionSpec = {
                                            (
                                                fadeIn(animationSpec = tween(180)) +
                                                    scaleIn(
                                                        initialScale = 0.72f,
                                                        animationSpec = folmeSpring(damping = 0.72f, response = 0.35f)
                                                    )
                                                ).togetherWith(
                                                fadeOut(animationSpec = tween(100)) +
                                                    scaleOut(targetScale = 0.8f, animationSpec = tween(120))
                                            )
                                        },
                                        label = "bookmark-state"
                                    ) { bookmarked ->
                                        Icon(
                                            imageVector = if (bookmarked) {
                                                MiuixIcons.FavoritesFill
                                            } else {
                                                MiuixIcons.Favorites
                                            },
                                            contentDescription = if (bookmarked) "取消收藏" else "收藏",
                                            tint = if (bookmarked) {
                                                MiuixTheme.colorScheme.primary
                                            } else {
                                                MiuixTheme.colorScheme.onSurfaceContainerHigh
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
        if (expanded && suggestions.isNotEmpty()) {
            SearchSuggestionPopup(
                suggestions = suggestions,
                blurEffectEnabled = blurEffectEnabled,
                onSuggestionSelected = { suggestion ->
                    onTextChange(suggestion)
                    onExpandedChange(false)
                    onSubmit(suggestion)
                }
            )
        }
        AnimatedVisibility(
            visible = tab?.isLoading == true,
            enter = fadeIn(animationSpec = tween(180)),
            exit = fadeOut(animationSpec = tween(180))
        ) {
            LinearProgressIndicator(
                progress = (tab?.progress ?: 0).coerceIn(0, 100) / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )
        }
    }
}
}

@Composable
private fun BrowserNavigationBar(
    tab: WebTab?,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onTabs: () -> Unit,
    onNewIncognitoTab: () -> Unit = {},
    onMenu: () -> Unit
) {
    val isIncognito = tab?.isIncognito == true
    val canGoBack = tab?.canGoBack == true ||
        tab?.url?.let { it.isNotBlank() && it != "about:blank" } == true
    val canGoForward = tab?.canGoForward == true
    val activeColor = if (isIncognito) androidx.compose.ui.graphics.Color.White else MiuixTheme.colorScheme.onSurface
    val disabledColor = if (isIncognito) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f) else MiuixTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(if (isIncognito) androidx.compose.ui.graphics.Color(0xFF14121E) else MiuixTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                enabled = canGoBack,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = MiuixIcons.ChevronBackward,
                    contentDescription = "后退",
                    tint = if (canGoBack) activeColor else disabledColor
                )
            }
            IconButton(
                onClick = onForward,
                enabled = canGoForward,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = MiuixIcons.ChevronForward,
                    contentDescription = "前进",
                    tint = if (canGoForward) activeColor else disabledColor
                )
            }
            IconButton(
                onClick = onHome,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = MiuixIcons.Home,
                    contentDescription = "主页",
                    tint = activeColor
                )
            }
            IconButton(
                onClick = onTabs,
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = MiuixIcons.GridView,
                        contentDescription = "标签页",
                        tint = activeColor
                    )
                    if (tabCount > 1) {
                        Badge(
                            containerColor = if (isIncognito) androidx.compose.ui.graphics.Color(0xFF9C27B0) else MiuixTheme.colorScheme.primary,
                            contentColor = if (isIncognito) androidx.compose.ui.graphics.Color.White else MiuixTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-4).dp)
                        ) {
                            Text(
                                text = tabCount.coerceAtMost(99).toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = onMenu,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = MiuixIcons.ListView,
                    contentDescription = "菜单",
                    tint = activeColor
                )
            }
        }
    }
}

@Composable
private fun AeryoHomeScreen(
    animationEnabled: Boolean,
    glassEffectEnabled: Boolean = false,
    blurEffectEnabled: Boolean = false,
    searchEngine: String = UserPreferences.ENGINE_BING,
    onSearchEngineChange: (String) -> Unit = {},
    onSearchSubmit: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var searchInputGeneration by remember { mutableStateOf(0) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(expanded, query, searchEngine) {
        if (!expanded || !isSearchQueryInput(query)) {
            suggestions = emptyList()
        } else {
            delay(220)
            suggestions = fetchSearchSuggestions(searchEngine, query)
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 28.dp)
    ) {
        val dismissSearch = {
            expanded = false
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
        }
        val centeredSearchY = maxHeight * 0.38f
        val searchOffsetY by animateDpAsState(
            targetValue = if (expanded) 10.dp else centeredSearchY,
            animationSpec = if (animationEnabled) {
                folmeSpring(damping = 0.86f, response = 0.42f)
            } else {
                snap()
            },
            label = "home-address-bar-offset"
        )
        val logoAlpha by animateFloatAsState(
            targetValue = if (expanded) 0f else 1f,
            animationSpec = if (animationEnabled) tween(170) else snap(),
            label = "home-logo-alpha"
        )
        val logoScale by animateFloatAsState(
            targetValue = if (expanded) 0.94f else 1f,
            animationSpec = if (animationEnabled) {
                folmeSpring(damping = 0.9f, response = 0.34f)
            } else {
                snap()
            },
            label = "home-logo-scale"
        )

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = dismissSearch
                    )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = centeredSearchY - 128.dp)
                .size(108.dp)
                .graphicsLayer {
                    alpha = logoAlpha
                    scaleX = logoScale
                    scaleY = logoScale
                },
            contentAlignment = Alignment.Center
        ) {
            AeryoHomeLogo()
        }
        key(searchInputGeneration) {
            AeryoSearchInputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { value ->
                    if (value.isNotBlank()) {
                        query = ""
                        searchInputGeneration++
                        expanded = false
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                        onSearchSubmit(value)
                    }
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                label = "搜索或输入网址",
                liquidGlassEnabled = glassEffectEnabled,
                blurEnabled = blurEffectEnabled,
                leadingIcon = {
                    Icon(
                        imageVector = MiuixIcons.Search,
                        contentDescription = "搜索",
                        tint = MiuixTheme.colorScheme.onSurfaceContainerHigh,
                        modifier = Modifier
                            .size(44.dp)
                            .padding(start = 16.dp, end = 8.dp)
                    )
                },
                trailingIcon = if (expanded) {
                    {
                        SearchEngineSwitchButton(
                            currentEngine = searchEngine,
                            isInputting = expanded,
                            onEngineChanged = onSearchEngineChange
                        )
                    }
                } else null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = searchOffsetY)
                    .fillMaxWidth()
            )
        }
        if (expanded && suggestions.isNotEmpty()) {
            SearchSuggestionPopup(
                suggestions = suggestions,
                blurEffectEnabled = blurEffectEnabled,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = searchOffsetY + 66.dp)
                    .fillMaxWidth(),
                onSuggestionSelected = { suggestion ->
                    query = ""
                    searchInputGeneration++
                    expanded = false
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                    onSearchSubmit(suggestion)
                }
            )
        }
        
        BackHandler(enabled = expanded) {
            dismissSearch()
        }
    }
}

@Composable
private fun AeryoHomeLogo() {
    Image(
        painter = painterResource(R.drawable.aeryo_app_icon),
        contentDescription = "Aeryo Logo",
        // The vector follows adaptive-icon safe-zone spacing; oversize it inside the
        // stable layout box just like InstallerX does in its Miuix About header.
        modifier = Modifier.requiredSize(196.dp)
    )
}

private fun fullScreenEnterTransition() =
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.4f)
    ) + fadeIn(animationSpec = tween(180))

private fun fullScreenExitTransition() =
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = folmeSpring(damping = 1f, response = 0.32f)
    ) + fadeOut(animationSpec = tween(140))

@Composable
private fun rememberBookmarkState(
    dao: net.zzbuaoye.aeryo.bookmarks.data.BookmarkDao,
    url: String
): Boolean {
    val bookmarked by dao.isBookmarked(url).collectAsState(initial = false)
    return bookmarked
}

private suspend fun net.zzbuaoye.aeryo.bookmarks.data.BookmarkDao.isBookmarkedNow(url: String): Boolean {
    return isBookmarked(url).first()
}

private fun processUrlInput(input: String, searchEngine: String): String {
    val value = input.trim()
    if (value.isEmpty()) return "about:blank"
    if (value.startsWith("aeryo://")) return value
    if (value.startsWith("aeryo:", ignoreCase = true)) return "aeryo://" + value.substringAfter("aeryo:").trimStart('/')
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (value.contains(".") && !value.contains(" ")) return "https://$value"
    return "$searchEngine${Uri.encode(value)}"
}

private fun collapsedAddressText(tab: WebTab?): String {
    val url = tab?.url.orEmpty().takeUnless { it.isBlank() || it == "about:blank" } ?: return ""
    tab?.title?.takeUnless {
        it.isBlank() || it == "新标签页" || it == "主页" || it == url
    }?.let { return it }
    extractSearchQueryFromUrl(url)?.takeIf(String::isNotBlank)?.let { return it }
    return url
}

private fun openExternalLink(context: android.content.Context, rawUrl: String): Boolean {
    val intent = runCatching {
        if (rawUrl.startsWith("intent:", ignoreCase = true)) {
            Intent.parseUri(rawUrl, Intent.URI_INTENT_SCHEME)
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse(rawUrl))
        }
    }.getOrNull() ?: return false

    val launchIntent = if (intent.resolveActivity(context.packageManager) != null) {
        intent
    } else {
        val fallbackUrl = intent.getStringExtra("browser_fallback_url") ?: return false
        Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
    }
    return try {
        if (context !is android.app.Activity) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(launchIntent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}

private fun restoreDefaultLauncherIcon(context: android.content.Context) {
    val defaultAlias = ".launcher.DefaultIconAlias"
    val legacyAliases = listOf(
        ".launcher.FlameIconAlias",
        ".launcher.FirefoxIconAlias",
        ".launcher.AuroraIconAlias",
        ".launcher.SunsetIconAlias",
        ".launcher.OceanIconAlias"
    )
    (listOf(defaultAlias) + legacyAliases).forEach { alias ->
        context.packageManager.setComponentEnabledSetting(
            android.content.ComponentName(context.packageName, context.packageName + alias),
            if (alias == defaultAlias) {
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            android.content.pm.PackageManager.DONT_KILL_APP
        )
    }
}

private fun isSearchQueryInput(input: String): Boolean {
    val value = input.trim()
    return value.isNotEmpty() &&
           !value.startsWith("http://") &&
           !value.startsWith("https://") &&
           !value.startsWith("aeryo://") &&
           !value.startsWith("aeryo:", ignoreCase = true) &&
           (!value.contains(".") || value.contains(" "))
}

private fun safeGoBack(webView: WebView): Boolean {
    if (!webView.canGoBack()) return false
    val history = webView.copyBackForwardList()
    val currentIndex = history.currentIndex
    if (currentIndex <= 0) return false

    val currentUrl = history.getItemAtIndex(currentIndex)?.url.orEmpty()
    val currentHost = runCatching { Uri.parse(currentUrl).host?.lowercase() }.getOrNull().orEmpty()
    val currentQuery = extractSearchQueryFromUrl(currentUrl)

    var targetIndex = currentIndex - 1
    while (targetIndex >= 0) {
        val targetItem = history.getItemAtIndex(targetIndex) ?: break
        val targetUrl = targetItem.url.orEmpty()
        val targetHost = runCatching { Uri.parse(targetUrl).host?.lowercase() }.getOrNull().orEmpty()
        val targetQuery = extractSearchQueryFromUrl(targetUrl)

        if (isUrlRedirectOrDuplicate(currentUrl, currentHost, currentQuery, targetUrl, targetHost, targetQuery)) {
            targetIndex--
        } else {
            break
        }
    }

    if (targetIndex < 0) return false

    val steps = currentIndex - targetIndex
    if (steps > 1) {
        webView.goBackOrForward(-steps)
    } else {
        webView.goBack()
    }
    return true
}

private fun isUrlRedirectOrDuplicate(
    currentUrl: String,
    currentHost: String,
    currentQuery: String?,
    targetUrl: String,
    targetHost: String,
    targetQuery: String?
): Boolean {
    if (targetUrl.isBlank()) return false
    val uri = runCatching { Uri.parse(targetUrl) }.getOrNull()
    if (uri != null && uri.isHierarchical) {
        val paramNames = runCatching { uri.queryParameterNames }.getOrNull().orEmpty()
        for (name in paramNames) {
            if (runCatching { uri.getQueryParameters(name) }.getOrNull()?.size.orZero() > 1) {
                return true
            }
        }
    }

    if (currentHost.isNotBlank() && targetHost.isNotBlank() &&
        isSameSearchEngineHost(currentHost, targetHost) &&
        !currentQuery.isNullOrBlank() && currentQuery.equals(targetQuery, ignoreCase = true)
    ) {
        return true
    }

    if (currentHost.removePrefix("www.") == targetHost.removePrefix("www.") &&
        currentUrl.substringAfter("://").substringBefore('?') == targetUrl.substringAfter("://").substringBefore('?')
    ) {
        if (currentUrl != targetUrl && (currentUrl.length > targetUrl.length + 10 || targetUrl.length > currentUrl.length + 10)) {
            return true
        }
    }

    return false
}

private fun isSameSearchEngineHost(host1: String, host2: String): Boolean {
    val h1 = host1.removePrefix("www.").lowercase()
    val h2 = host2.removePrefix("www.").lowercase()
    if (h1 == h2) return true

    val domain1 = h1.split('.').takeLast(2).joinToString(".")
    val domain2 = h2.split('.').takeLast(2).joinToString(".")
    if (domain1.isNotBlank() && domain1.contains('.') && domain1 == domain2) return true

    val engines = listOf("google.", "bing.", "baidu.", "duckduckgo.", "yahoo.", "yandex.", "so.com", "sogou.")
    return engines.any { h1.contains(it) && h2.contains(it) }
}

private fun Int?.orZero(): Int = this ?: 0

private fun buildSearchUrl(searchEngine: String, query: String): String {
    return searchEngine + Uri.encode(query)
}

private fun extractSearchQueryFromUrl(url: String): String? {
    if (url.isBlank()) return null
    val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return null
    if (!uri.isHierarchical) return null
    val host = uri.host?.lowercase().orEmpty()
    val param = when {
        host.contains("google.") -> "q"
        host.contains("bing.") -> "q"
        host.contains("baidu.") -> "wd"
        host.contains("duckduckgo.") -> "q"
        host.contains("yahoo.") -> "p"
        host.contains("yandex.") -> "text"
        host.contains("so.com") -> "q"
        host.contains("sogou.") -> "query"
        else -> null
    }

    if (param != null) {
        val q = uri.getQueryParameter(param)
        if (!q.isNullOrBlank()) return q
    }

    // 动态探测通用搜索 Query 参数名，无缝支持自定义搜索引擎
    val commonParams = listOf("q", "wd", "query", "p", "text", "k", "search", "key", "word", "kw", "s")
    for (p in commonParams) {
        val value = uri.getQueryParameter(p)
        if (!value.isNullOrBlank()) {
            return value
        }
    }

    return null
}

@Composable
private fun SearchSuggestionPopup(
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    blurEffectEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    AeryoGlassSurface(
        liquidGlassEnabled = false,
        blurEnabled = blurEffectEnabled,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .heightIn(max = 320.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(suggestions, key = { it }) { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionSelected(suggestion) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Search,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = suggestion,
                        color = MiuixTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private suspend fun fetchSearchSuggestions(searchEngine: String, query: String): List<String> {
    val value = query.trim()
    if (value.isEmpty()) return emptyList()
    return withContext(Dispatchers.IO) {
        val encoded = Uri.encode(value)
        val endpoint = when {
            searchEngine.contains("google", ignoreCase = true) ->
                "https://suggestqueries.google.com/complete/search?client=firefox&q=$encoded"
            searchEngine.contains("bing", ignoreCase = true) ->
                "https://www.bing.com/osjson.aspx?query=$encoded"
            searchEngine.contains("duckduckgo", ignoreCase = true) ->
                "https://duckduckgo.com/ac/?q=$encoded"
            searchEngine.contains("baidu", ignoreCase = true) ->
                "https://suggestion.baidu.com/su?wd=$encoded"
            searchEngine.contains("so.com", ignoreCase = true) ->
                "https://sug.so.360.cn/suggest?word=$encoded"
            searchEngine.contains("sogou", ignoreCase = true) ->
                "https://sugg.sogou.com/sugg/associated/Query?key=$encoded"
            else -> "https://suggestqueries.google.com/complete/search?client=firefox&q=$encoded"
        }
        runCatching {
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                connectTimeout = 2_500
                readTimeout = 2_500
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json,text/javascript,*/*")
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android) AeryoBrowser")
            }
            try {
                if (connection.responseCode !in 200..299) return@runCatching emptyList()
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                parseSuggestionResponse(body)
            } finally {
                connection.disconnect()
            }
        }.getOrDefault(emptyList()).distinct().filter { it.isNotBlank() }.take(12)
    }
}

private fun parseSuggestionResponse(body: String): List<String> {
    val start = body.indexOf('[')
    val end = body.lastIndexOf(']')
    val json = if (start >= 0 && end > start) body.substring(start, end + 1) else body
    runCatching {
        val root = JSONArray(json)
        val nested = root.optJSONArray(1)
        if (nested != null) {
            return buildList {
                for (index in 0 until nested.length()) {
                    nested.optString(index).takeIf(String::isNotBlank)?.let(::add)
                }
            }
        }
        val objectSuggestions = buildList {
            for (index in 0 until root.length()) {
                root.optJSONObject(index)?.optString("phrase")?.takeIf(String::isNotBlank)?.let(::add)
            }
        }
        if (objectSuggestions.isNotEmpty()) return objectSuggestions
    }
    return Regex("[\\\"']([^\\\"']{2,})[\\\"']")
        .findAll(body)
        .map { it.groupValues[1] }
        .filterNot { it == "q" || it == "p" }
        .toList()
}

@Composable
private fun SearchEngineSwitchButton(
    currentEngine: String,
    isInputting: Boolean,
    onEngineChanged: (String) -> Unit
) {
    var userExpanded by remember { mutableStateOf(false) }
    val showPopup = userExpanded
    val popupAnimState = remember { MutableTransitionState(false) }
    popupAnimState.targetState = showPopup
    val scope = rememberCoroutineScope()

    val engines = listOf(
        UserPreferences.ENGINE_BING to "Bing",
        UserPreferences.ENGINE_GOOGLE to "Google",
        UserPreferences.ENGINE_BAIDU to "百度",
        UserPreferences.ENGINE_DUCKDUCKGO to "DuckDuckGo",
        UserPreferences.ENGINE_YAHOO to "Yahoo",
        UserPreferences.ENGINE_YANDEX to "Yandex",
        UserPreferences.ENGINE_360 to "360",
        UserPreferences.ENGINE_SOGOU to "Sogou"
    )

    Box {
        IconButton(
            onClick = { userExpanded = true },
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Icon(
                imageVector = MiuixIcons.Sidebar,
                contentDescription = "Search Engine",
                tint = MiuixTheme.colorScheme.onSurfaceContainerHigh,
                modifier = Modifier.size(24.dp)
            )
        }

        if (showPopup || popupAnimState.currentState) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { userExpanded = false }
            ) {
                AnimatedVisibility(
                    visibleState = popupAnimState,
                    enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.8f, animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.8f, animationSpec = tween(150))
                ) {
                    Card(
                        modifier = Modifier.padding(top = 40.dp, end = 16.dp).width(160.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            engines.forEach { (engineUrl, engineName) ->
                                val isSelected = currentEngine == engineUrl
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onEngineChanged(engineUrl)
                                            scope.launch {
                                                delay(150)
                                                userExpanded = false
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = engineName,
                                        color = if (isSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "✓",
                                            color = MiuixTheme.colorScheme.primary,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
