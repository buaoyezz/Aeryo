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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
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
import coil.compose.rememberAsyncImagePainter
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
import top.yukonga.miuix.kmp.basic.InputField
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
    val supportsAdvancedVisualEffects = remember(context, rootView) {
        val activityManager = context.getSystemService(android.app.ActivityManager::class.java)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            rootView.isHardwareAccelerated &&
            activityManager?.isLowRamDevice != true
    }
    val chromeFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    val tabManager = remember { TabManager() }
    val downloadManager = remember { AeryoDownloadManager(context) }
    val dao = remember { BookmarkDatabase.getDatabase(context).bookmarkDao() }
    val preferences = remember { UserPreferences(context) }
    val recordedHistoryUrls = remember { mutableMapOf<String, String>() }
    val packageInfo = remember(context) {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val appVersionName = packageInfo.versionName.orEmpty().ifBlank { "1.0.0" }
    @Suppress("DEPRECATION")
    val appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        packageInfo.versionCode.toLong()
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
    val menuOrder by preferences.menuOrder.collectAsState(initial = emptyList())
    val addressBarAnimationEnabled by preferences.addressBarAnimationEnabled.collectAsState(initial = true)
    val nightModeEnabled by preferences.nightModeEnabled.collectAsState(initial = false)
    val downloadMode by preferences.downloadMode.collectAsState(initial = UserPreferences.DOWNLOAD_MODE_SYSTEM)
    val themeMode by preferences.themeMode.collectAsState(initial = UserPreferences.THEME_MODE_SYSTEM)
    val themePalette by preferences.themePalette.collectAsState(initial = UserPreferences.THEME_PALETTE_TONAL_SPOT)
    val themeKeyColor by preferences.themeKeyColor.collectAsState(initial = UserPreferences.DEFAULT_THEME_KEY_COLOR)
    val glassEffectEnabled by preferences.glassEffectEnabled.collectAsState(initial = true)
    val blurEffectEnabled by preferences.blurEffectEnabled.collectAsState(initial = false)
    val logoVariant by preferences.logoVariant.collectAsState(initial = UserPreferences.LOGO_VARIANT_DEFAULT)
    val logoStrokeWidth by preferences.logoStrokeWidth.collectAsState(initial = UserPreferences.DEFAULT_LOGO_STROKE_WIDTH)
    val logoCustomColor by preferences.logoCustomColor.collectAsState(initial = UserPreferences.DEFAULT_LOGO_CUSTOM_COLOR)
    val logoCustomColorEnabled by preferences.logoCustomColorEnabled.collectAsState(initial = false)
    val logoCustomImageUri by preferences.logoCustomImageUri.collectAsState(initial = "")
    val logoCustomText by preferences.logoCustomText.collectAsState(initial = "A")
    val logoOffsetX by preferences.logoOffsetX.collectAsState(initial = 0f)
    val logoOffsetY by preferences.logoOffsetY.collectAsState(initial = 0f)
    val launcherIconVariant by preferences.launcherIconVariant.collectAsState(initial = "")
    val effectiveGlassEffectEnabled = glassEffectEnabled && supportsAdvancedVisualEffects
    val effectiveBlurEffectEnabled = blurEffectEnabled && supportsAdvancedVisualEffects
    val privacyBiometricEnabled by preferences.privacyBiometricEnabled.collectAsState(initial = false)
    val doNotTrackEnabled by preferences.doNotTrackEnabled.collectAsState(initial = true)
    val blockThirdPartyCookies by preferences.blockThirdPartyCookies.collectAsState(initial = false)
    val clearOnExit by preferences.clearOnExit.collectAsState(initial = false)
    val privateHistory by dao.getAllPrivateHistory().collectAsState(initial = emptyList())
    val activity = context as? FragmentActivity
    val customLogoPainter = rememberAsyncImagePainter(model = logoCustomImageUri.takeIf(String::isNotBlank))

    LaunchedEffect(launcherIconVariant) {
        if (launcherIconVariant.isNotBlank()) {
            applyLauncherIcon(context, launcherIconVariant)
        }
    }

    val customLogoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            scope.launch {
                preferences.setLogoCustomImageUri(uri.toString())
                preferences.setLogoVariant(UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE)
            }
        }
    }

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
    var addressInputEnabled by remember { mutableStateOf(true) }
    var isAddressSubmitting by remember { mutableStateOf(false) }
    var addressInputGeneration by remember { mutableStateOf(0) }
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
                        isLoading = true
                    )
                }
            }
        }
    }

    LaunchedEffect(currentTab?.url, currentTab?.title, searchEngine, addressExpanded) {
        if (!addressExpanded) {
            addressText = collapsedAddressText(currentTab)
        }
    }

    LaunchedEffect(addressExpanded) {
        if (!addressExpanded) {
            delay(100)
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            rootView.clearFocus()
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

    BackHandler(
        enabled = showMenu || showTabs || showBookmarks || showHistory || showDownloads ||
            showSettings || showAdBlockSettings || showAbout || showFind || currentTab?.canGoBack == true
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
            currentTab?.canGoBack == true -> currentTab.webView?.goBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(1.dp)
                .focusRequester(chromeFocusRequester)
                .focusable()
        )
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
                        enabled = addressInputEnabled,
                        inputGeneration = addressInputGeneration,
                        text = addressText,
                        expanded = addressExpanded,
                        isBookmarked = rememberBookmarkState(dao, currentTab?.url.orEmpty()),
                        searchEngine = searchEngine,
                        glassEffectEnabled = effectiveGlassEffectEnabled,
                        blurEffectEnabled = effectiveBlurEffectEnabled,
                        onSearchEngineChange = { scope.launch { preferences.setSearchEngine(it) } },
                        onTextChange = { addressText = it },
                        onExpandedChange = { shouldExpand ->
                            if (!isAddressSubmitting || !shouldExpand) {
                                if (shouldExpand) {
                                    addressText = currentTab?.url
                                        ?.takeUnless { it.isBlank() || it == "about:blank" }
                                        .orEmpty()
                                    addressInputGeneration += 1
                                }
                                addressExpanded = shouldExpand
                            }
                        },
                        onSubmit = {
                            isAddressSubmitting = true
                            addressInputEnabled = false
                            addressText = it.trim()
                            addressExpanded = false
                            keyboardController?.hide()
                            focusManager.clearFocus(force = true)
                            navigateCurrentTab(it)
                            scope.launch {
                                delay(150)
                                addressExpanded = false
                                chromeFocusRequester.requestFocus()
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                rootView.clearFocus()
                                context.getSystemService(InputMethodManager::class.java)
                                    ?.hideSoftInputFromWindow(rootView.windowToken, 0)
                                addressInputGeneration += 1
                                addressInputEnabled = true
                                delay(100)
                                addressExpanded = false
                                isAddressSubmitting = false
                            }
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
                        currentTab?.let { tab ->
                            tab.webView?.takeIf(WebView::canGoBack)?.goBack()
                        }
                    },
                    onForward = {
                        currentTab?.let { tab ->
                            tab.webView?.takeIf(WebView::canGoForward)?.goForward()
                        }
                    },
                    onHome = {
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
                                preview = null
                            )
                        }
                    },
                    onTabs = {
                        addressExpanded = false
                        currentTab?.webView?.let { webView ->
                            webView.post {
                                captureWebViewPreview(webView)?.let { preview ->
                                    tabManager.updateTab(currentTab.id) {
                                        it.copy(preview = preview)
                                    }
                                }
                            }
                        }
                        showTabs = true
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
            containerColor = if (currentTab?.isIncognito == true) androidx.compose.ui.graphics.Color(0xFF0F0E17) else MiuixTheme.colorScheme.background
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
                            logoVariant = logoVariant,
                            logoStrokeWidth = logoStrokeWidth,
                            logoCustomColor = logoCustomColor,
                            logoCustomColorEnabled = logoCustomColorEnabled,
                            logoCustomImageUri = logoCustomImageUri,
                            logoCustomText = logoCustomText,
                            logoOffsetX = logoOffsetX,
                            logoOffsetY = logoOffsetY,
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
                                    isLoading = true
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
            }
        }

        AnimatedVisibility(
            visible = showTabs,
            enter = fullScreenEnterTransition(),
            exit = fullScreenExitTransition(),
            modifier = Modifier.fillMaxSize()
        ) {
            TabSwitcherScreen(
                tabs = tabs,
                activeTabIndex = activeTabIndex,
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
                    showTabs = false
                },
                onCloseIncognitoTabs = {
                    tabManager.closeIncognitoTabs()
                    showTabs = false
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
                currentGlassEffectEnabled = glassEffectEnabled,
                currentBlurEffectEnabled = blurEffectEnabled,
                currentLogoVariant = logoVariant,
                currentLogoStrokeWidth = logoStrokeWidth,
                currentLogoCustomColor = logoCustomColor,
                currentLogoCustomColorEnabled = logoCustomColorEnabled,
                currentLogoCustomText = logoCustomText,
                currentLogoOffsetX = logoOffsetX,
                currentLogoOffsetY = logoOffsetY,
                currentLauncherIconVariant = launcherIconVariant,
                supportsLiquidGlass = supportsAdvancedVisualEffects,
                supportsBackgroundBlur = supportsAdvancedVisualEffects,
                customLogoPainter = customLogoPainter.takeIf { logoCustomImageUri.isNotBlank() },
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
                onGlassEffectToggled = { enabled ->
                    scope.launch { preferences.setGlassEffectEnabled(enabled) }
                },
                onBlurEffectToggled = { enabled ->
                    scope.launch { preferences.setBlurEffectEnabled(enabled) }
                },
                onLogoVariantChanged = { variant ->
                    scope.launch { preferences.setLogoVariant(variant) }
                },
                onLogoStrokeWidthChanged = { width ->
                    scope.launch { preferences.setLogoStrokeWidth(width) }
                },
                onLogoCustomColorChanged = { color ->
                    scope.launch { preferences.setLogoCustomColor(color) }
                },
                onLogoCustomColorEnabledChanged = { enabled ->
                    scope.launch { preferences.setLogoCustomColorEnabled(enabled) }
                },
                onPickCustomLogo = { customLogoPicker.launch(arrayOf("image/*")) },
                onLogoCustomTextChanged = { text ->
                    scope.launch { preferences.setLogoCustomText(text) }
                },
                onLogoOffsetXChanged = { offset ->
                    scope.launch { preferences.setLogoOffsetX(offset) }
                },
                onLogoOffsetYChanged = { offset ->
                    scope.launch { preferences.setLogoOffsetY(offset) }
                },
                onLauncherIconVariantChanged = { variant ->
                    applyLauncherIcon(context, variant)
                    scope.launch { preferences.setLauncherIconVariant(variant) }
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
                blockedRequestCount = AdBlockEngine.getBlockedRequestCount(),
                onAdBlockToggled = { scope.launch { preferences.setAdBlockEnabled(it) } },
                onSourcesUpdated = { scope.launch { preferences.setAdBlockSources(it) } },
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
                androidVersion = "Android ${Build.VERSION.RELEASE} · API ${Build.VERSION.SDK_INT}",
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

@Composable
private fun BrowserAddressBar(
    tab: WebTab?,
    isHome: Boolean,
    enabled: Boolean,
    inputGeneration: Int,
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
    // Search result pages do not need a trailing favorite action; hiding it
    // leaves the address bar stable after a query has loaded.
    val isSearchResultPage = extractSearchQueryFromUrl(tab?.url.orEmpty()) != null
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(expanded, text, searchEngine) {
        if (!expanded || !isSearchQueryInput(text)) {
            suggestions = emptyList()
        } else {
            delay(220)
            suggestions = fetchSearchSuggestions(searchEngine, text)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    isIncognito -> androidx.compose.ui.graphics.Color(0xFF181524)
                    isHome -> MiuixTheme.colorScheme.background
                    glassEffectEnabled -> MiuixTheme.colorScheme.surface.copy(alpha = 0.86f)
                    else -> MiuixTheme.colorScheme.surface
                }
            )
            .statusBarsPadding()
    ) {
        key(inputGeneration) {
            InputField(
                query = text,
                onQueryChange = onTextChange,
                onSearch = onSubmit,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                enabled = enabled,
                label = if (tab?.url == "about:blank") {
                    if (isIncognito) "无痕模式 - 搜索或输入网址" else "搜索或输入网址"
                } else tab?.title.orEmpty(),
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
                                if (!isSearchResultPage) {
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
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        if (expanded && suggestions.isNotEmpty()) {
            SearchSuggestionPopup(
                suggestions = suggestions,
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
    NavigationBar(
        modifier = Modifier.height(48.dp),
        color = if (isIncognito) androidx.compose.ui.graphics.Color(0xFF14121E) else MiuixTheme.colorScheme.surface,
        showDivider = false,
        mode = NavigationBarDisplayMode.IconOnly
    ) {
        NavigationBarItem(
            selected = false,
            enabled = tab?.canGoBack == true,
            onClick = onBack,
            icon = MiuixIcons.ChevronBackward,
            label = "后退"
        )
        NavigationBarItem(
            selected = false,
            enabled = tab?.canGoForward == true,
            onClick = onForward,
            icon = MiuixIcons.ChevronForward,
            label = "前进"
        )
        NavigationBarItem(
            selected = false,
            onClick = onHome,
            icon = MiuixIcons.Home,
            label = "主页"
        )
        NavigationBarItem(
            selected = false,
            onClick = onTabs,
            icon = MiuixIcons.GridView,
            label = "标签页",
            badge = {
                AnimatedVisibility(
                    visible = tabCount > 1,
                    enter = fadeIn(animationSpec = tween(180)) +
                        scaleIn(
                            initialScale = 0.6f,
                            animationSpec = folmeSpring(damping = 0.76f, response = 0.34f)
                        ),
                    exit = fadeOut(animationSpec = tween(120)) +
                        scaleOut(targetScale = 0.6f, animationSpec = tween(140))
                ) {
                    Badge(
                        containerColor = if (isIncognito) androidx.compose.ui.graphics.Color(0xFF9C27B0) else MiuixTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (isIncognito) androidx.compose.ui.graphics.Color.White else MiuixTheme.colorScheme.onSurface
                    ) {
                        AnimatedContent(
                            targetState = tabCount.coerceAtMost(99),
                            transitionSpec = {
                                (
                                    slideInVertically { it } + fadeIn(animationSpec = tween(150))
                                    ).togetherWith(
                                    slideOutVertically { -it } + fadeOut(animationSpec = tween(100))
                                )
                            },
                            label = "tab-count"
                        ) { count ->
                            Text(text = count.toString())
                        }
                    }
                }
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = onMenu,
            icon = MiuixIcons.ListView,
            label = "菜单"
        )
    }
}

@Composable
private fun AeryoHomeScreen(
    animationEnabled: Boolean,
    logoVariant: String = UserPreferences.LOGO_VARIANT_DEFAULT,
    logoStrokeWidth: Float = UserPreferences.DEFAULT_LOGO_STROKE_WIDTH,
    logoCustomColor: Long = UserPreferences.DEFAULT_LOGO_CUSTOM_COLOR,
    logoCustomColorEnabled: Boolean = false,
    logoCustomImageUri: String = "",
    logoCustomText: String = "A",
    logoOffsetX: Float = 0f,
    logoOffsetY: Float = 0f,
    blurEffectEnabled: Boolean = false,
    searchEngine: String = UserPreferences.ENGINE_BING,
    onSearchEngineChange: (String) -> Unit = {},
    onSearchSubmit: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
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
        if (blurEffectEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 8.dp)
                    .size(176.dp)
                    .blur(58.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .background(
                        color = MiuixTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            )
        }
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
                .offset(x = logoOffsetX.dp, y = centeredSearchY - 128.dp + logoOffsetY.dp)
                .size(108.dp)
                .graphicsLayer {
                    alpha = logoAlpha
                    scaleX = logoScale
                    scaleY = logoScale
                },
            contentAlignment = Alignment.Center
        ) {
            AeryoHomeLogo(
                variant = logoVariant,
                strokeWidth = logoStrokeWidth,
                customColor = logoCustomColor,
                customColorEnabled = logoCustomColorEnabled,
                customImageUri = logoCustomImageUri,
                customText = logoCustomText
            )
        }
        InputField(
            query = query,
            onQueryChange = { query = it },
            onSearch = { value ->
                if (value.isNotBlank()) {
                    expanded = false
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                    onSearchSubmit(value)
                }
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            label = "搜索或输入网址",
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
        if (expanded && suggestions.isNotEmpty()) {
            SearchSuggestionPopup(
                suggestions = suggestions,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = searchOffsetY + 66.dp)
                    .fillMaxWidth(),
                onSuggestionSelected = { suggestion ->
                    query = suggestion
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
private fun AeryoHomeLogo(
    variant: String,
    strokeWidth: Float,
    customColor: Long,
    customColorEnabled: Boolean,
    customImageUri: String,
    customText: String
) {
    when (variant) {
        UserPreferences.LOGO_VARIANT_DEFAULT -> AeryoHomeLineMark(
            variant = variant,
            strokeWidth = strokeWidth,
            customColor = customColor,
            customColorEnabled = customColorEnabled
        )

        UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE -> {
            if (customImageUri.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(customImageUri),
                    contentDescription = "自定义 Logo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.aeryo_app_icon),
                    contentDescription = "Aeryo 默认 Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        UserPreferences.LOGO_VARIANT_CUSTOM_TEXT -> Text(
            text = customText.ifBlank { "A" },
            color = if (customColorEnabled) Color(customColor) else MiuixTheme.colorScheme.primary,
            fontSize = 52.sp,
            fontWeight = when {
                strokeWidth >= 15f -> FontWeight.Black
                strokeWidth >= 10f -> FontWeight.Bold
                else -> FontWeight.Medium
            }
        )

        else -> AeryoHomeLineMark(variant, strokeWidth, customColor, customColorEnabled)
    }
}

@Composable
private fun AeryoHomeLineMark(
    variant: String,
    strokeWidth: Float,
    customColor: Long,
    customColorEnabled: Boolean
) {
    val monoColor = MiuixTheme.colorScheme.onBackground
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sx = size.width / 120f
        val sy = size.height / 120f
        val path = Path().apply {
            moveTo(24f * sx, 96f * sy)
            cubicTo(30f * sx, 69f * sy, 37f * sx, 45f * sy, 50f * sx, 27f * sy)
            cubicTo(56f * sx, 19f * sy, 65f * sx, 19f * sy, 71f * sx, 28f * sy)
            cubicTo(79f * sx, 42f * sy, 84f * sx, 62f * sy, 89f * sx, 85f * sy)
            moveTo(36f * sx, 72f * sy)
            cubicTo(52f * sx, 62f * sy, 70f * sx, 58f * sy, 91f * sx, 60f * sy)
            cubicTo(104f * sx, 61f * sy, 111f * sx, 68f * sy, 108f * sx, 79f * sy)
            cubicTo(105f * sx, 91f * sy, 92f * sx, 98f * sy, 78f * sx, 96f * sy)
        }
        val brush = if (customColorEnabled) {
            SolidColor(Color(customColor))
        } else {
            when (variant) {
                UserPreferences.LOGO_VARIANT_SUNSET -> Brush.linearGradient(
                    listOf(Color(0xFFFFC247), Color(0xFFFF7043), Color(0xFFE04F88)),
                    Offset.Zero,
                    Offset(size.width, size.height)
                )
                UserPreferences.LOGO_VARIANT_MINT -> Brush.linearGradient(
                    listOf(Color(0xFF50D8AE), Color(0xFF27B7A5), Color(0xFF3A88D8)),
                    Offset.Zero,
                    Offset(size.width, size.height)
                )
                UserPreferences.LOGO_VARIANT_MONO -> SolidColor(monoColor)
                UserPreferences.LOGO_VARIANT_DEFAULT -> Brush.linearGradient(
                    listOf(Color(0xFFFFB13B), Color(0xFFF16A55), Color(0xFF8A4FD2)),
                    Offset.Zero,
                    Offset(size.width, size.height)
                )
                else -> Brush.linearGradient(
                    listOf(Color(0xFF4DA3FF), Color(0xFF806BFF), Color(0xFFC35BDF)),
                    Offset.Zero,
                    Offset(size.width, size.height)
                )
            }
        }
        drawPath(
            path = path,
            brush = brush,
            style = Stroke(
                width = strokeWidth.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
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
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (value.contains(".") && !value.contains(" ")) return "https://$value"
    return "$searchEngine${Uri.encode(value)}"
}

private fun collapsedAddressText(tab: WebTab?): String {
    val url = tab?.url.orEmpty().takeUnless { it.isBlank() || it == "about:blank" } ?: return ""
    extractSearchQueryFromUrl(url)?.takeIf(String::isNotBlank)?.let { return it }
    tab?.title?.takeUnless {
        it.isBlank() || it == "新标签页" || it == "主页" || it == url
    }?.let { return it }
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

private fun applyLauncherIcon(context: android.content.Context, variant: String) {
    val aliases = linkedMapOf(
        UserPreferences.LAUNCHER_ICON_DEFAULT to ".launcher.DefaultIconAlias",
        UserPreferences.LAUNCHER_ICON_FLAME to ".launcher.FlameIconAlias",
        UserPreferences.LAUNCHER_ICON_AURORA to ".launcher.AuroraIconAlias",
        UserPreferences.LAUNCHER_ICON_SUNSET to ".launcher.SunsetIconAlias",
        UserPreferences.LAUNCHER_ICON_OCEAN to ".launcher.OceanIconAlias"
    )
    val selected = aliases[variant] ?: aliases.getValue(UserPreferences.LAUNCHER_ICON_DEFAULT)
    (aliases.values + ".launcher.FirefoxIconAlias").forEach { alias ->
        context.packageManager.setComponentEnabledSetting(
            android.content.ComponentName(context.packageName, context.packageName + alias),
            if (alias == selected) {
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
           (!value.contains(".") || value.contains(" "))
}

private fun buildSearchUrl(searchEngine: String, query: String): String {
    return searchEngine + Uri.encode(query)
}

private fun extractSearchQueryFromUrl(url: String): String? {
    val param = when {
        url.startsWith("https://www.google.com/search?q=") -> "q"
        url.startsWith("https://www.bing.com/search?q=") -> "q"
        url.startsWith("https://www.baidu.com/s?wd=") -> "wd"
        url.startsWith("https://duckduckgo.com/?q=") -> "q"
        url.startsWith("https://search.yahoo.com/search?p=") -> "p"
        url.startsWith("https://yandex.com/search/?text=") -> "text"
        url.startsWith("https://www.so.com/s?q=") -> "q"
        url.startsWith("https://www.sogou.com/web?query=") -> "query"
        else -> return null
    }
    return Uri.parse(url).getQueryParameter(param)
}

@Composable
private fun SearchSuggestionPopup(
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 4.dp),
        insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        LazyColumn {
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
        }.getOrDefault(emptyList()).distinct().filter { it.isNotBlank() }.take(6)
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
