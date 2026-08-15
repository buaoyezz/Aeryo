package net.zzbuaoye.aeryo.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.utils.pressable
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.settings.data.SearchEngine
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import compose.icons.TablerIcons
import compose.icons.tablericons.Check
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import net.zzbuaoye.aeryo.settings.data.ClearDataOptions

private enum class SettingsPage(val title: String) {
    ROOT("设置"),
    APPEARANCE("外观与个性化"),
    BROWSER("浏览设置"),
    SEARCH_ENGINES("搜索引擎"),
    DOWNLOADS("下载设置"),
    PRIVACY("隐私与数据"),
    CLEAR_DATA("清除浏览数据")
}

@Composable
fun SettingsScreen(
    currentSearchEngine: SearchEngine = SearchEngine.PRESET_BING,
    allSearchEngines: List<SearchEngine> = SearchEngine.PRESET_ENGINES,
    onSearchEngineSelected: (SearchEngine) -> Unit = {},
    onAddCustomEngine: (name: String, searchUrl: String, suggestionUrl: String) -> Unit = { _, _, _ -> },
    onUpdateCustomEngine: (SearchEngine) -> Unit = {},
    onDeleteCustomEngine: (String) -> Unit = {},
    currentAddressBarAnimationEnabled: Boolean = true,
    currentDownloadMode: String = UserPreferences.DOWNLOAD_MODE_BUILT_IN,
    currentDownloadPath: String = "",
    currentThemeMode: String = UserPreferences.THEME_MODE_SYSTEM,
    currentThemePalette: String = UserPreferences.THEME_PALETTE_TONAL_SPOT,
    currentThemeKeyColor: Long = UserPreferences.DEFAULT_THEME_KEY_COLOR,
    currentPrivacyBiometricEnabled: Boolean = false,
    currentDoNotTrackEnabled: Boolean = true,
    currentBlockThirdPartyCookies: Boolean = false,
    currentClearOnExit: Boolean = false,
    currentVideoAutoLandscapeEnabled: Boolean = true,
    currentVideoKeepScreenOnEnabled: Boolean = true,
    currentVideoPipAutoEnabled: Boolean = true,
    currentVideoGestureEnabled: Boolean = true,
    currentBackgroundPlaybackEnabled: Boolean = true,
    currentMediaSnifferEnabled: Boolean = true,
    currentMediaSnifferFloatingEnabled: Boolean = false,
    currentMediaSnifferBadgeMode: String = UserPreferences.MEDIA_SNIFFER_BADGE_DOT,
    currentEdgeSwipeNavigationEnabled: Boolean = true,
    currentSmartDarkModeEnabled: Boolean = true,
    currentQuickScrollButtonEnabled: Boolean = true,
    onSearchEngineChanged: (String) -> Unit = {},
    onAdBlockSettingsClicked: () -> Unit = {},
    onAddressBarAnimationToggled: (Boolean) -> Unit = {},
    currentTabViewMode: String = UserPreferences.TAB_VIEW_MODE_GRID,
    onTabViewModeChanged: (String) -> Unit = {},
    onDownloadModeChanged: (String) -> Unit = {},
    onPickDownloadPath: () -> Unit = {},
    onThemeModeChanged: (String) -> Unit = {},
    onThemePaletteChanged: (String) -> Unit = {},
    onThemeKeyColorChanged: (Long) -> Unit = {},
    onPrivacyBiometricToggled: (Boolean) -> Unit = {},
    onDoNotTrackToggled: (Boolean) -> Unit = {},
    onBlockThirdPartyCookiesToggled: (Boolean) -> Unit = {},
    onClearOnExitToggled: (Boolean) -> Unit = {},
    onVideoAutoLandscapeToggled: (Boolean) -> Unit = {},
    onVideoKeepScreenOnToggled: (Boolean) -> Unit = {},
    onVideoPipAutoToggled: (Boolean) -> Unit = {},
    onVideoGestureToggled: (Boolean) -> Unit = {},
    onBackgroundPlaybackToggled: (Boolean) -> Unit = {},
    onMediaSnifferToggled: (Boolean) -> Unit = {},
    onMediaSnifferFloatingToggled: (Boolean) -> Unit = {},
    onMediaSnifferBadgeModeChanged: (String) -> Unit = {},
    onEdgeSwipeNavigationToggled: (Boolean) -> Unit = {},
    onSmartDarkModeToggled: (Boolean) -> Unit = {},
    onQuickScrollButtonToggled: (Boolean) -> Unit = {},
    onClearData: () -> Unit = {},
    onClearCustomData: (ClearDataOptions) -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var page by remember { mutableStateOf(SettingsPage.ROOT) }
    var clearDataOptions by remember { mutableStateOf(ClearDataOptions()) }
    var showAddEngineDialog by remember { mutableStateOf(false) }
    var editingEngine by remember { mutableStateOf<SearchEngine?>(null) }
    var engineToDelete by remember { mutableStateOf<SearchEngine?>(null) }

    val topAppBarScrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberMiuixWindowBackdrop()
    val context = androidx.compose.ui.platform.LocalContext.current

    fun navigateBack() {
        page = when (page) {
            SettingsPage.ROOT -> {
                onBack()
                SettingsPage.ROOT
            }
            SettingsPage.SEARCH_ENGINES -> SettingsPage.BROWSER
            SettingsPage.CLEAR_DATA -> SettingsPage.PRIVACY
            else -> SettingsPage.ROOT
        }
    }

    BackHandler(enabled = page != SettingsPage.ROOT) { navigateBack() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiuixBlurredBar(topBarBackdrop) {
                TopAppBar(
                    title = page.title,
                    color = if (topBarBackdrop != null) Color.Transparent else miuixWindowBackgroundColor(),
                    navigationIcon = {
                        IconButton(onClick = ::navigateBack) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                    actions = {
                        if (page == SettingsPage.SEARCH_ENGINES) {
                            IconButton(onClick = {
                                editingEngine = null
                                showAddEngineDialog = true
                            }) {
                                Icon(MiuixIcons.Add, contentDescription = "添加搜索引擎")
                            }
                        }
                    },
                    scrollBehavior = topAppBarScrollBehavior
                )
            }
        },
        containerColor = miuixWindowBackgroundColor()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedContent(
                targetState = page,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally(
                            animationSpec = folmeSpring(damping = 0.86f, response = 0.36f),
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) + fadeIn(animationSpec = folmeSpring(damping = 0.86f, response = 0.36f))).togetherWith(
                            slideOutHorizontally(
                                animationSpec = folmeSpring(damping = 0.95f, response = 0.32f),
                                targetOffsetX = { fullWidth -> -fullWidth / 3 }
                            ) + fadeOut(animationSpec = folmeSpring(damping = 0.95f, response = 0.32f))
                        )
                    } else {
                        (slideInHorizontally(
                            animationSpec = folmeSpring(damping = 0.86f, response = 0.36f),
                            initialOffsetX = { fullWidth -> -fullWidth / 3 }
                        ) + fadeIn(animationSpec = folmeSpring(damping = 0.86f, response = 0.36f))).togetherWith(
                            slideOutHorizontally(
                                animationSpec = folmeSpring(damping = 0.95f, response = 0.32f),
                                targetOffsetX = { fullWidth -> fullWidth }
                            ) + fadeOut(animationSpec = folmeSpring(damping = 0.95f, response = 0.32f))
                        )
                    }
                },
                label = "SettingsPageTransition"
            ) { targetPage ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 680.dp)
                        .fillMaxWidth()
                        .scrollEndHaptic()
                        .overScrollVertical()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    overscrollEffect = null,
                ) {
                    item(key = "page-top-space") { Spacer(modifier = Modifier.height(12.dp)) }
                    when (targetPage) {
                        SettingsPage.ROOT -> settingsRoot(
                            onAppearance = { page = SettingsPage.APPEARANCE },
                            onBrowser = { page = SettingsPage.BROWSER },
                            onDownloads = { page = SettingsPage.DOWNLOADS },
                            onPrivacy = { page = SettingsPage.PRIVACY },
                            onAdBlock = onAdBlockSettingsClicked,
                            onAbout = onOpenAbout
                        )

                        SettingsPage.APPEARANCE -> appearanceSettings(
                            currentThemeMode,
                            currentThemePalette,
                            currentThemeKeyColor,
                            onThemeModeChanged,
                            onThemePaletteChanged,
                            onThemeKeyColorChanged
                        )

                        SettingsPage.BROWSER -> browserSettings(
                            currentSearchEngine,
                            currentAddressBarAnimationEnabled,
                            currentTabViewMode,
                            currentVideoAutoLandscapeEnabled,
                            currentVideoKeepScreenOnEnabled,
                            currentVideoPipAutoEnabled,
                            currentVideoGestureEnabled,
                            currentBackgroundPlaybackEnabled,
                            currentMediaSnifferEnabled,
                            currentMediaSnifferFloatingEnabled,
                            currentMediaSnifferBadgeMode,
                            currentEdgeSwipeNavigationEnabled,
                            currentSmartDarkModeEnabled,
                            currentQuickScrollButtonEnabled,
                            onNavigateToSearchEngines = { page = SettingsPage.SEARCH_ENGINES },
                            onSearchEngineChanged,
                            onAddressBarAnimationToggled,
                            onTabViewModeChanged,
                            onVideoAutoLandscapeToggled,
                            onVideoKeepScreenOnToggled,
                            onVideoPipAutoToggled,
                            onVideoGestureToggled,
                            onBackgroundPlaybackToggled,
                            onMediaSnifferToggled,
                            onMediaSnifferFloatingToggled,
                            onMediaSnifferBadgeModeChanged,
                            onEdgeSwipeNavigationToggled,
                            onSmartDarkModeToggled,
                            onQuickScrollButtonToggled
                        )

                        SettingsPage.SEARCH_ENGINES -> searchEnginesSettings(
                            currentSearchEngine = currentSearchEngine,
                            allSearchEngines = allSearchEngines,
                            onSearchEngineSelected = {
                                onSearchEngineSelected(it)
                                onSearchEngineChanged(it.searchUrl)
                            },
                            onEditEngine = {
                                editingEngine = it
                                showAddEngineDialog = true
                            },
                            onDeleteEngine = {
                                engineToDelete = it
                            },
                            onAddEngine = {
                                editingEngine = null
                                showAddEngineDialog = true
                            }
                        )

                        SettingsPage.DOWNLOADS -> downloadSettings(
                            currentDownloadMode,
                            currentDownloadPath,
                            onDownloadModeChanged,
                            onPickDownloadPath
                        )

                        SettingsPage.PRIVACY -> privacySettings(
                            currentPrivacyBiometricEnabled,
                            currentDoNotTrackEnabled,
                            currentBlockThirdPartyCookies,
                            currentClearOnExit,
                            onPrivacyBiometricToggled,
                            onDoNotTrackToggled,
                            onBlockThirdPartyCookiesToggled,
                            onClearOnExitToggled,
                            onNavigateToClearData = { page = SettingsPage.CLEAR_DATA }
                        )

                        SettingsPage.CLEAR_DATA -> clearDataSettings(
                            options = clearDataOptions,
                            onOptionsChange = { clearDataOptions = it },
                            onExecuteClear = {
                                onClearCustomData(clearDataOptions)
                                navigateBack()
                            }
                        )
                    }
                }
            }
        }
    }

    AddOrEditSearchEngineDialog(
        show = showAddEngineDialog,
        initialEngine = editingEngine,
        onDismiss = {
            showAddEngineDialog = false
            editingEngine = null
        },
        onSave = { name, searchUrl, suggestionUrl ->
            val current = editingEngine
            if (current == null) {
                onAddCustomEngine(name, searchUrl, suggestionUrl)
                android.widget.Toast.makeText(context, "已添加搜索引擎: $name", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                onUpdateCustomEngine(
                    current.copy(
                        name = name,
                        searchUrl = searchUrl,
                        suggestionUrl = suggestionUrl
                    )
                )
                android.widget.Toast.makeText(context, "已更新搜索引擎: $name", android.widget.Toast.LENGTH_SHORT).show()
            }
            showAddEngineDialog = false
            editingEngine = null
        }
    )

    OverlayDialog(
        show = engineToDelete != null,
        title = "删除搜索引擎",
        summary = "确定要删除「${engineToDelete?.name.orEmpty()}」吗？",
        onDismissRequest = { engineToDelete = null },
        renderInRootScaffold = false
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                TextButton(
                    text = "取消",
                    onClick = { engineToDelete = null }
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    text = "删除",
                    onClick = {
                        engineToDelete?.let { engine ->
                            onDeleteCustomEngine(engine.id)
                            android.widget.Toast.makeText(context, "已删除搜索引擎: ${engine.name}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        engineToDelete = null
                    }
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.settingsRoot(
    onAppearance: () -> Unit,
    onBrowser: () -> Unit,
    onDownloads: () -> Unit,
    onPrivacy: () -> Unit,
    onAdBlock: () -> Unit,
    onAbout: () -> Unit
) {
    item("root-main") {
        SmallTitle(text = "应用设置")
        SettingsGroup {
            CategoryPreference("外观与个性化", onAppearance)
            CategoryPreference("浏览设置", onBrowser)
            CategoryPreference("下载设置", onDownloads)
        }
    }
    item("root-security") {
        SmallTitle(text = "内容与隐私")
        SettingsGroup {
            CategoryPreference("广告拦截", onAdBlock)
            CategoryPreference("隐私与数据", onPrivacy)
        }
    }
    item("root-app") {
        SmallTitle(text = "应用")
        SettingsGroup {
            CategoryPreference("关于 Aeryo", onAbout)
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
    ) {
        content()
    }
}

@Composable
private fun CategoryPreference(
    title: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    ArrowPreference(
        title = title,
        onClick = onClick,
        modifier = Modifier.pressable(interactionSource)
    )
}

private fun androidx.compose.foundation.lazy.LazyListScope.appearanceSettings(
    currentThemeMode: String,
    currentThemePalette: String,
    currentThemeKeyColor: Long,
    onThemeModeChanged: (String) -> Unit,
    onThemePaletteChanged: (String) -> Unit,
    onThemeKeyColorChanged: (Long) -> Unit
) {
    val themeModes = listOf(
        "跟随系统（Monet）" to UserPreferences.THEME_MODE_SYSTEM,
        "Monet 浅色" to UserPreferences.THEME_MODE_MONET_LIGHT,
        "Monet 深色" to UserPreferences.THEME_MODE_MONET_DARK
    )
    val palettes = listOf(
        "Tonal Spot" to UserPreferences.THEME_PALETTE_TONAL_SPOT,
        "Vibrant" to UserPreferences.THEME_PALETTE_VIBRANT,
        "Expressive" to UserPreferences.THEME_PALETTE_EXPRESSIVE,
        "Neutral" to UserPreferences.THEME_PALETTE_NEUTRAL
    )
    val colors = listOf(
        "蓝色" to UserPreferences.THEME_KEY_BLUE,
        "绿色" to UserPreferences.THEME_KEY_GREEN,
        "橙色" to UserPreferences.THEME_KEY_ORANGE,
        "紫色" to UserPreferences.THEME_KEY_PURPLE,
        "红色" to UserPreferences.THEME_KEY_RED
    )
    item("appearance-theme") {
        SmallTitle(text = "主题设置")
        SettingsGroup {
            OverlayDropdownPreference(title = "主题模式", items = themeModes.map { it.first }, selectedIndex = themeModes.indexOfFirst { it.second == currentThemeMode }.coerceAtLeast(0), onSelectedIndexChange = { onThemeModeChanged(themeModes[it].second) }, renderInRootScaffold = false)
            OverlayDropdownPreference(title = "Monet 调色板", items = palettes.map { it.first }, selectedIndex = palettes.indexOfFirst { it.second == currentThemePalette }.coerceAtLeast(0), onSelectedIndexChange = { onThemePaletteChanged(palettes[it].second) }, renderInRootScaffold = false)
            OverlayDropdownPreference(title = "重点色", items = colors.map { it.first }, selectedIndex = colors.indexOfFirst { it.second == currentThemeKeyColor }.coerceAtLeast(0), onSelectedIndexChange = { onThemeKeyColorChanged(colors[it].second) }, renderInRootScaffold = false)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.browserSettings(
    currentSearchEngine: SearchEngine,
    currentAddressBarAnimationEnabled: Boolean,
    currentTabViewMode: String,
    currentVideoAutoLandscapeEnabled: Boolean,
    currentVideoKeepScreenOnEnabled: Boolean,
    currentVideoPipAutoEnabled: Boolean,
    currentVideoGestureEnabled: Boolean,
    currentBackgroundPlaybackEnabled: Boolean,
    currentMediaSnifferEnabled: Boolean,
    currentMediaSnifferFloatingEnabled: Boolean,
    currentMediaSnifferBadgeMode: String,
    currentEdgeSwipeNavigationEnabled: Boolean,
    currentSmartDarkModeEnabled: Boolean,
    currentQuickScrollButtonEnabled: Boolean,
    onNavigateToSearchEngines: () -> Unit,
    onSearchEngineChanged: (String) -> Unit,
    onAddressBarAnimationToggled: (Boolean) -> Unit,
    onTabViewModeChanged: (String) -> Unit,
    onVideoAutoLandscapeToggled: (Boolean) -> Unit,
    onVideoKeepScreenOnToggled: (Boolean) -> Unit,
    onVideoPipAutoToggled: (Boolean) -> Unit,
    onVideoGestureToggled: (Boolean) -> Unit,
    onBackgroundPlaybackToggled: (Boolean) -> Unit,
    onMediaSnifferToggled: (Boolean) -> Unit,
    onMediaSnifferFloatingToggled: (Boolean) -> Unit,
    onMediaSnifferBadgeModeChanged: (String) -> Unit,
    onEdgeSwipeNavigationToggled: (Boolean) -> Unit,
    onSmartDarkModeToggled: (Boolean) -> Unit,
    onQuickScrollButtonToggled: (Boolean) -> Unit
) {
    val tabModes = listOf("网格全屏卡片" to UserPreferences.TAB_VIEW_MODE_GRID, "半屏模式" to UserPreferences.TAB_VIEW_MODE_HALF)
    item("browser-general") {
        SmallTitle(text = "地址栏与搜索")
        SettingsGroup {
            ArrowPreference(
                title = "搜索引擎",
                summary = currentSearchEngine.name,
                onClick = onNavigateToSearchEngines
            )
            SwitchPreference(title = "主页地址栏动画", checked = currentAddressBarAnimationEnabled, onCheckedChange = onAddressBarAnimationToggled)
        }
    }
    item("browser-interaction") {
        SmallTitle(text = "日常浏览与交互")
        SettingsGroup {
            SwitchPreference(
                title = "边缘滑动手势导航",
                summary = "屏幕左/右边缘向内滑动快速触发网页后退与前进",
                checked = currentEdgeSwipeNavigationEnabled,
                onCheckedChange = onEdgeSwipeNavigationToggled
            )
            SwitchPreference(
                title = "网页智能夜间滤镜",
                summary = "夜间模式下对浅色网页智能反色，并二次反转保护图片与视频色彩",
                checked = currentSmartDarkModeEnabled,
                onCheckedChange = onSmartDarkModeToggled
            )
            SwitchPreference(
                title = "快捷回顶浮动按钮",
                summary = "长网页向下滚动时自动显示平滑一键回顶胶囊",
                checked = currentQuickScrollButtonEnabled,
                onCheckedChange = onQuickScrollButtonToggled
            )
        }
    }
    item("browser-video") {
        SmallTitle(text = "视频与多媒体")
        SettingsGroup {
            SwitchPreference(
                title = "视频全屏时自动横屏",
                summary = "全屏播放视频时自动旋转至横屏，退出全屏时自动恢复",
                checked = currentVideoAutoLandscapeEnabled,
                onCheckedChange = onVideoAutoLandscapeToggled
            )
            SwitchPreference(
                title = "视频全屏时保持屏幕常亮",
                summary = "全屏观影期间防止屏幕休眠熄灭",
                checked = currentVideoKeepScreenOnEnabled,
                onCheckedChange = onVideoKeepScreenOnToggled
            )
            SwitchPreference(
                title = "离开全屏时自动画中画",
                summary = "全屏播放时按 Home 键或切回桌面自动开启画中画",
                checked = currentVideoPipAutoEnabled,
                onCheckedChange = onVideoPipAutoToggled
            )
            SwitchPreference(
                title = "全屏观影手势控制",
                summary = "支持双侧滑动调亮度/音量、横向拖拽快进/快退、长按倍速",
                checked = currentVideoGestureEnabled,
                onCheckedChange = onVideoGestureToggled
            )
            SwitchPreference(
                title = "后台播放与息屏听剧",
                summary = "切后台或手机锁屏时保持网页音频继续播放",
                checked = currentBackgroundPlaybackEnabled,
                onCheckedChange = onBackgroundPlaybackToggled
            )
            SwitchPreference(
                title = "网页媒体资源智能嗅探",
                summary = "自动探测网页中的视频流，支持外调播放器与一键下载",
                checked = currentMediaSnifferEnabled,
                onCheckedChange = onMediaSnifferToggled
            )
            if (currentMediaSnifferEnabled) {
                SwitchPreference(
                    title = "网页浮动嗅探胶囊",
                    summary = "在嗅探到媒体时，网页右下角自动弹出浮动胶囊",
                    checked = currentMediaSnifferFloatingEnabled,
                    onCheckedChange = onMediaSnifferFloatingToggled
                )
                val badgeModes = listOf(
                    "红点提示" to UserPreferences.MEDIA_SNIFFER_BADGE_DOT,
                    "数字角标" to UserPreferences.MEDIA_SNIFFER_BADGE_COUNT,
                    "不显示提示" to UserPreferences.MEDIA_SNIFFER_BADGE_NONE
                )
                OverlayDropdownPreference(
                    title = "菜单嗅探提示样式",
                    items = badgeModes.map { it.first },
                    selectedIndex = badgeModes.indexOfFirst { it.second == currentMediaSnifferBadgeMode }.coerceAtLeast(0),
                    onSelectedIndexChange = { onMediaSnifferBadgeModeChanged(badgeModes[it].second) },
                    renderInRootScaffold = false
                )
            }
        }
    }
    item("browser-tabs") {
        SmallTitle(text = "标签页切换器")
        SettingsGroup {
            OverlayDropdownPreference(
                title = "标签页视图模式",
                items = tabModes.map { it.first },
                selectedIndex = tabModes.indexOfFirst { it.second == currentTabViewMode }.coerceAtLeast(0),
                onSelectedIndexChange = { onTabViewModeChanged(tabModes[it].second) },
                renderInRootScaffold = false
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.searchEnginesSettings(
    currentSearchEngine: SearchEngine,
    allSearchEngines: List<SearchEngine>,
    onSearchEngineSelected: (SearchEngine) -> Unit,
    onEditEngine: (SearchEngine) -> Unit,
    onDeleteEngine: (SearchEngine) -> Unit,
    onAddEngine: () -> Unit
) {
    val presets = allSearchEngines.filter { it.isPreset }
    val customs = allSearchEngines.filterNot { it.isPreset }

    item("engines_preset_header") {
        SmallTitle(text = "内置搜索引擎 (${presets.size})")
    }

    item("engines_preset_group") {
        SettingsGroup {
            presets.forEach { engine ->
                val isSelected = currentSearchEngine.id == engine.id ||
                    currentSearchEngine.searchUrl.equals(engine.searchUrl, ignoreCase = true)
                BasicComponent(
                    title = engine.name,
                    summary = engine.searchUrl,
                    onClick = { onSearchEngineSelected(engine) },
                    endActions = {
                        if (isSelected) {
                            Icon(
                                imageVector = TablerIcons.Check,
                                contentDescription = "已选择",
                                tint = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }
        }
    }

    item("engines_custom_header") {
        SmallTitle(text = "自定义搜索引擎 (${customs.size})")
    }

    item("engines_custom_group") {
        SettingsGroup {
            if (customs.isEmpty()) {
                BasicComponent(
                    title = "添加自定义搜索引擎",
                    summary = "支持自定义搜索 URL 及联想词 API 接口",
                    onClick = onAddEngine,
                    endActions = {
                        Icon(
                            imageVector = MiuixIcons.Add,
                            contentDescription = "添加",
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            } else {
                customs.forEach { engine ->
                    val isSelected = currentSearchEngine.id == engine.id ||
                        currentSearchEngine.searchUrl.equals(engine.searchUrl, ignoreCase = true)
                    BasicComponent(
                        title = engine.name,
                        summary = if (engine.suggestionUrl.isNotBlank()) {
                            "${engine.searchUrl}\n联想: ${engine.suggestionUrl}"
                        } else {
                            engine.searchUrl
                        },
                        onClick = { onSearchEngineSelected(engine) },
                        endActions = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onEditEngine(engine) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = MiuixIcons.Edit,
                                        contentDescription = "编辑",
                                        tint = MiuixTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteEngine(engine) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = MiuixIcons.Delete,
                                        contentDescription = "删除",
                                        tint = MiuixTheme.colorScheme.error ?: Color(0xFFE53935),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = TablerIcons.Check,
                                        contentDescription = "已选择",
                                        tint = MiuixTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    item("engines_help_header") {
        SmallTitle(text = "参数与接口说明")
    }

    item("engines_help_group") {
        SettingsGroup {
            BasicComponent(
                title = "搜索 URL 占位符",
                summary = "在搜索网址中使用 %s 代表关键词。例如：\nhttps://github.com/search?q=%s\n若未填写 %s，搜索词将自动拼接到网址末尾。"
            )
            BasicComponent(
                title = "联想词 API 接口（选填）",
                summary = "在联想词 API 中使用 %s 代表关键词。例如：\nhttps://api.bing.com/osjson.aspx?query=%s\n支持标准 OpenSearch JSON 建议与数组格式。"
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.downloadSettings(
    currentDownloadMode: String,
    currentDownloadPath: String,
    onDownloadModeChanged: (String) -> Unit,
    onPickDownloadPath: () -> Unit
) {
    val modes = listOf(
        "内置下载（支持暂停/续传）" to UserPreferences.DOWNLOAD_MODE_BUILT_IN,
        "系统下载（不可暂停）" to UserPreferences.DOWNLOAD_MODE_SYSTEM
    )
    item("download-mode") {
        SmallTitle(text = "下载方式")
        SettingsGroup {
            OverlayDropdownPreference(
                title = "下载管理器",
                items = modes.map { it.first },
                selectedIndex = modes.indexOfFirst { it.second == currentDownloadMode }.coerceAtLeast(0),
                onSelectedIndexChange = { onDownloadModeChanged(modes[it].second) },
                renderInRootScaffold = false
            )
        }
    }
    item("download-path") {
        SmallTitle(text = "存储位置")
        SettingsGroup {
            ArrowPreference(
                title = "自定义下载目录",
                summary = currentDownloadPath.ifBlank { "默认 Download 目录" },
                onClick = onPickDownloadPath
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.privacySettings(
    biometric: Boolean,
    doNotTrack: Boolean,
    blockThirdPartyCookies: Boolean,
    clearOnExit: Boolean,
    onBiometric: (Boolean) -> Unit,
    onDoNotTrack: (Boolean) -> Unit,
    onBlockThirdPartyCookies: (Boolean) -> Unit,
    onClearOnExit: (Boolean) -> Unit,
    onNavigateToClearData: () -> Unit
) {
    item("privacy-protection") {
        SmallTitle(text = "隐私保护")
        SettingsGroup {
            SwitchPreference(title = "隐私模式需要身份验证", checked = biometric, onCheckedChange = onBiometric)
            SwitchPreference(title = "发送 Do Not Track", checked = doNotTrack, onCheckedChange = onDoNotTrack)
            SwitchPreference(title = "阻止第三方 Cookie", checked = blockThirdPartyCookies, onCheckedChange = onBlockThirdPartyCookies)
        }
    }
    item("privacy-data") {
        SmallTitle(text = "数据管理")
        SettingsGroup {
            SwitchPreference(title = "退出时清理网页数据", checked = clearOnExit, onCheckedChange = onClearOnExit)
            ArrowPreference(title = "清除浏览数据", summary = "自定义清除缓存、历史、Cookie 等", onClick = onNavigateToClearData)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.clearDataSettings(
    options: ClearDataOptions,
    onOptionsChange: (ClearDataOptions) -> Unit,
    onExecuteClear: () -> Unit
) {
    item("clear-data-items") {
        SmallTitle(text = "选择要清除的项目")
        SettingsGroup {
            SwitchPreference(
                title = "缓存文件与图片",
                summary = "释放磁盘空间，加快页面加载",
                checked = options.clearCache,
                onCheckedChange = { onOptionsChange(options.copy(clearCache = it)) }
            )
            SwitchPreference(
                title = "表单与自动填充数据",
                summary = "已保存的账号密码与输入框历史",
                checked = options.clearFormData,
                onCheckedChange = { onOptionsChange(options.copy(clearFormData = it)) }
            )
            SwitchPreference(
                title = "浏览历史记录",
                summary = "所有普通与私密模式访问历史",
                checked = options.clearHistory,
                onCheckedChange = { onOptionsChange(options.copy(clearHistory = it)) }
            )
            SwitchPreference(
                title = "已关闭的标签页",
                summary = "清空标签页恢复栈",
                checked = options.clearClosedTabs,
                onCheckedChange = { onOptionsChange(options.copy(clearClosedTabs = it)) }
            )
            SwitchPreference(
                title = "网页存储与站点数据",
                summary = "LocalStorage、IndexedDB 与离线数据",
                checked = options.clearWebStorage,
                onCheckedChange = { onOptionsChange(options.copy(clearWebStorage = it)) }
            )
            SwitchPreference(
                title = "Cookie 与登录状态",
                summary = "保持登录的网站凭证（清除后需重新登录）",
                checked = options.clearCookies,
                onCheckedChange = { onOptionsChange(options.copy(clearCookies = it)) }
            )
            SwitchPreference(
                title = "应用运行时缓存",
                summary = "临时文件、图标缓存与崩溃日志",
                checked = options.clearAppCache,
                onCheckedChange = { onOptionsChange(options.copy(clearAppCache = it)) }
            )
        }
    }
    item("clear-data-actions") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    text = if (options.hasAnySelected) "全不选" else "全选",
                    onClick = {
                        val selectAll = !options.hasAnySelected
                        onOptionsChange(
                            ClearDataOptions(
                                clearCache = selectAll,
                                clearFormData = selectAll,
                                clearHistory = selectAll,
                                clearClosedTabs = selectAll,
                                clearWebStorage = selectAll,
                                clearCookies = selectAll,
                                clearAppCache = selectAll
                            )
                        )
                    }
                )
                Button(
                    onClick = onExecuteClear,
                    enabled = options.hasAnySelected,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text("立即清除")
                }
            }
        }
    }
}

private fun themeLabel(mode: String) = when (UserPreferences.normalizeThemeMode(mode)) {
    UserPreferences.THEME_MODE_MONET_LIGHT -> "Monet 浅色"
    UserPreferences.THEME_MODE_MONET_DARK -> "Monet 深色"
    else -> "跟随系统（Monet）"
}

private fun engineLabel(engine: String) = UserPreferences.resolveSearchEngine(engine).name
