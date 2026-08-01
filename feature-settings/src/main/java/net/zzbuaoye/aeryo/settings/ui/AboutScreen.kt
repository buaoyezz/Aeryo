package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val MIUIX_SOURCE_URL = "https://github.com/compose-miuix-ui/miuix"
private const val MIUIX_DOCUMENTATION_URL = "https://compose-miuix-ui.github.io/miuix/dokka/index.html"
private const val ZZBUAOYE_GITHUB_URL = "https://github.com/buaoyezz"
private const val ZZBUAOYE_OFFICIAL_WEBSITE_URL = "https://aeryo.zzbuaoye.net/"

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
    val topAppBarScrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmallTopAppBar(
                title = "关于",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item(key = "identity") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 36.dp, bottom = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = appIcon,
                            contentDescription = "Aeryo Browser 应用图标",
                            modifier = Modifier.size(260.dp)
                        )
                        Text(
                            text = "Aeryo Browser",
                            color = MiuixTheme.colorScheme.onBackground,
                            fontSize = MiuixTheme.textStyles.title2.fontSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "版本 $versionName ($versionCode)",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = MiuixTheme.textStyles.body2.fontSize,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                item(key = "runtime-title") {
                    SmallTitle(text = "应用信息", modifier = Modifier.fillMaxWidth())
                }
                item(key = "runtime-card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
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

                item(key = "privacy-title") {
                    SmallTitle(text = "隐私", modifier = Modifier.fillMaxWidth())
                }
                item(key = "privacy-card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        BasicComponent(
                            title = "本地数据",
                            summary = "书签、浏览历史与偏好设置保存在此设备上，可随时在设置中清除"
                        )
                    }
                }

                item(key = "developer-title") {
                    SmallTitle(text = "开发者", modifier = Modifier.fillMaxWidth())
                }
                item(key = "developer-card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
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

                item(key = "open-source-title") {
                    SmallTitle(text = "开源与许可", modifier = Modifier.fillMaxWidth())
                }
                item(key = "open-source-card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
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
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = MiuixTheme.textStyles.footnote2.fontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Powered by Miuix UI",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = MiuixTheme.textStyles.footnote2.fontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 20.dp)
                    )
                }
            }
        }
    }
}
