package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.zzbuaoye.aeryo.settings.data.AdBlockSource
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdBlockSettingsScreen(
    currentAdBlockEnabled: Boolean,
    sources: List<AdBlockSource>,
    onAdBlockToggled: (Boolean) -> Unit,
    onSourcesUpdated: (List<AdBlockSource>) -> Unit,
    onRequestUpdateAll: () -> Unit,
    onRequestDownload: (AdBlockSource) -> Unit,
    blockedRequestCount: Long = 0L,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    val df = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "广告拦截",
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
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 20.dp
            )
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainerHighest)
                ) {
                    SwitchPreference(
                        title = "启用广告拦截",
                        summary = "强力过滤常见广告资源，支持规则订阅",
                        checked = currentAdBlockEnabled,
                        onCheckedChange = onAdBlockToggled
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer)
                ) {
                    BasicComponent(
                        title = "拦截统计",
                        summary = "本次运行已拦截 ${blockedRequestCount} 个请求 · 已启用 ${sources.count { it.isEnabled }} 个订阅源"
                    )
                }
            }

            item {
                SmallTitle(text = "订阅源")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainerHighest)
                ) {
                    sources.forEach { source ->
                        val updateTimeStr = if (source.lastUpdated > 0) df.format(Date(source.lastUpdated)) else "从未更新"
                        val summaryText = "更新时间: $updateTimeStr"

                        SwitchPreference(
                            title = source.name,
                            summary = summaryText,
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

            item {
                Box(modifier = Modifier.padding(12.dp)) {
                    TextButton(
                        text = if (isUpdating) "更新中..." else "立即更新所有启用的源",
                        onClick = {
                            if (isUpdating) return@TextButton
                            isUpdating = true
                            onRequestUpdateAll()
                            isUpdating = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showAddDialog) {
            AddAdBlockSourceDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, url ->
                    val newId = "custom_${System.currentTimeMillis()}"
                    val newSource = AdBlockSource(id = newId, name = name, url = url, isEnabled = true)
                    onSourcesUpdated(sources + newSource)
                    showAddDialog = false
                    onRequestDownload(newSource)
                }
            )
        }
    }
}
