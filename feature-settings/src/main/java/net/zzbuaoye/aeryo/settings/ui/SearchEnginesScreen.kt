package net.zzbuaoye.aeryo.settings.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.Check
import net.zzbuaoye.aeryo.settings.data.SearchEngine
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun SearchEnginesScreen(
    currentSearchEngine: SearchEngine,
    allSearchEngines: List<SearchEngine>,
    onSearchEngineSelected: (SearchEngine) -> Unit,
    onAddCustomEngine: (name: String, searchUrl: String, suggestionUrl: String) -> Unit,
    onUpdateCustomEngine: (SearchEngine) -> Unit,
    onDeleteCustomEngine: (String) -> Unit,
    onBack: () -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior()
    val topBarBackdrop = rememberMiuixWindowBackdrop()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var editingEngine by remember { mutableStateOf<SearchEngine?>(null) }
    var engineToDelete by remember { mutableStateOf<SearchEngine?>(null) }

    val presets = remember(allSearchEngines) { allSearchEngines.filter { it.isPreset } }
    val customs = remember(allSearchEngines) { allSearchEngines.filterNot { it.isPreset } }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiuixBlurredBar(topBarBackdrop) {
                TopAppBar(
                    title = "搜索引擎",
                    color = if (topBarBackdrop != null) Color.Transparent else miuixWindowBackgroundColor(),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            editingEngine = null
                            showDialog = true
                        }) {
                            Icon(MiuixIcons.Add, contentDescription = "添加自定义搜索引擎")
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
                    bottom = paddingValues.calculateBottomPadding() + 24.dp
                ),
                overscrollEffect = null
            ) {
                item(key = "page-top-space") {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item(key = "preset_header") {
                    SmallTitle(text = "内置搜索引擎 (${presets.size})")
                }

                item(key = "preset_card") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            presets.forEach { engine ->
                                val isSelected = currentSearchEngine.id == engine.id ||
                                    currentSearchEngine.searchUrl.equals(engine.searchUrl, ignoreCase = true)

                                BasicComponent(
                                    title = engine.name,
                                    summary = engine.searchUrl,
                                    onClick = { onSearchEngineSelected(engine) },
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

                item(key = "custom_header") {
                    SmallTitle(text = "自定义搜索引擎 (${customs.size})")
                }

                item(key = "custom_card") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (customs.isEmpty()) {
                                BasicComponent(
                                    title = "添加自定义搜索引擎",
                                    summary = "支持自定义搜索 URL 及联想词 API 接口",
                                    onClick = {
                                        editingEngine = null
                                        showDialog = true
                                    },
                                    endActions = {
                                        Icon(
                                            imageVector = MiuixIcons.Add,
                                            contentDescription = "添加",
                                            tint = MiuixTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                )
                            } else {
                                customs.forEach { engine ->
                                    val isSelected = currentSearchEngine.id == engine.id ||
                                        currentSearchEngine.searchUrl.equals(engine.searchUrl, ignoreCase = true)

                                    BasicComponent(
                                        title = engine.name,
                                        summary = if (engine.suggestionUrl.isNotBlank()) {
                                            "${engine.searchUrl}\n联想: ${engine.suggestionUrl}"
                                        } else {
                                            engine.searchUrl
                                        },
                                        onClick = { onSearchEngineSelected(engine) },
                                        endActions = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = {
                                                        editingEngine = engine
                                                        showDialog = true
                                                    },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = MiuixIcons.Edit,
                                                        contentDescription = "编辑",
                                                        tint = MiuixTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { engineToDelete = engine },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = MiuixIcons.Delete,
                                                        contentDescription = "删除",
                                                        tint = MiuixTheme.colorScheme.error ?: Color(0xFFE53935),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                if (isSelected) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = TablerIcons.Check,
                                                        contentDescription = "已选择",
                                                        tint = MiuixTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item(key = "help_header") {
                    SmallTitle(text = "参数与接口说明")
                }

                item(key = "help_card") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            BasicComponent(
                                title = "搜索 URL 占位符",
                                summary = "在搜索网址中使用 %s 代表关键词。例如：\nhttps://github.com/search?q=%s\n若未填写 %s，搜索词将自动拼接到网址末尾。"
                            )
                            BasicComponent(
                                title = "联想词 API 接口（选填）",
                                summary = "在联想词 API 中使用 %s 代表关键词。例如：\nhttps://api.bing.com/osjson.aspx?query=%s\n支持标准 OpenSearch JSON 建议与数组格式。"
                            )
                        }
                    }
                }
            }
        }
    }

    AddOrEditSearchEngineDialog(
        show = showDialog,
        initialEngine = editingEngine,
        onDismiss = {
            showDialog = false
            editingEngine = null
        },
        onSave = { name, searchUrl, suggestionUrl ->
            val current = editingEngine
            if (current == null) {
                onAddCustomEngine(name, searchUrl, suggestionUrl)
                Toast.makeText(context, "已添加搜索引擎: $name", Toast.LENGTH_SHORT).show()
            } else {
                onUpdateCustomEngine(
                    current.copy(
                        name = name,
                        searchUrl = searchUrl,
                        suggestionUrl = suggestionUrl
                    )
                )
                Toast.makeText(context, "已更新搜索引擎: $name", Toast.LENGTH_SHORT).show()
            }
            showDialog = false
            editingEngine = null
        }
    )

    OverlayDialog(
        show = engineToDelete != null,
        title = "删除搜索引擎",
        summary = "确定要删除「${engineToDelete?.name.orEmpty()}」吗？",
        onDismissRequest = { engineToDelete = null },
        renderInRootScaffold = false
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    text = "取消",
                    onClick = { engineToDelete = null }
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    text = "删除",
                    onClick = {
                        engineToDelete?.let { engine ->
                            onDeleteCustomEngine(engine.id)
                            Toast.makeText(context, "已删除搜索引擎: ${engine.name}", Toast.LENGTH_SHORT).show()
                        }
                        engineToDelete = null
                    }
                )
            }
        }
    }
}

