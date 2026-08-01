package net.zzbuaoye.aeryo.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

private enum class SettingsPage(val title: String) {
    ROOT("设置"),
    APPEARANCE("外观与个性化"),
    LOGO("应用 Logo 与图标"),
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
    currentGlassEffectEnabled: Boolean = true,
    currentBlurEffectEnabled: Boolean = false,
    currentLogoVariant: String = UserPreferences.LOGO_VARIANT_DEFAULT,
    currentLogoStrokeWidth: Float = UserPreferences.DEFAULT_LOGO_STROKE_WIDTH,
    currentLogoCustomColor: Long = UserPreferences.DEFAULT_LOGO_CUSTOM_COLOR,
    currentLogoCustomColorEnabled: Boolean = false,
    currentLogoCustomText: String = "A",
    currentLogoOffsetX: Float = 0f,
    currentLogoOffsetY: Float = 0f,
    currentLauncherIconVariant: String = UserPreferences.LAUNCHER_ICON_DEFAULT,
    supportsLiquidGlass: Boolean = true,
    supportsBackgroundBlur: Boolean = true,
    customLogoPainter: Painter? = null,
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
    onGlassEffectToggled: (Boolean) -> Unit = {},
    onBlurEffectToggled: (Boolean) -> Unit = {},
    onLogoVariantChanged: (String) -> Unit = {},
    onLogoStrokeWidthChanged: (Float) -> Unit = {},
    onLogoCustomColorChanged: (Long) -> Unit = {},
    onLogoCustomColorEnabledChanged: (Boolean) -> Unit = {},
    onPickCustomLogo: () -> Unit = {},
    onLogoCustomTextChanged: (String) -> Unit = {},
    onLogoOffsetXChanged: (Float) -> Unit = {},
    onLogoOffsetYChanged: (Float) -> Unit = {},
    onLauncherIconVariantChanged: (String) -> Unit = {},
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

