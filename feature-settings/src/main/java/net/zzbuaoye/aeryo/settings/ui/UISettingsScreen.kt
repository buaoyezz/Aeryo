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
fun UISettingsScreen(
    currentUIDarkModeSwitch: Boolean,
    onUIDarkModeToggled: (Boolean) -> Unit,
    currentWebAutoDarkModeSwitch: Boolean,
    onWebAutoDarkModeToggled: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "界面设置",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    SwitchPreference(
                        title = "深色模式",
                        summary = "启用后，应用界面将全局使用深色主题",
                        checked = currentUIDarkModeSwitch,
                        onCheckedChange = onUIDarkModeToggled
                    )
                }
                item {
                    SwitchPreference(
                        title = "网页自动深色模式",
                        summary = "启用后，支持自动深色模式的网页将自动使用深色主题",
                        checked = currentWebAutoDarkModeSwitch,
                        onCheckedChange = onWebAutoDarkModeToggled
                    )
                }
            }
        }
    )
}

