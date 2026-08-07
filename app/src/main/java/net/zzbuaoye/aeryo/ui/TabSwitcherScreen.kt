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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed as lazyItemsIndexed
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import net.zzbuaoye.aeryo.core.ui.aeryoBackdropSource
import net.zzbuaoye.aeryo.core.ui.aeryoBlurEffect
import net.zzbuaoye.aeryo.core.ui.aeryoCardColor
import net.zzbuaoye.aeryo.core.ui.aeryoTopBarColor
import net.zzbuaoye.aeryo.core.ui.aeryoWindowColor
import net.zzbuaoye.aeryo.core.ui.rememberAeryoWindowBackdrop
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.ListView
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun TabSwitcherScreen(
    tabs: List<WebTab>,
    activeTabIndex: Int,
    tabViewMode: String = UserPreferences.TAB_VIEW_MODE_GRID,
    onTabViewModeChanged: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit,
    onTabClosed: (String) -> Unit,
    onNewTab: (isIncognito: Boolean) -> Unit,
    onCloseAllTabs: () -> Unit,
    onCloseIncognitoTabs: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val currentTab = tabs.getOrNull(activeTabIndex)
    val hasIncognitoTabs = remember(tabs) { tabs.any { it.isIncognito } }
    var selectedSegment by remember(hasIncognitoTabs, currentTab?.isIncognito) {
        mutableIntStateOf(if (hasIncognitoTabs && currentTab?.isIncognito == true) 1 else 0)
    }

    LaunchedEffect(hasIncognitoTabs) {
        if (!hasIncognitoTabs && selectedSegment == 1) {
            selectedSegment = 0
        }
    }

    val filteredTabs = remember(tabs, selectedSegment, hasIncognitoTabs) {
        if (!hasIncognitoTabs) {
            tabs.filter { !it.isIncognito }
        } else {
            tabs.filter { if (selectedSegment == 1) it.isIncognito else !it.isIncognito }
        }
    }
    val scrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberAeryoWindowBackdrop()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.aeryoBlurEffect(topBarBackdrop),
                color = topBarBackdrop.aeryoTopBarColor(),
                title = "标签页",
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val nextMode = if (tabViewMode == UserPreferences.TAB_VIEW_MODE_GRID) UserPreferences.TAB_VIEW_MODE_HALF else UserPreferences.TAB_VIEW_MODE_GRID
                        onTabViewModeChanged(nextMode)
                    }) {
                        Icon(
                            imageVector = if (tabViewMode == UserPreferences.TAB_VIEW_MODE_GRID) MiuixIcons.ListView else MiuixIcons.GridView,
                            contentDescription = "切换视图模式"
                        )
                    }
                    IconButton(onClick = { onNewTab(hasIncognitoTabs && selectedSegment == 1) }) {
                        Icon(MiuixIcons.Add, contentDescription = "新建标签页")
                    }
                    IconButton(onClick = {
                        if (hasIncognitoTabs && selectedSegment == 1) onCloseIncognitoTabs() else onCloseAllTabs()
                    }) {
                        Icon(MiuixIcons.Delete, contentDescription = "关闭当前列表标签页")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = aeryoWindowColor(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .aeryoBackdropSource(topBarBackdrop)
                .padding(top = padding.calculateTopPadding())
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            if (hasIncognitoTabs) {
                TabRowWithContour(
                    tabs = listOf(
                        "常规  ${tabs.count { !it.isIncognito }}",
                        "无痕  ${tabs.count { it.isIncognito }}",
                    ),
                    selectedTabIndex = selectedSegment,
                    onTabSelected = { selectedSegment = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }

            if (filteredTabs.isEmpty() && selectedSegment == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 42.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.11f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Hide,
                                contentDescription = "无痕模式",
                                tint = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.size(27.dp)
                            )
                        }
                        Text(
                            text = "无痕浏览模式",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MiuixTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "无痕标签页不会自动保留浏览记录、表单数据和 Cookie。",
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 7.dp, bottom = 20.dp)
                        )
                        Button(onClick = { onNewTab(true) }) {
                            Text("新建无痕标签页")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = if (hasIncognitoTabs) 0.dp else 12.dp,
                        bottom = padding.calculateBottomPadding() + 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollEndHaptic()
                        .overScrollVertical()
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
}

@Composable
fun TabSwitcherBottomSheetContent(
    tabs: List<WebTab>,
    activeTabIndex: Int,
    tabViewMode: String = UserPreferences.TAB_VIEW_MODE_HALF,
    onTabViewModeChanged: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit,
    onTabClosed: (String) -> Unit,
    onNewTab: (isIncognito: Boolean) -> Unit,
    onCloseAllTabs: () -> Unit,
    onCloseIncognitoTabs: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val currentTab = tabs.getOrNull(activeTabIndex)
    val hasIncognitoTabs = remember(tabs) { tabs.any { it.isIncognito } }
    var selectedSegment by remember(hasIncognitoTabs, currentTab?.isIncognito) {
        mutableIntStateOf(if (hasIncognitoTabs && currentTab?.isIncognito == true) 1 else 0)
    }

    LaunchedEffect(hasIncognitoTabs) {
        if (!hasIncognitoTabs && selectedSegment == 1) {
            selectedSegment = 0
        }
    }

    val filteredTabs = remember(tabs, selectedSegment, hasIncognitoTabs) {
        if (!hasIncognitoTabs) {
            tabs.filter { !it.isIncognito }
        } else {
            tabs.filter { if (selectedSegment == 1) it.isIncognito else !it.isIncognito }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "标签页",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.onSurface
                )
                Text(
                    text = if (hasIncognitoTabs && selectedSegment == 1) "${filteredTabs.size} 个无痕标签页" else "${filteredTabs.size} 个常规标签页",
                    fontSize = 11.5.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            IconButton(
                onClick = { onTabViewModeChanged(UserPreferences.TAB_VIEW_MODE_GRID) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.GridView,
                    contentDescription = "切换全屏卡片模式",
                    tint = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = {
                    if (hasIncognitoTabs && selectedSegment == 1) onCloseIncognitoTabs() else onCloseAllTabs()
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Delete,
                    contentDescription = "关闭当前列表标签",
                    tint = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Close,
                    contentDescription = "关闭标签页",
                    tint = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (hasIncognitoTabs) {
            TabRowWithContour(
                tabs = listOf(
                    "常规  ${tabs.count { !it.isIncognito }}",
                    "无痕  ${tabs.count { it.isIncognito }}",
                ),
                selectedTabIndex = selectedSegment,
                onTabSelected = { selectedSegment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val createIncognito = hasIncognitoTabs && selectedSegment == 1
            Button(
                onClick = { onNewTab(createIncognito) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (createIncognito) MiuixIcons.Hide else MiuixIcons.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (createIncognito) "新建无痕窗口" else "新建常规窗口",
                    fontSize = 13.5.sp
                )
            }
        }

        if (filteredTabs.isEmpty() && selectedSegment == 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无无痕标签页",
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 2.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .scrollEndHaptic()
                    .overScrollVertical()
            ) {
                lazyItemsIndexed(filteredTabs, key = { _, tab -> tab.id }) { _, tab ->
                    val globalIndex = tabs.indexOfFirst { it.id == tab.id }
                    HalfScreenTabRow(
                        tab = tab,
                        selected = globalIndex == activeTabIndex,
                        onClick = {
                            onTabSelected(globalIndex)
                        },
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
    val headerColor by animateColorAsState(
        targetValue = if (selected) {
            MiuixTheme.colorScheme.primaryContainer
        } else {
            aeryoCardColor()
        },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.38f),
        label = "tab-header-color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth(),
        cornerRadius = 20.dp,
        insideMargin = PaddingValues(0.dp),
        colors = CardDefaults.defaultColors(color = aeryoCardColor()),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = onClick,
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

@Composable
private fun HalfScreenTabRow(
    tab: WebTab,
    selected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MiuixTheme.colorScheme.primaryContainer.copy(alpha = 0.62f)
        } else {
            aeryoCardColor()
        },
        animationSpec = folmeSpring(damping = 0.9f, response = 0.38f),
        label = "half-tab-bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
        cornerRadius = 14.dp,
        colors = CardDefaults.defaultColors(color = backgroundColor),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) MiuixTheme.colorScheme.primary.copy(alpha = 0.16f)
                        else MiuixTheme.colorScheme.surfaceContainerHigh
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (tab.isIncognito) {
                    Icon(
                        imageVector = MiuixIcons.Hide,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (tab.favicon != null) {
                    Image(
                        bitmap = tab.favicon!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (tab.url == "about:blank") MiuixIcons.Home else MiuixIcons.Hide,
                        contentDescription = null,
                        tint = if (selected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tab.title,
                    fontSize = 13.5.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val hostStr = remember(tab.url) {
                    try {
                        val uri = android.net.Uri.parse(tab.url)
                        uri.host ?: if (tab.url == "about:blank") "新标签页" else tab.url
                    } catch (e: Exception) {
                        tab.url
                    }
                }
                Text(
                    text = if (tab.isIncognito) "无痕标签页" else hostStr,
                    fontSize = 11.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (selected) {
                Text(
                    text = "当前",
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp),
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Close,
                    contentDescription = "关闭标签页",
                    tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
