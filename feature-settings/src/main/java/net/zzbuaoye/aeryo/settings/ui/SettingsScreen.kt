package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import net.zzbuaoye.aeryo.settings.data.UserPreferences
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
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    currentSearchEngine: String,
    currentAdBlockEnabled: Boolean,
    currentAddressBarAnimationEnabled: Boolean,
    currentDownloadMode: String,
    currentPrivacyBiometricEnabled: Boolean = false,
    appVersion: String,
    onSearchEngineChanged: (String) -> Unit,
    onAdBlockSettingsClicked: () -> Unit,
    onAddressBarAnimationToggled: (Boolean) -> Unit,
    onDownloadModeChanged: (String) -> Unit,
    onPrivacyBiometricToggled: (Boolean) -> Unit = {},
    onClearData: () -> Unit,
    onOpenAbout: () -> Unit,
    onBack: () -> Unit
) {
    val engines = listOf(
        "Bing" to UserPreferences.ENGINE_BING,
        "Google" to UserPreferences.ENGINE_GOOGLE,
        "Baidu" to UserPreferences.ENGINE_BAIDU,
        "DuckDuckGo" to UserPreferences.ENGINE_DUCKDUCKGO,
        "Yahoo" to UserPreferences.ENGINE_YAHOO,
        "Yandex" to UserPreferences.ENGINE_YANDEX,
        "360" to UserPreferences.ENGINE_360,
        "Sogou" to UserPreferences.ENGINE_SOGOU
    )
    val selectedEngine = engines.indexOfFirst { it.second == currentSearchEngine }.coerceAtLeast(0)
    val downloadModes = listOf(
        "系统下载" to UserPreferences.DOWNLOAD_MODE_SYSTEM,
        "内置下载" to UserPreferences.DOWNLOAD_MODE_BUILT_IN
    )
    val selectedDownloadMode = downloadModes.indexOfFirst { it.second == currentDownloadMode }.coerceAtLeast(0)
    val topAppBarScrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "设置",
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
                )
            ) {
                item(key = "browser") {
                    SmallTitle(text = "浏览")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        OverlayDropdownPreference(
                            title = "默认搜索引擎",
                            summary = "用于地址栏和主页搜索",
                            items = engines.map { it.first },
                            selectedIndex = selectedEngine,
                            onSelectedIndexChange = { onSearchEngineChanged(engines[it].second) }
                        )
                        SwitchPreference(
                            title = "主页地址栏动画",
                            summary = "点击搜索框时移动到浏览器顶部",
                            checked = currentAddressBarAnimationEnabled,
                            onCheckedChange = onAddressBarAnimationToggled
                        )
                    }
                }

                item(key = "content") {
                    SmallTitle(text = "内容")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        ArrowPreference(
                            title = "广告拦截",
                            summary = if (currentAdBlockEnabled) "已开启" else "已关闭",
                            onClick = onAdBlockSettingsClicked
                        )
                    }
                }

                item(key = "downloads") {
                    SmallTitle(text = "下载")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        OverlayDropdownPreference(
                            title = "下载方式",
                            summary = if (currentDownloadMode == UserPreferences.DOWNLOAD_MODE_BUILT_IN) {
                                "内置下载支持暂停、继续和实时速度"
                            } else {
                                "交由 Android 系统下载服务处理"
                            },
                            items = downloadModes.map { it.first },
                            selectedIndex = selectedDownloadMode,
                            onSelectedIndexChange = { index ->
                                onDownloadModeChanged(downloadModes[index].second)
                            }
                        )
                    }
                }

                item(key = "privacy") {
                    SmallTitle(text = "隐私与数据")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        SwitchPreference(
                            title = "进入隐私模式需指纹验证",
                            summary = "切换至无痕模式或查看私密历史时验证设备安全身份",
                            checked = currentPrivacyBiometricEnabled,
                            onCheckedChange = onPrivacyBiometricToggled
                        )
                        ArrowPreference(
                            title = "清除浏览数据",
                            summary = "清除历史记录和网页缓存",
                            onClick = onClearData
                        )
                    }
                }

                item(key = "about") {
                    SmallTitle(text = "应用")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        ArrowPreference(
                            title = "关于 Aeryo Browser",
                            endActions = {
                                Text(
                                    text = appVersion,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                                    fontSize = MiuixTheme.textStyles.body2.fontSize
                                )
                            },
                            onClick = onOpenAbout
                        )
                    }
                }

                item(key = "bottom-space") {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
