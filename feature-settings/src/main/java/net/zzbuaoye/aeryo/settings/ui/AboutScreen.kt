package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.settings.ui.effect.BgEffectBackground
import net.zzbuaoye.aeryo.settings.ui.effect.ColorBlendToken
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurBlendMode
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import androidx.compose.ui.graphics.BlendMode as ComposeBlendMode

private const val MIUIX_SOURCE_URL = "https://github.com/compose-miuix-ui/miuix"
private const val MIUIX_DOCUMENTATION_URL = "https://compose-miuix-ui.github.io/miuix/dokka/index.html"
private const val ZZBUAOYE_GITHUB_URL = "https://github.com/buaoyezz"
private const val AERYO_GITHUB_URL = "https://github.com/buaoyezz/Aeryo"
private const val TABLER_ICONS_URL = "https://tabler.io/icons"

/**
 * 页面骨架直接对应 Miuix 官方 example/AboutPage：
 * SmallTopAppBar 随 Logo 区滚动渐入，Backdrop 只在完全折叠后启用。
 */
@Composable
fun AboutScreen(
    appIcon: Painter,
    versionName: String,
    versionCode: Long,
    packageName: String,
    webViewVersion: String,
    versionChannelName: String,
    onBack: () -> Unit,
    onOpenLicenses: (() -> Unit)? = null,
) {
    val lazyListState = rememberLazyListState()
    val topAppBarScrollBehavior = MiuixScrollBehavior()
    val scrollProgress by remember {
        derivedStateOf {
            when {
                lazyListState.firstVisibleItemIndex > 0 -> 1f
                else -> {
                    val spacer = lazyListState.layoutInfo.visibleItemsInfo
                        .firstOrNull { it.key == "logoSpacer" }
                    if (spacer != null && spacer.size > 0) {
                        (lazyListState.firstVisibleItemScrollOffset.toFloat() / spacer.size)
                            .coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                }
            }
        }
    }
    val topBarBackdrop = rememberMiuixWindowBackdrop()
    val blurActive = topBarBackdrop != null && scrollProgress >= 0.999f
    val barColor = when {
        blurActive -> Color.Transparent
        scrollProgress >= 0.999f -> miuixWindowBackgroundColor()
        else -> Color.Transparent
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiuixBlurredBar(topBarBackdrop, blurActive) {
                SmallTopAppBar(
                    title = "关于 Aeryo",
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    titleColor = MiuixTheme.colorScheme.onSurface.copy(
                        alpha = ((scrollProgress - 0.35f) / 0.65f).coerceIn(0f, 1f),
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                )
            }
        },
        containerColor = miuixWindowBackgroundColor(),
    ) { innerPadding ->
        Box(
            modifier = topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier,
        ) {
            AboutContent(
                padding = innerPadding,
                appIcon = appIcon,
                versionName = versionName,
                versionCode = versionCode,
                packageName = packageName,
                webViewVersion = webViewVersion,
                versionChannelName = versionChannelName,
                lazyListState = lazyListState,
                scrollBehavior = topAppBarScrollBehavior,
                scrollProgress = scrollProgress,
                onOpenLicenses = onOpenLicenses,
            )
        }
    }
}

@Composable
private fun AboutContent(
    padding: PaddingValues,
    appIcon: Painter,
    versionName: String,
    versionCode: Long,
    packageName: String,
    webViewVersion: String,
    versionChannelName: String,
    lazyListState: LazyListState,
    scrollBehavior: ScrollBehavior,
    scrollProgress: Float,
    onOpenLicenses: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val contentBackdrop = rememberMiuixWindowBackdrop()
    val shaderSupported = remember { isRuntimeShaderSupported() }
    val dynamicBackground = remember(shaderSupported) { mutableStateOf(shaderSupported) }
    val isDark = miuixWindowBackgroundColor().luminance() < 0.5f
    val cardBlend = if (isDark) {
        ColorBlendToken.Overlay_Thin_Light
    } else {
        ColorBlendToken.Pured_Regular_Light
    }
    val logoBlend = remember(isDark) {
        if (isDark) {
            listOf(
                BlendColorEntry(Color(0xE6A1A1A1), BlurBlendMode.ColorDodge),
                BlendColorEntry(Color(0x4DE6E6E6), BlurBlendMode.LinearLight),
                BlendColorEntry(Color(0xFF1AF500), BlurBlendMode.Lab),
            )
        } else {
            listOf(
                BlendColorEntry(Color(0xCC4A4A4A), BlurBlendMode.ColorBurn),
                BlendColorEntry(Color(0xFF4F4F4F), BlurBlendMode.LinearLight),
                BlendColorEntry(Color(0xFF1AF200), BlurBlendMode.Lab),
            )
        }
    }
    val density = LocalDensity.current
    var logoHeightDp by remember { mutableStateOf(300.dp) }
    val versionProgress = ((scrollProgress - 0.05f) / 0.15f).coerceIn(0f, 1f)
    val projectProgress = ((scrollProgress - 0.20f) / 0.15f).coerceIn(0f, 1f)
    val iconProgress = ((scrollProgress - 0.35f) / 0.15f).coerceIn(0f, 1f)

    BgEffectBackground(
        dynamicBackground = dynamicBackground.value,
        isOs3Effect = true,
        isFullSize = true,
        modifier = Modifier.fillMaxSize(),
        bgModifier = contentBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier,
        alpha = { 1f - scrollProgress },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = padding.calculateTopPadding() + 92.dp,
                    start = 12.dp,
                    end = 12.dp,
                )
                .onSizeChanged { size ->
                    with(density) { logoHeightDp = size.height.toDp() }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(88.dp),
            ) {
                Image(
                    painter = appIcon,
                    contentDescription = "Aeryo Logo",
                    // Match InstallerX's Miuix About header: keep an 88 dp layout box,
                    // but oversize adaptive-icon artwork so its safe-zone padding does not
                    // make the visible mark look tiny.
                    modifier = Modifier
                        .requiredSize(160.dp)
                        .graphicsLayer {
                            // Keep the animation layer at the artwork's full size. Applying
                            // alpha to the 88 dp layout box creates an offscreen layer that
                            // trims the Aeryo stroke by a couple of pixels while scrolling.
                            alpha = 1f - iconProgress
                            scaleX = 1f - iconProgress * 0.05f
                            scaleY = 1f - iconProgress * 0.05f
                        }
                        .then(
                            if (contentBackdrop != null) {
                                Modifier.textureBlur(
                                    backdrop = contentBackdrop,
                                    shape = RoundedCornerShape(16.dp),
                                    blurRadius = 200f,
                                    colors = BlurDefaults.blurColors(blendColors = logoBlend),
                                    contentBlendMode = ComposeBlendMode.DstIn,
                                )
                            } else {
                                Modifier
                            },
                        ),
                )
            }
            Text(
                text = "Aeryo",
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 5.dp)
                    .graphicsLayer {
                        alpha = 1f - projectProgress
                        scaleX = 1f - projectProgress * 0.05f
                        scaleY = 1f - projectProgress * 0.05f
                    }
                    .then(
                        if (contentBackdrop != null) {
                            Modifier.textureBlur(
                                backdrop = contentBackdrop,
                                shape = RoundedCornerShape(16.dp),
                                blurRadius = 150f,
                                colors = BlurDefaults.blurColors(blendColors = logoBlend),
                                contentBlendMode = ComposeBlendMode.DstIn,
                            )
                        } else {
                            Modifier
                        },
                    ),
                color = MiuixTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
            )
            Text(
                text = "v$versionName · build $versionCode",
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = 1f - versionProgress
                        scaleX = 1f - versionProgress * 0.05f
                        scaleY = 1f - versionProgress * 0.05f
                    },
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = padding.calculateTopPadding(),
                end = 12.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            overscrollEffect = null,
        ) {
            item(key = "logoSpacer") {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(logoHeightDp + 218.dp),
                )
            }

            item(key = "author") {
                SmallTitle(text = "作者")
                AboutGlassCard(contentBackdrop, cardBlend) {
                    AboutLinkPreference(
                        title = "ZZBuAoYe",
                        summary = "@buaoyezz · 核心开发者",
                        onClick = { uriHandler.openUri(ZZBUAOYE_GITHUB_URL) },
                    )
                }
            }

            item(key = "build_info") {
                SmallTitle(text = "系统与构建信息")
                AboutGlassCard(contentBackdrop, cardBlend) {
                    if (versionChannelName.isNotBlank() && !versionChannelName.equals("Stable", ignoreCase = true)) {
                        AboutInfoPreference(title = "通道", value = versionChannelName)
                    }
                    AboutInfoPreference(title = "版本", value = versionName)
                    AboutInfoPreference(title = "构建", value = versionCode.toString())
                    AboutInfoPreference(title = "WebView", value = webViewVersion)
                    // AboutInfoPreference(title = "包名", value = packageName)
                }
            }

            item(key = "project") {
                SmallTitle(text = "项目")
                AboutGlassCard(contentBackdrop, cardBlend) {
                    AboutLinkPreference(
                        title = "Aeryo",
                        summary = "项目主页 · GitHub",
                        onClick = { uriHandler.openUri(AERYO_GITHUB_URL) },
                    )
                    AboutLinkPreference(
                        title = "Miuix 文档",
                        summary = "官方组件文档",
                        onClick = { uriHandler.openUri(MIUIX_DOCUMENTATION_URL) },
                    )
                }
            }

            item(key = "open_source") {
                SmallTitle(text = "开放源代码许可")
                AboutGlassCard(contentBackdrop, cardBlend) {
                    if (onOpenLicenses != null) {
                        AboutLinkPreference(
                            title = "开放源代码许可",
                            summary = "查看 Aeryo 所使用的全部开源项目与许可协议",
                            onClick = onOpenLicenses,
                        )
                    }
                    AboutLinkPreference(
                        title = "MIUIX KMP",
                        summary = "HyperOS / MIUI 风格 Compose UI 组件库",
                        onClick = { uriHandler.openUri(MIUIX_SOURCE_URL) },
                    )
                    AboutLinkPreference(
                        title = "Tabler Icons",
                        summary = "矢量图标库",
                        onClick = { uriHandler.openUri(TABLER_ICONS_URL) },
                    )
                }
            }

            item(key = "copyright") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Aeryo Browser",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Text(
                        text = "GNU General Public License v3.0",
                        style = MiuixTheme.textStyles.footnote2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutGlassCard(
    backdrop: LayerBackdrop?,
    blendColors: List<BlendColorEntry>,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .then(
                if (backdrop != null) {
                    Modifier.textureBlur(
                        backdrop = backdrop,
                        shape = RoundedCornerShape(16.dp),
                        blurRadius = 60f,
                        colors = BlurDefaults.blurColors(blendColors = blendColors),
                    )
                } else {
                    Modifier
                },
            ),
        colors = CardDefaults.defaultColors(
            color = if (backdrop != null) Color.Transparent else MiuixTheme.colorScheme.surfaceContainer,
            contentColor = MiuixTheme.colorScheme.onSurfaceContainer,
        ),
    ) {
        content()
    }
}

@Composable
private fun AboutLinkPreference(
    title: String,
    summary: String,
    onClick: () -> Unit,
) {
    ArrowPreference(
        title = title,
        summary = summary,
        summaryColor = BasicComponentDefaults.summaryColor(),
        onClick = onClick,
    )
}

@Composable
private fun AboutInfoPreference(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.subtitle,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
