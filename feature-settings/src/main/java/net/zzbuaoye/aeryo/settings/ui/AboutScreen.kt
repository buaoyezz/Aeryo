package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.shader.RuntimeShader
import top.yukonga.miuix.kmp.shader.asBrush
import top.yukonga.miuix.kmp.shader.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val MIUIX_SOURCE_URL = "https://github.com/compose-miuix-ui/miuix"
private const val MIUIX_DOCUMENTATION_URL = "https://compose-miuix-ui.github.io/miuix/dokka/index.html"
private const val ZZBUAOYE_GITHUB_URL = "https://github.com/buaoyezz"
private const val ZZBUAOYE_OFFICIAL_WEBSITE_URL = "https://aeryo.zzbuaoye.net/"

private val AboutTopBarHeight = 56.dp
private val AboutTopInset = 80.dp

private const val ABOUT_FLOW_SHADER = """
uniform float2 uResolution;
uniform float uTime;
uniform float4 uBackground;
uniform float4 uPrimary;
uniform float4 uSecondary;
uniform float4 uTertiary;

float glow(float2 point, float2 center, float radius) {
    float2 distance = point - center;
    return exp(-dot(distance, distance) / radius);
}

half4 main(float2 position) {
    float2 uv = position / uResolution;
    float time = uTime * 6.2831853;

    float2 primaryCenter = float2(
        0.20 + 0.12 * sin(time * 0.21),
        0.24 + 0.10 * cos(time * 0.17)
    );
    float2 secondaryCenter = float2(
        0.78 + 0.14 * cos(time * 0.16),
        0.40 + 0.12 * sin(time * 0.23)
    );
    float2 tertiaryCenter = float2(
        0.48 + 0.17 * sin(time * 0.13 + 2.0),
        0.84 + 0.10 * cos(time * 0.19 + 1.0)
    );

    float primaryGlow = glow(uv, primaryCenter, 0.18);
    float secondaryGlow = glow(uv, secondaryCenter, 0.20);
    float tertiaryGlow = glow(uv, tertiaryCenter, 0.16);
    float wave = 0.5 + 0.5 * sin(uv.x * 3.5 + uv.y * 2.0 + time * 0.18);

    float3 color = uBackground.rgb;
    color = mix(color, uPrimary.rgb, 0.34 * primaryGlow);
    color = mix(color, uSecondary.rgb, 0.36 * secondaryGlow);
    color = mix(color, uTertiary.rgb, 0.30 * tertiaryGlow);
    color = mix(color, uSecondary.rgb, 0.06 * wave);

    return half4(color, 1.0);
}
"""

