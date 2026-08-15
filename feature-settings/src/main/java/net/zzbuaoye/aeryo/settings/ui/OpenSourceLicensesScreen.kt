package net.zzbuaoye.aeryo.settings.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.settings.ui.OpenSourceLicenses.OPEN_SOURCE_LIBRARIES
import net.zzbuaoye.aeryo.settings.ui.OpenSourceLicenses.OpenSourceLibrary
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * 独立的开放源代码许可页面组件 (规范 MIUIX TopAppBar + NestedScroll 设计)
 */
@Composable
fun OpenSourceLicensesScreen(
    onBack: () -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberMiuixWindowBackdrop()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiuixBlurredBar(topBarBackdrop) {
                TopAppBar(
                    title = "开放源代码许可",
                    color = if (topBarBackdrop != null) Color.Transparent else miuixWindowBackgroundColor(),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    }
                )
            }
        },
        containerColor = miuixWindowBackgroundColor()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier),
            contentAlignment = Alignment.TopCenter
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
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                ),
                overscrollEffect = null
            ) {
                item(key = "page-top-space") {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                settingsOpenSourceLicenses()
            }
        }
    }
}

/**
 * 开放源代码许可 LazyListScope 内容块
 */
fun LazyListScope.settingsOpenSourceLicenses() {
    items(
        items = OPEN_SOURCE_LIBRARIES,
        key = { it.name }
    ) { library ->
        OpenSourceLibraryCard(library = library)
    }

    item("licenses_bottom_spacer") {
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun OpenSourceLibraryCard(
    library: OpenSourceLibrary
) {
    var expanded by remember { mutableStateOf(false) }
    val licenseScrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            // Header Row: Library Name + License Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = library.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = library.licenseType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MiuixTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle Row: Author and Version
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = library.author,
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.primary
                )

                Text(
                    text = "v${library.version}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }

            // Expanded Area: Package info, Scrollable License Container & Actions
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Monospace Package Coordinates Tag
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = library.packageName,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MiuixTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Scrollable License Text Container (Max Height 220dp, prevents page inflation)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                            .verticalScroll(licenseScrollState)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = library.licenseText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Row: [Left: Collapse Button] <---> [Right: Copy License & Visit Link]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: 收回内容按钮
                        IconButton(
                            onClick = { expanded = false }
                        ) {
                            Icon(
                                imageVector = MiuixIcons.ExpandLess,
                                contentDescription = "收回",
                                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }

                        // Right: 复制协议 & 访问主页
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                text = "复制协议",
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(library.licenseText))
                                    Toast.makeText(context, "协议文本已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                text = "访问主页",
                                onClick = {
                                    uriHandler.openUri(library.url)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
