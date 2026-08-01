package net.zzbuaoye.aeryo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.zzbuaoye.aeryo.R
import net.zzbuaoye.aeryo.browser.model.WebTab
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType

@Composable
fun TabSwitcherScreen(
    tabs: List<WebTab>,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabClosed: (String) -> Unit,
    onNewTab: (isIncognito: Boolean) -> Unit,
    onCloseAllTabs: () -> Unit,
    onCloseIncognitoTabs: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val currentTab = tabs.getOrNull(activeTabIndex)
    var selectedSegment by remember { mutableIntStateOf(if (currentTab?.isIncognito == true) 1 else 0) }

    val filteredTabs = remember(tabs, selectedSegment) {
        tabs.filter { if (selectedSegment == 1) it.isIncognito else !it.isIncognito }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = "标签页",
                    largeTitle = "标签页",
                    subtitle = if (selectedSegment == 1) "${filteredTabs.size} 个无痕标签页" else "${filteredTabs.size} 个常规标签页",
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNewTab(selectedSegment == 1) }) {
                            Icon(MiuixIcons.Add, contentDescription = "新建标签页")
                        }
                        IconButton(onClick = {
                            if (selectedSegment == 1) onCloseIncognitoTabs() else onCloseAllTabs()
                        }) {
                            Icon(MiuixIcons.Delete, contentDescription = "关闭当前列表标签页")
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MiuixTheme.colorScheme.surfaceContainer)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedSegment == 0) MiuixTheme.colorScheme.primaryContainer else MiuixTheme.colorScheme.surfaceContainer
                            )
                            .clickable { selectedSegment = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "常规 (${tabs.count { !it.isIncognito }})",
                            color = if (selectedSegment == 0) MiuixTheme.colorScheme.onPrimaryContainer else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontWeight = if (selectedSegment == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedSegment == 1) MiuixTheme.colorScheme.primaryContainer else MiuixTheme.colorScheme.surfaceContainer
                            )
                            .clickable { selectedSegment = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Hide,
                                contentDescription = null,
                                tint = if (selectedSegment == 1) MiuixTheme.colorScheme.onPrimaryContainer else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "无痕 (${tabs.count { it.isIncognito }})",
                                color = if (selectedSegment == 1) MiuixTheme.colorScheme.onPrimaryContainer else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontWeight = if (selectedSegment == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = MiuixTheme.colorScheme.background
    ) { padding ->
        if (filteredTabs.isEmpty() && selectedSegment == 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MiuixTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Hide,
                            contentDescription = "无痕模式",
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "无痕浏览模式",
                        fontSize = MiuixTheme.textStyles.title3.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = MiuixTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "在无痕模式下，不会自动保存浏览历史记录、输入表单数据和 Cookies。保护私密浏览安全。",
                        fontSize = MiuixTheme.textStyles.body2.fontSize,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                    )
                    Button(
                        onClick = { onNewTab(true) }
                    ) {
                        Text("新建无痕标签页")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 14.dp,
                    top = padding.calculateTopPadding() + 4.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredTabs, key = { _, tab -> tab.id }) { _, tab ->
                    val globalIndex = tabs.indexOfFirst { it.id == tab.id }
                    TabCard(
                        tab = tab,
                        selected = globalIndex == activeTabIndex,
                        onClick = { onTabSelected(globalIndex) },
                        onClose = { onTabClosed(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: WebTab,
    selected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)
    val headerColor by animateColorAsState(
        targetValue = if (selected) {
            MiuixTheme.colorScheme.primaryContainer
        } else {
            MiuixTheme.colorScheme.surfaceContainer
        },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.38f),
        label = "tab-header-color"
    )
    val outlineColor by animateColorAsState(
        targetValue = if (selected) {
            MiuixTheme.colorScheme.primary
        } else {
            MiuixTheme.colorScheme.surfaceContainer
        },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.38f),
        label = "tab-outline-color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = outlineColor,
                shape = cardShape
            ),
        cornerRadius = 22.dp,
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tab.isIncognito) {
                    Icon(
                        imageVector = MiuixIcons.Hide,
                        contentDescription = "无痕标签",
                        tint = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                } else if (tab.favicon != null) {
                    Image(
                        bitmap = tab.favicon!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    Icon(
                        imageVector = if (tab.url == "about:blank") MiuixIcons.Home else MiuixIcons.Hide,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = tab.title,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Close,
                        contentDescription = "关闭标签页",
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .background(MiuixTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                if (tab.preview != null) {
                    Image(
                        bitmap = tab.preview!!.asImageBitmap(),
                        contentDescription = "标签页预览",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = if (tab.isIncognito) MiuixIcons.Hide else MiuixIcons.Home,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (tab.isIncognito) "无痕模式" else if (tab.url == "about:blank") "新标签页" else tab.url,
                            fontSize = 12.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