    fun navigateBack() {
        page = when (page) {
            SettingsPage.LOGO -> SettingsPage.APPEARANCE
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
            TopAppBar(
                title = page.title,
                navigationIcon = {
                    IconButton(onClick = ::navigateBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
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
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding() + 16.dp
                    )
                ) {
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
                        currentGlassEffectEnabled,
                        currentBlurEffectEnabled,
                        supportsLiquidGlass,
                        supportsBackgroundBlur,
                        onThemeModeChanged,
                        onThemePaletteChanged,
                        onThemeKeyColorChanged,
                        onGlassEffectToggled,
                        onBlurEffectToggled,
                        onOpenLogo = { page = SettingsPage.LOGO }
                    )

                    SettingsPage.LOGO -> logoSettings(
                        currentLogoVariant,
                        currentLogoStrokeWidth,
                        currentLogoCustomColor,
                        currentLogoCustomColorEnabled,
                        currentLogoCustomText,
                        currentLogoOffsetX,
                        currentLogoOffsetY,
                        currentLauncherIconVariant,
                        customLogoPainter,
                        onLogoVariantChanged,
                        onLogoStrokeWidthChanged,
                        onLogoCustomColorChanged,
                        onLogoCustomColorEnabledChanged,
                        onPickCustomLogo,
                        onLogoCustomTextChanged,
                        onLogoOffsetXChanged,
                        onLogoOffsetYChanged,
                        onLauncherIconVariantChanged
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
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) { content() }
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
    currentGlassEffectEnabled: Boolean,
    currentBlurEffectEnabled: Boolean,
    supportsLiquidGlass: Boolean,
    supportsBackgroundBlur: Boolean,
    onThemeModeChanged: (String) -> Unit,
    onThemePaletteChanged: (String) -> Unit,
    onThemeKeyColorChanged: (Long) -> Unit,
    onGlassEffectToggled: (Boolean) -> Unit,
    onBlurEffectToggled: (Boolean) -> Unit,
    onOpenLogo: () -> Unit
) {
    val themeModes = listOf(
        "跟随系统（Monet）" to UserPreferences.THEME_MODE_SYSTEM,
        "浅色" to UserPreferences.THEME_MODE_LIGHT,
        "深色" to UserPreferences.THEME_MODE_DARK,
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
    if (supportsLiquidGlass || supportsBackgroundBlur) {
        item("appearance-effects") {
            SmallTitle(text = "界面效果")
            SettingsGroup {
                if (supportsLiquidGlass) {
                    SwitchPreference(title = "液态玻璃", checked = currentGlassEffectEnabled, onCheckedChange = onGlassEffectToggled)
                }
                if (supportsBackgroundBlur) {
                    SwitchPreference(title = "毛玻璃模糊", checked = currentBlurEffectEnabled, onCheckedChange = onBlurEffectToggled)
                }
            }
        }
    }
    item("appearance-logo") {
        SmallTitle(text = "标志与图标")
        SettingsGroup {
            ArrowPreference(title = "应用 Logo 与桌面图标", onClick = onOpenLogo)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.logoSettings(
    variant: String,
    strokeWidth: Float,
    customColor: Long,
    customColorEnabled: Boolean,
    customText: String,
    offsetX: Float,
    offsetY: Float,
    launcherIconVariant: String,
    customLogoPainter: Painter?,
    onVariantChanged: (String) -> Unit,
    onStrokeWidthChanged: (Float) -> Unit,
    onCustomColorChanged: (Long) -> Unit,
    onCustomColorEnabledChanged: (Boolean) -> Unit,
    onPickCustomLogo: () -> Unit,
    onCustomTextChanged: (String) -> Unit,
    onOffsetXChanged: (Float) -> Unit,
    onOffsetYChanged: (Float) -> Unit,
    onLauncherIconVariantChanged: (String) -> Unit
) {
    val variants = listOf(
        "默认（软件图标）" to UserPreferences.LOGO_VARIANT_DEFAULT,
        "Aurora 极光" to UserPreferences.LOGO_VARIANT_AURORA,
        "Sunset 日落" to UserPreferences.LOGO_VARIANT_SUNSET,
        "Mint 薄荷" to UserPreferences.LOGO_VARIANT_MINT,
        "Monochrome 单色" to UserPreferences.LOGO_VARIANT_MONO,
        "自定义图片" to UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE,
        "自定义文字" to UserPreferences.LOGO_VARIANT_CUSTOM_TEXT
    )
    val launcherVariants = listOf(
        "默认图标" to UserPreferences.LAUNCHER_ICON_DEFAULT,
        "焰尾渐变" to UserPreferences.LAUNCHER_ICON_FLAME,
        "Aurora 极光" to UserPreferences.LAUNCHER_ICON_AURORA,
        "Sunset 日落" to UserPreferences.LAUNCHER_ICON_SUNSET,
        "Ocean 海洋" to UserPreferences.LAUNCHER_ICON_OCEAN
    )
    item("logo-preview") {
        SmallTitle(text = "实时预览")
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(170.dp), contentAlignment = Alignment.Center) {
                LogoPreview(variant, strokeWidth, customColor, customColorEnabled, customText, offsetX, offsetY, customLogoPainter)
            }
        }
    }
    item("logo-style") {
        SmallTitle(text = "主页 Logo")
        SettingsGroup {
            OverlayDropdownPreference(title = "Logo 样式", items = variants.map { it.first }, selectedIndex = variants.indexOfFirst { it.second == variant }.coerceAtLeast(0), onSelectedIndexChange = { onVariantChanged(variants[it].second) })
            if (variant != UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("线条粗细")
                        Text("${strokeWidth.toInt()} dp", color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    }
                    Slider(value = strokeWidth, onValueChange = onStrokeWidthChanged, valueRange = 6f..22f, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                }
                SwitchPreference(title = "使用自定义颜色", checked = customColorEnabled, onCheckedChange = onCustomColorEnabledChanged)
            }
            if (variant == UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE) {
                ArrowPreference(title = "选择本地图片", onClick = onPickCustomLogo)
            }
            if (variant == UserPreferences.LOGO_VARIANT_CUSTOM_TEXT) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    TextField(value = customText, onValueChange = onCustomTextChanged, label = "自定义文字（最多 8 个字符）", modifier = Modifier.fillMaxWidth())
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("水平位置")
                    Text("${offsetX.toInt()} dp", color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
                Slider(value = offsetX, onValueChange = onOffsetXChanged, valueRange = -60f..60f, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("垂直位置")
                    Text("${offsetY.toInt()} dp", color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
                Slider(value = offsetY, onValueChange = onOffsetYChanged, valueRange = -60f..60f, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
            }
        }
    }
    if (customColorEnabled && variant != UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE) {
        item("logo-color") {
            SmallTitle(text = "自定义颜色")
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                ColorPicker(
                    color = Color(customColor),
                    onColorChanged = { onCustomColorChanged(it.value.toLong()) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }
    item("launcher-icon") {
        SmallTitle(text = "桌面软件图标")
        SettingsGroup {
            OverlayDropdownPreference(title = "图标配色", items = launcherVariants.map { it.first }, selectedIndex = launcherVariants.indexOfFirst { it.second == launcherIconVariant }.coerceAtLeast(0), onSelectedIndexChange = { onLauncherIconVariantChanged(launcherVariants[it].second) })
        }
    }
}

@Composable
private fun LogoPreview(
    variant: String,
    strokeWidth: Float,
    customColor: Long,
    customColorEnabled: Boolean,
    customText: String,
    offsetX: Float,
    offsetY: Float,
    customLogoPainter: Painter?
) {
    Box(modifier = Modifier.size(132.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.offset(offsetX.dp, offsetY.dp), contentAlignment = Alignment.Center) {
            when (variant) {
                UserPreferences.LOGO_VARIANT_DEFAULT -> AeryoLineMark(variant, strokeWidth, customColor, customColorEnabled)
                UserPreferences.LOGO_VARIANT_CUSTOM_IMAGE -> if (customLogoPainter != null) {
                    Image(customLogoPainter, "自定义 Logo", Modifier.size(112.dp))
                } else {
                    Text("请选择图片", color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
                UserPreferences.LOGO_VARIANT_CUSTOM_TEXT -> Text(
                    text = customText.ifBlank { "A" },
                    color = if (customColorEnabled) Color(customColor) else MiuixTheme.colorScheme.primary,
                    fontSize = 58.sp,
                    fontWeight = if (strokeWidth >= 15f) FontWeight.Black else if (strokeWidth >= 10f) FontWeight.Bold else FontWeight.Medium
                )
                else -> AeryoLineMark(variant, strokeWidth, customColor, customColorEnabled)
            }
        }
    }
}

@Composable
private fun AeryoLineMark(variant: String, strokeWidth: Float, customColor: Long, customColorEnabled: Boolean) {
    val monoColor = MiuixTheme.colorScheme.onBackground
    Canvas(modifier = Modifier.size(112.dp)) {
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
        val brush = if (customColorEnabled) SolidColor(Color(customColor)) else when (variant) {
            UserPreferences.LOGO_VARIANT_SUNSET -> Brush.linearGradient(listOf(Color(0xFFFFC247), Color(0xFFFF7043), Color(0xFFE04F88)), Offset.Zero, Offset(size.width, size.height))
            UserPreferences.LOGO_VARIANT_MINT -> Brush.linearGradient(listOf(Color(0xFF50D8AE), Color(0xFF27B7A5), Color(0xFF3A88D8)), Offset.Zero, Offset(size.width, size.height))
            UserPreferences.LOGO_VARIANT_MONO -> SolidColor(monoColor)
            UserPreferences.LOGO_VARIANT_DEFAULT -> Brush.linearGradient(listOf(Color(0xFFFFB13B), Color(0xFFF16A55), Color(0xFF8A4FD2)), Offset.Zero, Offset(size.width, size.height))
            else -> Brush.linearGradient(listOf(Color(0xFF4DA3FF), Color(0xFF806BFF), Color(0xFFC35BDF)), Offset.Zero, Offset(size.width, size.height))
        }
        drawPath(path, brush, style = Stroke(width = strokeWidth.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
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
    val modes = listOf("系统下载" to UserPreferences.DOWNLOAD_MODE_SYSTEM, "内置下载" to UserPreferences.DOWNLOAD_MODE_BUILT_IN)
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

private fun themeLabel(mode: String) = when (mode) {
    UserPreferences.THEME_MODE_LIGHT -> "浅色"
    UserPreferences.THEME_MODE_DARK -> "深色"
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
