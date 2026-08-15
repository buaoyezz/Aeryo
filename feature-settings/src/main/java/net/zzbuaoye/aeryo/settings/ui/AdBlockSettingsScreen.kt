package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.settings.data.AdBlockSource
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Blocklist
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.input.nestedscroll.nestedScroll
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior

@Composable
fun AdBlockSettingsScreen(
    currentAdBlockEnabled: Boolean,
    sources: List<AdBlockSource>,
    currentAutoUpdateInterval: String = UserPreferences.AD_BLOCK_AUTO_UPDATE_3D,
    onAdBlockToggled: (Boolean) -> Unit,
    onSourcesUpdated: (List<AdBlockSource>) -> Unit,
    onAutoUpdateIntervalChanged: (String) -> Unit = {},
    onRequestUpdateAll: () -> Unit,
    onRequestDownload: (AdBlockSource) -> Unit,
    blockedRequestCount: Long = 0L,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    val df = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val enabledSourcesCount = remember(sources) { sources.count { it.isEnabled } }
    val scrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberMiuixWindowBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        MiuixWindowSurface()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MiuixBlurredBar(topBarBackdrop) {
                    TopAppBar(
                        title = "广告拦截",
                        color = if (topBarBackdrop != null) Color.Transparent else miuixWindowBackgroundColor(),
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(MiuixIcons.Back, contentDescription = "返回")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAddDialog = true }) {
                                Icon(MiuixIcons.Add, contentDescription = "添加源")
                            }
                        }
                    )
                }
            },
            containerColor = miuixWindowBackgroundColor()
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollEndHaptic()
                        .overScrollVertical()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding() + 20.dp
                    ),
                    overscrollEffect = null,
                ) {
            // 使用与 InstallerX Miuix 设置页一致的分组卡片，不在普通设置页自造仪表盘。
            item {
                SmallTitle(text = "拦截状态")
                Card(modifier = Modifier.fillMaxWidth()) {
                    BasicComponent(
                        title = if (currentAdBlockEnabled) "防护运行中" else "防护已停止",
                        summary = if (currentAdBlockEnabled) {
                            "正在使用 $enabledSourcesCount 个规则源过滤网页请求"
                        } else {
                            "启用后将按已选择的规则源过滤广告和追踪请求"
                        },
                        startAction = {
                            Icon(
                                imageVector = MiuixIcons.Blocklist,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp),
                                tint = if (currentAdBlockEnabled) {
                                    MiuixTheme.colorScheme.primary
                                } else {
                                    MiuixTheme.colorScheme.onSurfaceVariantSummary
                                },
                            )
                        },
                        endActions = {
                            Text(
                                text = String.format(Locale.getDefault(), "%,d", blockedRequestCount),
                                color = MiuixTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                    )
                }
            }

            // 2. 总控设置
            item {
                SmallTitle(text = "系统与开关")
                Card(modifier = Modifier.fillMaxWidth()) {
                    SwitchPreference(
                        title = "启用全局广告拦截",
                        summary = "强力过滤常见广告资源，支持规则在线订阅与自动更新",
                        checked = currentAdBlockEnabled,
                        onCheckedChange = onAdBlockToggled
                    )
                }
            }

            // 3. 规则全量维护与同步
            item {
                SmallTitle(text = "规则维护")
                Card(modifier = Modifier.fillMaxWidth()) {
                    val latestUpdated = sources.filter { it.lastUpdated > 0 }.maxOfOrNull { it.lastUpdated } ?: 0L
                    val lastSyncStr = if (latestUpdated > 0) df.format(Date(latestUpdated)) else "未同步"

                    BasicComponent(
                        title = "全量更新所有规则源",
                        summary = "上次同步时间: $lastSyncStr",
                        onClick = {
                            if (!isUpdating) {
                                isUpdating = true
                                onRequestUpdateAll()
                                isUpdating = false
                            }
                        },
                        endActions = {
                            TextButton(
                                text = if (isUpdating) "更新中..." else "立即更新",
                                onClick = {
                                    if (!isUpdating) {
                                        isUpdating = true
                                        onRequestUpdateAll()
                                        isUpdating = false
                                    }
                                }
                            )
                        }
                    )

                    val autoUpdateOptions = listOf(
                        "每 12 小时" to UserPreferences.AD_BLOCK_AUTO_UPDATE_12H,
                        "每 3 天" to UserPreferences.AD_BLOCK_AUTO_UPDATE_3D,
                        "每 7 天" to UserPreferences.AD_BLOCK_AUTO_UPDATE_7D,
                        "每 15 天" to UserPreferences.AD_BLOCK_AUTO_UPDATE_15D,
                        "每 30 天 (一个月)" to UserPreferences.AD_BLOCK_AUTO_UPDATE_30D,
                        "永不自动更新" to UserPreferences.AD_BLOCK_AUTO_UPDATE_NEVER
                    )
                    val selectedIndex = autoUpdateOptions.indexOfFirst { it.second == currentAutoUpdateInterval }.coerceAtLeast(0)

                    OverlayDropdownPreference(
                        title = "自动更新频率",
                        items = autoUpdateOptions.map { it.first },
                        selectedIndex = selectedIndex,
                        onSelectedIndexChange = { index ->
                            onAutoUpdateIntervalChanged(autoUpdateOptions[index].second)
                        },
                        renderInRootScaffold = false
                    )
                }
            }

            // 4. 订阅源规则管理
            item {
                SmallTitle(text = "规则订阅源 (${sources.size})")
                Card(modifier = Modifier.fillMaxWidth()) {
                    sources.forEach { source ->
                        val updateTimeStr = if (source.lastUpdated > 0) df.format(Date(source.lastUpdated)) else "从未更新"
                        val isCustom = source.id.startsWith("custom_")

                        SwitchPreference(
                            title = source.name,
                            summary = "更新时间: $updateTimeStr" + if (isCustom) " (自定义)" else "",
                            checked = source.isEnabled,
                            onCheckedChange = { checked ->
                                val newSources = sources.map {
                                    if (it.id == source.id) it.copy(isEnabled = checked) else it
                                }
                                onSourcesUpdated(newSources)

                                if (checked && source.lastUpdated == 0L) {
                                    onRequestDownload(source)
                                }
                            }
                        )
                    }
                }
            }

            // 5. 添加自定义源
            item {
                SmallTitle(text = "拓展")
                Card(modifier = Modifier.fillMaxWidth()) {
                    BasicComponent(
                        title = "添加自定义订阅源",
                        summary = "输入第三方规则 URL 拓展拦截能力",
                        startAction = {
                            Icon(
                                imageVector = MiuixIcons.Add,
                                contentDescription = null,
                                tint = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        onClick = { showAddDialog = true }
                    )
                }
            }
        }

        AddAdBlockSourceDialog(
            show = showAddDialog,
            onDismiss = { showAddDialog = false },
            onAdd = { name, url ->
                val newId = "custom_${System.currentTimeMillis()}"
                val newSource = AdBlockSource(id = newId, name = name, url = url, isEnabled = true)
                onSourcesUpdated(sources + newSource)
                showAddDialog = false
                onRequestDownload(newSource)
            },
            renderInRootScaffold = false
        )
    }
}
}
}
