package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun UISettingsScreen(
    currentWebAutoDarkModeSwitch: Boolean,
    onWebAutoDarkModeToggled: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberMiuixWindowBackdrop()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiuixBlurredBar(topBarBackdrop) {
                TopAppBar(
                    title = "界面设置",
                    color = if (topBarBackdrop != null) Color.Transparent else miuixWindowBackgroundColor(),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                )
            }
        },
        containerColor = miuixWindowBackgroundColor(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = paddingValues.calculateTopPadding(),
                    end = 12.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                ),
                overscrollEffect = null,
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }
                item { SmallTitle(text = "网页") }
                item {
                    Card(modifier = Modifier.padding(bottom = 12.dp)) {
                        SwitchPreference(
                            title = "网页自动深色模式",
                            summary = "启用后，支持自动深色模式的网页将自动使用深色主题",
                            checked = currentWebAutoDarkModeSwitch,
                            onCheckedChange = onWebAutoDarkModeToggled,
                        )
                    }
                }
            }
        }
    }
}
