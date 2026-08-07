package net.zzbuaoye.aeryo.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

private enum class SettingsPage(val title: String) {
    ROOT("设置"),
    APPEARANCE("外观与个性化"),
    BROWSER("浏览设置"),
    DOWNLOADS("下载设置"),
    PRIVACY("隐私与数据")
}

@Composable
fun SettingsScreen(
    currentSearchEngine: String,
    currentAddressBarAnimationEnabled: Boolean,
    currentDownloadMode: String,
    currentThemeMode: String = UserPreferences.THEME_MODE_SYSTEM,
    currentThemePalette: String = UserPreferences.THEME_PALETTE_TONAL_SPOT,
    currentThemeKeyColor: Long = UserPreferences.DEFAULT_THEME_KEY_COLOR,
    currentPrivacyBiometricEnabled: Boolean = false,
    currentDoNotTrackEnabled: Boolean = true,
    currentBlockThirdPartyCookies: Boolean = false,
    currentClearOnExit: Boolean = false,
    onSearchEngineChanged: (String) -> Unit,
    onAdBlockSettingsClicked: () -> Unit,
    onAddressBarAnimationToggled: (Boolean) -> Unit,
    currentTabViewMode: String = UserPreferences.TAB_VIEW_MODE_GRID,
    onTabViewModeChanged: (String) -> Unit = {},
    onDownloadModeChanged: (String) -> Unit,
    onThemeModeChanged: (String) -> Unit = {},
    onThemePaletteChanged: (String) -> Unit = {},
    onThemeKeyColorChanged: (Long) -> Unit = {},
    onPrivacyBiometricToggled: (Boolean) -> Unit = {},
    onDoNotTrackToggled: (Boolean) -> Unit = {},
    onBlockThirdPartyCookiesToggled: (Boolean) -> Unit = {},
    onClearOnExitToggled: (Boolean) -> Unit = {},
    onClearData: () -> Unit,
    onOpenAbout: () -> Unit,
    onBack: () -> Unit
) {
    var page by remember { mutableStateOf(SettingsPage.ROOT) }
    val topAppBarScrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberMiuixWindowBackdrop()

    fun navigateBack() {
        page = when (page) {
            SettingsPage.ROOT -> {
                onBack()
                SettingsPage.ROOT
            }
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
                            onSearchEngineChanged,
                            onAddressBarAnimationToggled,
                            onTabViewModeChanged
                        )

                        SettingsPage.DOWNLOADS -> downloadSettings(
                            currentDownloadMode,
                            onDownloadModeChanged
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
                            onClearData
                        )
                    }
                }
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
            CategoryPreference("关于 Aeryo Browser", onAbout)
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
            OverlayDropdownPreference(title = "主题模式", items = themeModes.map { it.first }, selectedIndex = themeModes.indexOfFirst { it.second == currentThemeMode }.coerceAtLeast(0), onSelectedIndexChange = { onThemeModeChanged(themeModes[it].second) })
            OverlayDropdownPreference(title = "Monet 调色板", items = palettes.map { it.first }, selectedIndex = palettes.indexOfFirst { it.second == currentThemePalette }.coerceAtLeast(0), onSelectedIndexChange = { onThemePaletteChanged(palettes[it].second) })
            OverlayDropdownPreference(title = "重点色", items = colors.map { it.first }, selectedIndex = colors.indexOfFirst { it.second == currentThemeKeyColor }.coerceAtLeast(0), onSelectedIndexChange = { onThemeKeyColorChanged(colors[it].second) })
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.browserSettings(
    currentSearchEngine: String,
    currentAddressBarAnimationEnabled: Boolean,
    currentTabViewMode: String,
    onSearchEngineChanged: (String) -> Unit,
    onAddressBarAnimationToggled: (Boolean) -> Unit,
    onTabViewModeChanged: (String) -> Unit
) {
    val engines = listOf("Bing" to UserPreferences.ENGINE_BING, "Google" to UserPreferences.ENGINE_GOOGLE, "Baidu" to UserPreferences.ENGINE_BAIDU, "DuckDuckGo" to UserPreferences.ENGINE_DUCKDUCKGO, "Yahoo" to UserPreferences.ENGINE_YAHOO, "Yandex" to UserPreferences.ENGINE_YANDEX, "360" to UserPreferences.ENGINE_360, "Sogou" to UserPreferences.ENGINE_SOGOU)
    val tabModes = listOf("网格全屏卡片" to UserPreferences.TAB_VIEW_MODE_GRID, "半屏模式" to UserPreferences.TAB_VIEW_MODE_HALF)
    item("browser-general") {
        SmallTitle(text = "地址栏与搜索")
        SettingsGroup {
            OverlayDropdownPreference(title = "默认搜索引擎", items = engines.map { it.first }, selectedIndex = engines.indexOfFirst { it.second == currentSearchEngine }.coerceAtLeast(0), onSelectedIndexChange = { onSearchEngineChanged(engines[it].second) })
            SwitchPreference(title = "主页地址栏动画", checked = currentAddressBarAnimationEnabled, onCheckedChange = onAddressBarAnimationToggled)
        }
    }
    item("browser-tabs") {
        SmallTitle(text = "标签页切换器")
        SettingsGroup {
            OverlayDropdownPreference(
                title = "标签页视图模式",
                items = tabModes.map { it.first },
                selectedIndex = tabModes.indexOfFirst { it.second == currentTabViewMode }.coerceAtLeast(0),
                onSelectedIndexChange = { onTabViewModeChanged(tabModes[it].second) }
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.downloadSettings(currentDownloadMode: String, onDownloadModeChanged: (String) -> Unit) {
    val modes = listOf(
        "内置下载（支持暂停）" to UserPreferences.DOWNLOAD_MODE_BUILT_IN,
        "系统下载（不可暂停）" to UserPreferences.DOWNLOAD_MODE_SYSTEM
    )
    item("download-mode") {
        SmallTitle(text = "下载方式")
        SettingsGroup {
            OverlayDropdownPreference(title = "下载管理器", items = modes.map { it.first }, selectedIndex = modes.indexOfFirst { it.second == currentDownloadMode }.coerceAtLeast(0), onSelectedIndexChange = { onDownloadModeChanged(modes[it].second) })
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
    onClearData: () -> Unit
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
            ArrowPreference(title = "清除浏览数据", onClick = onClearData)
        }
    }
}

private fun themeLabel(mode: String) = when (UserPreferences.normalizeThemeMode(mode)) {
    UserPreferences.THEME_MODE_MONET_LIGHT -> "Monet 浅色"
    UserPreferences.THEME_MODE_MONET_DARK -> "Monet 深色"
    else -> "跟随系统（Monet）"
}

private fun engineLabel(engine: String) = when (engine) {
    UserPreferences.ENGINE_GOOGLE -> "Google"
    UserPreferences.ENGINE_BAIDU -> "Baidu"
    UserPreferences.ENGINE_DUCKDUCKGO -> "DuckDuckGo"
    UserPreferences.ENGINE_YAHOO -> "Yahoo"
    UserPreferences.ENGINE_YANDEX -> "Yandex"
    UserPreferences.ENGINE_360 -> "360"
    UserPreferences.ENGINE_SOGOU -> "Sogou"
    else -> "Bing"
}