@Composable
fun AboutScreen(
    appIcon: Painter,
    versionName: String,
    versionCode: Long,
    packageName: String,
    webViewVersion: String,
    androidVersion: String,
    onBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val colors = MiuixTheme.colorScheme
    val cardColors = CardDefaults.defaultColors(
        color = colors.surface.copy(alpha = 0.86f)
    )
    AboutFlowingBackground(
        colors = colors,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp),
                contentPadding = PaddingValues(
                    top = AboutTopInset,
                    bottom = 28.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item(key = "first-page") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 18.dp, bottom = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = appIcon,
                                contentDescription = "Aeryo Browser 应用图标",
                                modifier = Modifier.size(176.dp)
                            )
                            Text(
                                text = "Aeryo Browser",
                                color = colors.onBackground,
                                fontSize = MiuixTheme.textStyles.title2.fontSize,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "版本 $versionName ($versionCode)",
                                color = colors.onSurfaceVariantSummary,
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        SmallTitle(
                            text = "应用信息",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            colors = cardColors
                        ) {
                            BasicComponent(
                                title = "应用包名",
                                summary = packageName
                            )
                            BasicComponent(
                                title = "Android 版本",
                                summary = androidVersion
                            )
                            BasicComponent(
                                title = "WebView 版本",
                                summary = webViewVersion
                            )
                        }
                    }
                }

                item(key = "privacy") {
                    SmallTitle(text = "隐私", modifier = Modifier.fillMaxWidth())
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = cardColors
                    ) {
                        BasicComponent(
                            title = "本地数据",
                            summary = "书签、浏览历史与偏好设置保存在此设备上，可随时在设置中清除"
                        )
                    }
                }

                item(key = "developer") {
                    SmallTitle(text = "开发者", modifier = Modifier.fillMaxWidth())
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = cardColors
                    ) {
                        ArrowPreference(
                            title = "官方网站",
                            summary = "aeryo.zzbuaoye.net",
                            onClick = { uriHandler.openUri(ZZBUAOYE_OFFICIAL_WEBSITE_URL) }
                        )
                        ArrowPreference(
                            title = "GitHub",
                            summary = "@buaoyezz",
                            onClick = { uriHandler.openUri(ZZBUAOYE_GITHUB_URL) }
                        )
                    }
                }

                item(key = "open-source") {
                    SmallTitle(text = "开源与许可", modifier = Modifier.fillMaxWidth())
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = cardColors
                    ) {
                        ArrowPreference(
                            title = "Miuix UI",
                            summary = "Apache-2.0 开源许可",
                            onClick = { uriHandler.openUri(MIUIX_SOURCE_URL) }
                        )
                        ArrowPreference(
                            title = "Miuix 组件文档",
                            summary = "查看组件库文档",
                            onClick = { uriHandler.openUri(MIUIX_DOCUMENTATION_URL) }
                        )
                    }
                }

                item(key = "footer") {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = "Copyright 2026 ZZBuAoYe",
                        color = colors.onSurfaceVariantSummary,
                        fontSize = MiuixTheme.textStyles.footnote2.fontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Powered by Miuix UI",
                        color = colors.onSurfaceVariantSummary,
                        fontSize = MiuixTheme.textStyles.footnote2.fontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 20.dp)
                    )
                }
        }

        AboutTopBar(onBack = onBack)
    }
}

@Composable
private fun AboutFlowingBackground(
    colors: top.yukonga.miuix.kmp.theme.Colors,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "about-flow")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "about-flow-phase"
    )
    val shader = remember(
        colors.background,
        colors.primaryContainer,
        colors.secondaryContainer,
        colors.tertiaryContainer
    ) {
        if (isRuntimeShaderSupported()) {
            runCatching { RuntimeShader(ABOUT_FLOW_SHADER) }.getOrNull()
        } else {
            null
        }
    }
    val shaderBrush = remember(shader) { shader?.asBrush() }

    Box(
        modifier = modifier.background(colors.background)
    ) {
        if (shader != null && shaderBrush != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        shader.setFloatUniform("uResolution", size.width, size.height)
                        shader.setFloatUniform("uTime", phase)
                        shader.setColorUniform("uBackground", colors.background)
                        shader.setColorUniform("uPrimary", colors.primaryContainer)
                        shader.setColorUniform("uSecondary", colors.secondaryContainer)
                        shader.setColorUniform("uTertiary", colors.tertiaryContainer)
                        drawRect(shaderBrush)
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val shift = (phase * 2f - 0.5f) * size.width
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    colors.background,
                                    colors.primaryContainer.copy(alpha = 0.70f),
                                    colors.secondaryContainer.copy(alpha = 0.78f),
                                    colors.tertiaryContainer.copy(alpha = 0.70f),
                                    colors.background
                                ),
                                start = Offset(shift, 0f),
                                end = Offset(shift + size.width, size.height)
                            )
                        )
                    }
            )
        }
        content()
    }
}

@Composable
private fun AboutTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(AboutTopBarHeight)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(MiuixIcons.Back, contentDescription = "返回")
        }
        Text(
            text = "关于",
            color = MiuixTheme.colorScheme.onBackground,
            fontSize = MiuixTheme.textStyles.title3.fontSize,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
