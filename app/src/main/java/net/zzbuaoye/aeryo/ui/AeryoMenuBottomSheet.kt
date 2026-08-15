package net.zzbuaoye.aeryo.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons

import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Bookmark
import compose.icons.tablericons.History
import compose.icons.tablericons.Download
import compose.icons.tablericons.Share
import compose.icons.tablericons.Refresh
import compose.icons.tablericons.Search
import compose.icons.tablericons.World
import compose.icons.tablericons.EyeOff
import compose.icons.tablericons.Lock
import compose.icons.tablericons.DeviceDesktop
import compose.icons.tablericons.ShieldCheck
import compose.icons.tablericons.Moon
import compose.icons.tablericons.Settings
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Blocklist
import top.yukonga.miuix.kmp.icon.extended.ChevronForward
import top.yukonga.miuix.kmp.icon.extended.Download
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.icon.extended.Favorites
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Link
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.RotateLeft
import top.yukonga.miuix.kmp.icon.extended.ScreenMirroring
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.pressable
import compose.icons.tablericons.Check
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import net.zzbuaoye.aeryo.settings.data.SearchEngine

private const val MenuColumnCount = 4
private const val MenuPageSize = 8

/** 例如 1-2-3 表示第 1 页、第 2 排、第 3 个菜单项。 */
private fun menuSlot(index: Int): String {
    val page = index / MenuPageSize + 1
    val indexOnPage = index % MenuPageSize
    val row = indexOnPage / MenuColumnCount + 1
    val position = indexOnPage % MenuColumnCount + 1
    return "$page-$row-$position"
}

data class MenuActionItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val active: Boolean? = null,
    val badgeDot: Boolean = false,
    val badgeCount: Int = 0
)

data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val active: Boolean? = null,
    val badgeDot: Boolean = false,
    val badgeCount: Int = 0
)