@Composable
fun AddOrEditSearchEngineDialog(
    show: Boolean,
    initialEngine: SearchEngine?,
    onDismiss: () -> Unit,
    onSave: (name: String, searchUrl: String, suggestionUrl: String) -> Unit,
    renderInRootScaffold: Boolean = false
) {
    var name by remember(show, initialEngine) { mutableStateOf(initialEngine?.name.orEmpty()) }
    var searchUrl by remember(show, initialEngine) { mutableStateOf(initialEngine?.searchUrl.orEmpty()) }
    var suggestionUrl by remember(show, initialEngine) { mutableStateOf(initialEngine?.suggestionUrl.orEmpty()) }
    var errorText by remember(show) { mutableStateOf<String?>(null) }

    OverlayDialog(
        show = show,
        title = if (initialEngine == null) "添加搜索引擎" else "编辑搜索引擎",
        summary = "使用 %s 代表搜索词与联想词的占位符",
        onDismissRequest = onDismiss,
        renderInRootScaffold = renderInRootScaffold
    ) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
            Text("名称 (例如: GitHub)", fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, modifier = Modifier.padding(bottom = 4.dp))
            MiuixDialogTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorText = null
                },
                placeholder = "例如: GitHub"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("搜索 URL (含 %s)", fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, modifier = Modifier.padding(bottom = 4.dp))
            MiuixDialogTextField(
                value = searchUrl,
                onValueChange = {
                    searchUrl = it
                    errorText = null
                },
                placeholder = "https://github.com/search?q=%s"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("联想词 API URL (选填, 含 %s)", fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, modifier = Modifier.padding(bottom = 4.dp))
            MiuixDialogTextField(
                value = suggestionUrl,
                onValueChange = {
                    suggestionUrl = it
                    errorText = null
                },
                placeholder = "https://api.github.com/search/... (选填)"
            )

            if (errorText != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorText!!,
                    color = MiuixTheme.colorScheme.error ?: Color(0xFFE53935),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(
                    text = "取消",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                TextButton(
                    text = "保存",
                    onClick = {
                        val trimmedName = name.trim()
                        val trimmedSearch = searchUrl.trim()
                        val trimmedSug = suggestionUrl.trim()
                        if (trimmedName.isBlank()) {
                            errorText = "请输入搜索引擎名称"
                            return@TextButton
                        }
                        if (trimmedSearch.isBlank()) {
                            errorText = "请输入搜索 URL"
                            return@TextButton
                        }
                        if (!trimmedSearch.startsWith("http://", ignoreCase = true) &&
                            !trimmedSearch.startsWith("https://", ignoreCase = true)
                        ) {
                            errorText = "搜索 URL 必须以 http:// 或 https:// 开头"
                            return@TextButton
                        }
                        if (trimmedSug.isNotBlank() &&
                            !trimmedSug.startsWith("http://", ignoreCase = true) &&
                            !trimmedSug.startsWith("https://", ignoreCase = true)
                        ) {
                            errorText = "联想词 URL 必须以 http:// 或 https:// 开头"
                            return@TextButton
                        }
                        onSave(trimmedName, trimmedSearch, trimmedSug)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MiuixDialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MiuixTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MiuixTheme.textStyles.main.copy(
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 14.sp
            ),
            cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
