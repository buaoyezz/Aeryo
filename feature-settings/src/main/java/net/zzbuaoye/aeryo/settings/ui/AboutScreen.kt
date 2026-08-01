package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val MIUIX_SOURCE_URL = "https://github.com/compose-miuix-ui/miuix"
private const val MIUIX_DOCUMENTATION_URL = "https://compose-miuix-ui.github.io/miuix/dokka/index.html"
private const val ZZBUAOYE_GITHUB_URL = "https://github.com/buaoyezz"
private const val ZZBUAOYE_OFFICIAL_WEBSITE_URL = "https://aeryo.zzbuaoye.net/"

private val HyperListShape = RoundedCornerShape(20.dp)

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

    Box(modifier = Modifier.fillMaxSize()) {
        HyperOsBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            HyperBackBar(onBack = onBack)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .widthIn(max = 680.dp),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item(key = "hero") {
                    HyperBrandHero(
                        appIcon = appIcon,
                        versionName = versionName,
                        versionCode = versionCode
                    )
                }

                item(key = "application-info") {
                    HyperListCard {
                        HyperInfoRow(title = "应用名称", value = "Aeryo Browser")
                        HyperInfoRow(title = "应用版本", value = versionName)
                        HyperInfoRow(title = "构建版本", value = versionCode.toString())
                        HyperInfoRow(title = "Android 版本", value = androidVersion)
                        HyperInfoRow(title = "WebView 版本", value = webViewVersion)
                    }
                }

                item(key = "package-and-privacy") {
                    HyperListCard {
                        HyperInfoRow(title = "应用包名", value = packageName)
                        HyperInfoRow(
                            title = "本地数据",
                            value = "仅保存在此设备"
                        )
                    }
                }

                item(key = "links") {
                    HyperListCard {
                        HyperLinkRow(
                            title = "官方网站",
                            value = "aeryo.zzbuaoye.net",
                            onClick = { uriHandler.openUri(ZZBUAOYE_OFFICIAL_WEBSITE_URL) }
                        )
                        HyperLinkRow(
                            title = "GitHub",
                            value = "@buaoyezz",
                            onClick = { uriHandler.openUri(ZZBUAOYE_GITHUB_URL) }
                        )
                        HyperLinkRow(
                            title = "Miuix UI",
                            value = "Apache-2.0",
                            onClick = { uriHandler.openUri(MIUIX_SOURCE_URL) }
                        )
                        HyperLinkRow(
                            title = "组件库文档",
                            value = "Miuix UI",
                            onClick = { uriHandler.openUri(MIUIX_DOCUMENTATION_URL) }
                        )
                    }
                }

                item(key = "footer") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aeryo Browser",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = MiuixTheme.textStyles.footnote2.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "轻快 · 私密 · 无负担",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.70f),
                            fontSize = MiuixTheme.textStyles.footnote2.fontSize,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HyperOsBackground() {
    val colors = MiuixTheme.colorScheme

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = colors.background)
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to colors.background,
                    0.18f to colors.primaryContainer.copy(alpha = 0.82f),
                    0.41f to colors.secondaryContainer.copy(alpha = 0.88f),
                    0.63f to colors.tertiaryContainer.copy(alpha = 0.72f),
                    0.84f to colors.background.copy(alpha = 0.92f),
                    1.00f to colors.background
                ),
                startY = 0f,
                endY = size.height
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.surface.copy(alpha = 0.58f),
                    colors.tertiaryContainer.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.76f, size.height * 0.08f),
                radius = size.width * 0.92f
            ),
            radius = size.width * 0.92f,
            center = Offset(size.width * 0.76f, size.height * 0.08f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.primary.copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.16f, size.height * 0.40f),
                radius = size.width * 0.76f
            ),
            radius = size.width * 0.76f,
            center = Offset(size.width * 0.16f, size.height * 0.40f)
        )
    }
}

@Composable
private fun HyperBackBar(onBack: () -> Unit) {
    val colors = MiuixTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(52.dp)
            .padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = MiuixIcons.Back,
                contentDescription = "返回",
                tint = colors.primary
            )
        }
    }
}

@Composable
private fun HyperBrandHero(
    appIcon: Painter,
    versionName: String,
    versionCode: Long
) {
    val colors = MiuixTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(276.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = appIcon,
            contentDescription = "Aeryo 应用标志",
            modifier = Modifier.size(192.dp)
        )
        Text(
            text = "Aeryo",
            color = colors.onBackground,
            style = MiuixTheme.textStyles.title2,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "$versionName  ·  $versionCode",
            color = colors.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
private fun HyperListCard(content: @Composable () -> Unit) {
    val colors = MiuixTheme.colorScheme
    val surfaceColor = colors.surface.copy(alpha = 0.86f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HyperListShape)
            .background(surfaceColor)
            .border(
                width = 0.75.dp,
                color = colors.onSurface.copy(alpha = 0.045f),
                shape = HyperListShape
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun HyperInfoRow(
    title: String,
    value: String
) {
    HyperBaseRow(
        title = title,
        value = value,
        showChevron = false,
        onClick = null
    )
}

@Composable
private fun HyperLinkRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    HyperBaseRow(
        title = title,
        value = value,
        showChevron = true,
        onClick = onClick
    )
}

@Composable
private fun HyperBaseRow(
    title: String,
    value: String,
    showChevron: Boolean,
    onClick: (() -> Unit)?
) {
    val colors = MiuixTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(start = 17.dp, end = 14.dp, top = 15.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = colors.onSurface,
            style = MiuixTheme.textStyles.body1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = colors.onSurfaceVariantSummary,
            style = MiuixTheme.textStyles.body2,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.28f)
        )
        if (showChevron) {
            Spacer(modifier = Modifier.size(7.dp))
            HyperChevron()
        }
    }
}

@Composable
private fun HyperChevron() {
    val chevronColor = MiuixTheme.colorScheme.onSurfaceVariantSummary

    Canvas(modifier = Modifier.size(width = 7.dp, height = 13.dp)) {
        val strokeWidth = 1.2.dp.toPx()
        drawLine(
            color = chevronColor,
            start = Offset(size.width * 0.22f, size.height * 0.16f),
            end = Offset(size.width * 0.76f, size.height * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = chevronColor,
            start = Offset(size.width * 0.76f, size.height * 0.50f),
            end = Offset(size.width * 0.22f, size.height * 0.84f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
