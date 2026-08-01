package net.zzbuaoye.aeryo.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
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

private val HistoryOutlineIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "HistoryOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3.05f, 13.0f)
            curveTo(3.05f, 17.971f, 7.08f, 22.0f, 12.05f, 22.0f)
            curveTo(17.02f, 22.0f, 21.05f, 17.971f, 21.05f, 13.0f)
            curveTo(21.05f, 8.0294f, 17.02f, 4.0f, 12.05f, 4.0f)
            curveTo(9.5165f, 4.0f, 7.2274f, 5.0468f, 5.6022f, 6.7214f)
        }
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3.05f, 4.0f)
            verticalLineTo(7.1f)
            horizontalLineTo(6.15f)
        }
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12.05f, 8.5f)
            verticalLineTo(13.0f)
            lineTo(15.05f, 16.0f)
        }
    }.build()
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
            val defaultItems = listOf(
                MenuItem("new_tab", "新建标签", MiuixIcons.Add, onNewTab),
                MenuItem("bookmarks", "书签", MiuixIcons.Favorites, onNavigateToBookmarks),
                MenuItem("history", "历史记录", HistoryOutlineIcon, onNavigateToHistory),
                MenuItem("downloads", "下载", MiuixIcons.Download, onNavigateToDownloads),
                MenuItem("save_bookmark", "添加书签", MiuixIcons.Favorites, {
                    onSaveBookmark()
                    onDismiss()
                }),
                MenuItem("share", "分享网页", MiuixIcons.Link, {
                    onSharePage()
                    onDismiss()
                }),
                MenuItem("refresh", "刷新", MiuixIcons.Refresh, onRefresh),
                MenuItem("find", "页内查找", MiuixIcons.Search, onFindInPage),
                MenuItem(
                    "re_search_engine",
                    "换引擎",
                    MiuixIcons.ChevronForward,
                    { showingSearchEnginePicker = true }
                ),
                MenuItem(
                    "incognito",
                    "无痕模式",
                    MiuixIcons.Hide,
                    {
                        localIsIncognito = !localIsIncognito
                        onIncognitoChanged(localIsIncognito)
                    },
                    localIsIncognito
                ),
                MenuItem(
                    "save_private_history",
                    "保存至私密历史",
                    MiuixIcons.Hide,
                    {
                        onSavePrivateHistory()
                        onDismiss()
                    }
                ),
                MenuItem(
                    "desktop",
                    "桌面版",
                    MiuixIcons.ScreenMirroring,
                    {
                        localIsDesktopMode = !localIsDesktopMode
                        onDesktopModeChanged(localIsDesktopMode)
                    },
                    localIsDesktopMode
                ),
                MenuItem(
                    "ad_block",
                    "广告拦截",
                    MiuixIcons.Blocklist,
                    {
                        localIsAdBlockEnabled = !localIsAdBlockEnabled
                        onAdBlockChanged(localIsAdBlockEnabled)
                    },
                    localIsAdBlockEnabled
                ),
                MenuItem(
                    "night_mode",
                    "夜间模式",
                    MiuixIcons.Theme,
                    {
                        localIsNightModeEnabled = !localIsNightModeEnabled
                        onNightModeChanged(localIsNightModeEnabled)
                    },
                    localIsNightModeEnabled
                ),
                MenuItem("settings", "设置", MiuixIcons.Settings, onNavigateToSettings),
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
                    } else if (indexOf("re_search_engine") >= MenuPageSize) {
                        remove("re_search_engine")
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
                            text = "长按卡片拖动；编号为最终的 页-排-位置（如 1-2-3）",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(MenuColumnCount),
                        state = lazyGridState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(310.dp)
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(orderedItems, key = { _, item -> item.id }) { index, item ->
                            ReorderableItem(reorderableState, key = item.id) { isDragging ->
                                val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)
                                val scale by animateFloatAsState(if (isDragging) 1.06f else 1f)
                                val slot = menuSlot(index)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .graphicsLayer {
                                            shadowElevation = elevation.toPx()
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isDragging) {
                                                MiuixTheme.colorScheme.primaryContainer
                                            } else {
                                                MiuixTheme.colorScheme.surfaceContainerHigh
                                            }
                                        )
                                        .longPressDraggableHandle(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(7.dp)
                                    ) {
                                        Text(
                                            text = slot,
                                            color = if (isDragging) {
                                                MiuixTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MiuixTheme.colorScheme.primary
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "第 ${(index % MenuPageSize) / MenuColumnCount + 1} 排",
                                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(top = 1.dp)
                                        )
                                    }
                                    Column(
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
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "长按拖动到此位置",
                                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                            fontSize = 8.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    TextButton(
                        text = "完成",
                        onClick = editItem.onClick,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp, bottom = 4.dp)
                    )
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
