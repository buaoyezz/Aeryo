package net.zzbuaoye.aeryo.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import top.yukonga.miuix.kmp.utils.pressable
import net.zzbuaoye.aeryo.settings.data.UserPreferences

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



data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val active: Boolean? = null
)

data class SearchEngineOption(val name: String, val url: String)

private val SearchEngineOptions = listOf(
    SearchEngineOption("Bing", UserPreferences.ENGINE_BING),
    SearchEngineOption("Google", UserPreferences.ENGINE_GOOGLE),
    SearchEngineOption("Baidu", UserPreferences.ENGINE_BAIDU),
    SearchEngineOption("DuckDuckGo", UserPreferences.ENGINE_DUCKDUCKGO),
    SearchEngineOption("360", UserPreferences.ENGINE_360),
    SearchEngineOption("Sogou", UserPreferences.ENGINE_SOGOU)
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
    currentSearchQuery: String,
    onSearchEngineSelected: (String) -> Unit,
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
) {
    var editing by remember(show) { mutableStateOf(false) }
    var selectedItemId by remember(show) { mutableStateOf<String?>(null) }
    var localOrder by remember(menuOrder, show) { mutableStateOf(menuOrder) }
    var localIsIncognito by remember(show) { mutableStateOf(isIncognito) }
    var localIsDesktopMode by remember(show) { mutableStateOf(isDesktopMode) }
    var localIsAdBlockEnabled by remember(show) { mutableStateOf(isAdBlockEnabled) }
    var localIsNightModeEnabled by remember(show) { mutableStateOf(isNightModeEnabled) }
    var showingSearchEnginePicker by remember(show) { mutableStateOf(false) }

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
        title = null,
        onDismissRequest = {
            if (showingSearchEnginePicker) {
                showingSearchEnginePicker = false
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
                currentSearchQuery = currentSearchQuery,
                onSearchEngineSelected = { engine ->
                    onSearchEngineSelected(engine)
                    showingSearchEnginePicker = false
                },
                onBack = { showingSearchEnginePicker = false }
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
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 12.dp, bottom = 4.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(MiuixTheme.colorScheme.primary)
                            .clickable { editItem.onClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "完成",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(132.dp)
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
    currentSearchQuery: String,
    onSearchEngineSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val selectedIndex = SearchEngineOptions
        .indexOfFirst { it.url == currentSearchEngine }
        .coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        OverlayDropdownPreference(
            title = "搜索引擎",
            summary = currentSearchQuery.ifBlank { "当前没有搜索内容" },
            items = SearchEngineOptions.map(SearchEngineOption::name),
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { index ->
                if (index != selectedIndex && currentSearchQuery.isNotBlank()) {
                    onSearchEngineSelected(SearchEngineOptions[index].url)
                }
            }
        )
        if (currentSearchQuery.isBlank()) {
            Text(
                text = "因为你在主页，或者当前页面没有进行任何搜索，所以无法更换搜索引擎。",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun MenuPage(
    items: List<MenuItem>,
    editing: Boolean,
    selectedItemId: String?,
    onItemClick: (MenuItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(2) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(MenuColumnCount) { columnIndex ->
                    val item = items.getOrNull(rowIndex * MenuColumnCount + columnIndex)
                    if (item == null) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(66.dp)
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
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (item.active == true || selected) {
        MiuixTheme.colorScheme.primary
    } else {
        MiuixTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .height(66.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) MiuixTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .pressable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            color = contentColor
        )
    }
}