@Composable
fun AeryoMenuBottomSheet(
    show: Boolean,
    isIncognito: Boolean,
    isDesktopMode: Boolean,
    isAdBlockEnabled: Boolean,
    isNightModeEnabled: Boolean,
    onIncognitoChanged: (Boolean) -> Unit,
    onDesktopModeChanged: (Boolean) -> Unit,
    onAdBlockChanged: (Boolean) -> Unit,
    onNightModeChanged: (Boolean) -> Unit,
    currentSearchEngine: String,
    allSearchEngines: List<SearchEngine> = SearchEngine.PRESET_ENGINES,
    currentSearchQuery: String,
    onSearchEngineSelected: (String) -> Unit,
    currentVideoSpeed: Float = 1.0f,
    onVideoSpeedChange: (Float) -> Unit = {},
    sniffedMediaCount: Int = 0,
    mediaSnifferBadgeMode: String = UserPreferences.MEDIA_SNIFFER_BADGE_DOT,
    onShowMediaSniffer: () -> Unit = {},
    menuOrder: List<String>,
    onMenuOrderChanged: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    onNewTab: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSavePrivateHistory: () -> Unit = {},
    onSaveBookmark: () -> Unit = {},
    onSharePage: () -> Unit = {},
    onRefresh: () -> Unit,
    onFindInPage: () -> Unit,
    onEnterReaderMode: () -> Unit = {},
) {
    var editing by remember(show) { mutableStateOf(false) }
    var selectedItemId by remember(show) { mutableStateOf<String?>(null) }
    var localOrder by remember(menuOrder, show) { mutableStateOf(menuOrder) }
    var localIsIncognito by remember(show) { mutableStateOf(isIncognito) }
    var localIsDesktopMode by remember(show) { mutableStateOf(isDesktopMode) }
    var localIsAdBlockEnabled by remember(show) { mutableStateOf(isAdBlockEnabled) }
    var localIsNightModeEnabled by remember(show) { mutableStateOf(isNightModeEnabled) }
    var showingSearchEnginePicker by remember(show) { mutableStateOf(false) }
    var showingVideoSpeedPicker by remember(show) { mutableStateOf(false) }

    LaunchedEffect(menuOrder, editing) {
        if (!editing) {
            localOrder = menuOrder
        }
    }

    LaunchedEffect(isIncognito) {
        localIsIncognito = isIncognito
    }
    LaunchedEffect(isDesktopMode) {
        localIsDesktopMode = isDesktopMode
    }
    LaunchedEffect(isAdBlockEnabled) {
        localIsAdBlockEnabled = isAdBlockEnabled
    }
    LaunchedEffect(isNightModeEnabled) {
        localIsNightModeEnabled = isNightModeEnabled
    }

    OverlayBottomSheet(
        show = show,
        title = when {
            showingSearchEnginePicker -> "更换搜索引擎"
            showingVideoSpeedPicker -> "网页视频倍速"
            editing -> "调整菜单"
            else -> "浏览器菜单"
        },
        onDismissRequest = {
            if (showingSearchEnginePicker) {
                showingSearchEnginePicker = false
            } else if (showingVideoSpeedPicker) {
                showingVideoSpeedPicker = false
            } else if (editing) {
                onMenuOrderChanged(localOrder)
                onDismiss()
            } else {
                onDismiss()
            }
        }
    ) {
        if (showingSearchEnginePicker) {
            SearchEnginePicker(
                currentSearchEngine = currentSearchEngine,
                allSearchEngines = allSearchEngines,
                currentSearchQuery = currentSearchQuery,
                onSearchEngineSelected = { engine ->
                    onSearchEngineSelected(engine)
                    showingSearchEnginePicker = false
                },
                onBack = { showingSearchEnginePicker = false }
            )
        } else if (showingVideoSpeedPicker) {
            VideoSpeedPicker(
                currentSpeed = currentVideoSpeed,
                onSpeedSelected = { speed ->
                    onVideoSpeedChange(speed)
                    showingVideoSpeedPicker = false
                    onDismiss()
                },
                onBack = { showingVideoSpeedPicker = false }
            )
        } else {
            val defaultItems = listOfNotNull(
                MenuItem("new_tab", "新建标签", TablerIcons.Plus, onNewTab),
                MenuItem("bookmarks", "书签", TablerIcons.Bookmark, onNavigateToBookmarks),
                MenuItem("history", "历史记录", TablerIcons.History, onNavigateToHistory),
                MenuItem("downloads", "下载", TablerIcons.Download, onNavigateToDownloads),
                MenuItem("save_bookmark", "添加书签", TablerIcons.Bookmark, {
                    onSaveBookmark()
                    onDismiss()
                }),
                MenuItem("share", "分享网页", TablerIcons.Share, {
                    onSharePage()
                    onDismiss()
                }),
                MenuItem("refresh", "刷新", TablerIcons.Refresh, onRefresh),
                MenuItem("find", "页内查找", TablerIcons.Search, onFindInPage),
                MenuItem(
                    "re_search_engine",
                    "换引擎",
                    TablerIcons.World,
                    { showingSearchEnginePicker = true }
                ),
                MenuItem(
                    "incognito",
                    "无痕模式",
                    TablerIcons.EyeOff,
                    {
                        localIsIncognito = !localIsIncognito
                        onIncognitoChanged(localIsIncognito)
                    },
                    localIsIncognito
                ),
                if (localIsIncognito) MenuItem(
                    "save_private_history",
                    "保存至私密历史",
                    TablerIcons.Lock,
                    {
                        onSavePrivateHistory()
                        onDismiss()
                    }
                ) else null,
                MenuItem(
                    "desktop",
                    "桌面版",
                    TablerIcons.DeviceDesktop,
                    {
                        localIsDesktopMode = !localIsDesktopMode
                        onDesktopModeChanged(localIsDesktopMode)
                    },
                    localIsDesktopMode
                ),
                MenuItem(
                    "ad_block",
                    "广告拦截",
                    TablerIcons.ShieldCheck,
                    {
                        localIsAdBlockEnabled = !localIsAdBlockEnabled
                        onAdBlockChanged(localIsAdBlockEnabled)
                    },
                    localIsAdBlockEnabled
                ),
                MenuItem(
                    "night_mode",
                    "夜间模式",
                    TablerIcons.Moon,
                    {
                        localIsNightModeEnabled = !localIsNightModeEnabled
                        onNightModeChanged(localIsNightModeEnabled)
                    },
                    localIsNightModeEnabled
                ),
                MenuItem(
                    "video_speed",
                    "视频倍速",
                    MiuixIcons.ScreenMirroring,
                    { showingVideoSpeedPicker = true }
                ),
                MenuItem(
                    id = "media_sniffer",
                    title = "媒体嗅探",
                    icon = TablerIcons.Download,
                    onClick = {
                        onShowMediaSniffer()
                        onDismiss()
                    },
                    badgeDot = sniffedMediaCount > 0 && mediaSnifferBadgeMode == UserPreferences.MEDIA_SNIFFER_BADGE_DOT,
                    badgeCount = if (mediaSnifferBadgeMode == UserPreferences.MEDIA_SNIFFER_BADGE_COUNT) sniffedMediaCount else 0
                ),
                MenuItem(
                    "reader_mode",
                    "阅读模式",
                    TablerIcons.Bookmark,
                    {
                        onEnterReaderMode()
                        onDismiss()
                    }
                ),
                MenuItem("settings", "设置", TablerIcons.Settings, onNavigateToSettings),
            )

            val itemById = defaultItems.associateBy(MenuItem::id)

            val validLocalOrder = localOrder
                .filter(itemById::containsKey)
                .distinct()
            val seededOrder = if (validLocalOrder.isEmpty()) {
                defaultItems.map(MenuItem::id)
            } else {
                validLocalOrder.toMutableList().apply {
                    if ("history" !in this) {
                        val bookmarkIndex = indexOf("bookmarks")
                        add(if (bookmarkIndex >= 0) bookmarkIndex + 1 else size, "history")
                    }
                    if ("re_search_engine" !in this) {
                        val findIndex = indexOf("find")
                        add(if (findIndex >= 0) findIndex + 1 else 0, "re_search_engine")
                    }
                }
            }
            val normalizedOrder = seededOrder + defaultItems.map(MenuItem::id).filterNot(seededOrder::contains)
            val orderedItems = normalizedOrder.mapNotNull(itemById::get)
            LaunchedEffect(editing, normalizedOrder) {
                if (editing) {
                    localOrder = normalizedOrder
                }
            }
            val editItem = MenuItem(
                id = "edit_order",
                title = if (editing) "完成" else "调整顺序",
                icon = MiuixIcons.Edit,
                onClick = {
                    if (editing) {
                        onMenuOrderChanged(normalizedOrder)
                    }
                    editing = !editing
                    selectedItemId = null
                },
                active = editing
            )
            val pages = (orderedItems + editItem).chunked(MenuPageSize)
            val pagerState = rememberPagerState(pageCount = { pages.size })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                if (editing) {
                    val lazyGridState = rememberLazyGridState()
                    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
                        val visibleOrder = orderedItems.map(MenuItem::id).toMutableList()
                        if (from.index !in visibleOrder.indices || visibleOrder.isEmpty()) return@rememberReorderableLazyGridState
                        val targetIndex = to.index.coerceIn(0, visibleOrder.lastIndex)
                        if (from.index == targetIndex) return@rememberReorderableLazyGridState
                        val fromItem = visibleOrder.removeAt(from.index)
                        visibleOrder.add(targetIndex, fromItem)
                        localOrder = visibleOrder
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "调整菜单顺序",
                            color = MiuixTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "长按并拖动图标调整位置，按 4×2 网格分页排列",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 12.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Edit,
                                contentDescription = null,
                                tint = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "长按卡片即可自由拖拽重排",
                                fontSize = 11.sp,
                                color = MiuixTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    val rowCount = (orderedItems.size + MenuColumnCount - 1) / MenuColumnCount
                    val gridHeight = (rowCount * 96).dp.coerceAtMost(380.dp)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(MenuColumnCount),
                        state = lazyGridState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(gridHeight)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(orderedItems, key = { _, item -> item.id }) { index, item ->
                            ReorderableItem(reorderableState, key = item.id) { isDragging ->
                                val elevation by animateDpAsState(if (isDragging) 10.dp else 0.dp)
                                val scale by animateFloatAsState(if (isDragging) 1.06f else 1f)
                                val page = index / MenuPageSize + 1
                                val indexOnPage = index % MenuPageSize
                                val row = indexOnPage / MenuColumnCount + 1
                                val position = indexOnPage % MenuColumnCount + 1
                                val slot = "$page-$row-$position"
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(88.dp)
                                        .graphicsLayer {
                                            shadowElevation = elevation.toPx()
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isDragging) {
                                                MiuixTheme.colorScheme.primaryContainer
                                            } else {
                                                MiuixTheme.colorScheme.surfaceContainerHigh
                                            }
                                        )
                                        .border(
                                            BorderStroke(
                                                width = if (isDragging) 1.5.dp else 1.dp,
                                                color = if (isDragging) {
                                                    MiuixTheme.colorScheme.primary
                                                } else {
                                                    MiuixTheme.colorScheme.outline.copy(alpha = 0.12f)
                                                }
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .longPressDraggableHandle(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.TopCenter)
                                            .padding(horizontal = 6.dp, vertical = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (isDragging) {
                                                        MiuixTheme.colorScheme.primary.copy(alpha = 0.25f)
                                                    } else if (page == 1) {
                                                        MiuixTheme.colorScheme.primary.copy(alpha = 0.12f)
                                                    } else {
                                                        MiuixTheme.colorScheme.secondary.copy(alpha = 0.12f)
                                                    }
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = slot,
                                                color = if (isDragging) {
                                                    MiuixTheme.colorScheme.onPrimaryContainer
                                                } else if (page == 1) {
                                                    MiuixTheme.colorScheme.primary
                                                } else {
                                                    MiuixTheme.colorScheme.secondary
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = "⋮⋮",
                                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.35f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(top = 14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = "拖动 ${item.title} 到 $slot",
                                            modifier = Modifier.size(24.dp),
                                            tint = if (isDragging) {
                                                MiuixTheme.colorScheme.primary
                                            } else {
                                                MiuixTheme.colorScheme.onSurface
                                            }
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = item.title,
                                            color = if (isDragging) {
                                                MiuixTheme.colorScheme.primary
                                            } else {
                                                MiuixTheme.colorScheme.onSurface
                                            },
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Button(
                        onClick = { editItem.onClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 12.dp, bottom = 4.dp),
                    ) {
                        Text(text = "完成")
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(148.dp)
                    ) { page ->
                        MenuPage(
                            items = pages[page],
                            editing = false,
                            selectedItemId = null,
                            onItemClick = { item ->
                                item.onClick()
                            }
                        )
                    }
                    if (pages.size > 1) {
                        PageIndicator(
                            pageCount = pages.size,
                            selectedPage = pagerState.currentPage,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEnginePicker(
    currentSearchEngine: String,
    allSearchEngines: List<SearchEngine> = SearchEngine.PRESET_ENGINES,
    currentSearchQuery: String,
    onSearchEngineSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val selectedEngine = allSearchEngines.find {
        it.searchUrl.equals(currentSearchEngine, ignoreCase = true) ||
            it.searchUrl.replace("%s", "").equals(currentSearchEngine.replace("%s", ""), ignoreCase = true)
    } ?: allSearchEngines.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (currentSearchQuery.isNotBlank()) {
            Text(
                text = "搜索关键词: $currentSearchQuery",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                allSearchEngines.forEach { engine ->
                    val isSelected = engine.id == selectedEngine?.id ||
                        engine.searchUrl.equals(currentSearchEngine, ignoreCase = true)
                    BasicComponent(
                        title = engine.name,
                        summary = if (engine.isPreset) "内置引擎" else "自定义引擎",
                        onClick = {
                            onSearchEngineSelected(engine.searchUrl)
                        },
                        endActions = {
                            if (isSelected) {
                                Icon(
                                    imageVector = TablerIcons.Check,
                                    contentDescription = "已选择",
                                    tint = MiuixTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoSpeedPicker(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onBack: () -> Unit
) {
    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
    val speedLabels = speedOptions.map { "${it}x" }
    val selectedIndex = speedOptions.indexOfFirst { kotlin.math.abs(it - currentSpeed) < 0.01f }.coerceAtLeast(2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
    ) {
        OverlayDropdownPreference(
            title = "播放速度",
            summary = "调整当前网页所有播放中的视频速度",
            items = speedLabels,
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { index ->
                onSpeedSelected(speedOptions[index])
            }
        )
    }
}

@Composable
private fun MenuPage(
    items: List<MenuItem>,
    editing: Boolean,
    selectedItemId: String?,
    onItemClick: (MenuItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(2) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(MenuColumnCount) { columnIndex ->
                    val item = items.getOrNull(rowIndex * MenuColumnCount + columnIndex)
                    if (item == null) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                        )
                    } else {
                        MenuCommand(
                            item = item,
                            selected = editing && selectedItemId == item.id,
                            onClick = { onItemClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        repeat(pageCount) { index ->
            val color by animateColorAsState(
                if (index == selectedPage) MiuixTheme.colorScheme.onSurface
                else MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            val width by animateDpAsState(if (index == selectedPage) 12.dp else 5.dp)

            Box(
                modifier = Modifier
                    .height(5.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun MenuCommand(
    item: MenuItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (item.active == true || selected) {
        MiuixTheme.colorScheme.primary
    } else {
        MiuixTheme.colorScheme.onSurface
    }
    val backgroundColor = if (item.active == true || selected) {
        MiuixTheme.colorScheme.primaryContainer
    } else {
        MiuixTheme.colorScheme.surfaceContainerHigh
    }

    Card(
        modifier = modifier
            .height(70.dp),
        insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp),
        cornerRadius = 14.dp,
        colors = CardDefaults.defaultColors(color = backgroundColor),
        pressFeedbackType = PressFeedbackType.Sink,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(23.dp),
                    tint = contentColor
                )
                if (item.badgeDot) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 3.dp, y = (-2).dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MiuixTheme.colorScheme.primary)
                    )
                } else if (item.badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(MiuixTheme.colorScheme.primary)
                            .padding(horizontal = 4.dp, vertical = 0.5.dp)
                    ) {
                        Text(
                            text = if (item.badgeCount > 99) "99+" else "${item.badgeCount}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = item.title,
                fontSize = 11.sp,
                fontWeight = if (item.active == true) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}
