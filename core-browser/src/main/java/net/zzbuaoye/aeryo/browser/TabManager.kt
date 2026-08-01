package net.zzbuaoye.aeryo.browser

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.zzbuaoye.aeryo.browser.model.WebTab

/**
 * 标签页管理器 (TabManager) - Chromium 风格多标签管理逻辑
 */
class TabManager {
    data class State(
        val tabs: List<WebTab> = emptyList(),
        val activeTabIndex: Int = 0
    ) {
        val currentTab: WebTab?
            get() = tabs.getOrNull(activeTabIndex)
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    val currentTab: WebTab?
        get() = _state.value.currentTab

    init {
        // 默认初始化一个主页标签
        createNewTab("about:blank", isIncognito = false)
    }

    fun createNewTab(
        url: String = "about:blank",
        isIncognito: Boolean = false,
        lastSearchQuery: String = ""
    ): WebTab {
        val newTab = WebTab(
            url = url,
            title = if (url == "about:blank") "新标签页" else url,
            isIncognito = isIncognito,
            lastSearchQuery = lastSearchQuery
        )
        val currentList = _state.value.tabs.toMutableList()
        currentList.add(newTab)
        _state.value = State(
            tabs = currentList,
            activeTabIndex = currentList.lastIndex
        )
        return newTab
    }

    fun closeTab(tabId: String) {
        val currentState = _state.value
        val currentList = currentState.tabs.toMutableList()
        val indexToClose = currentList.indexOfFirst { it.id == tabId }
        if (indexToClose != -1) {
            val tab = currentList[indexToClose]
            tab.webView?.apply {
                if (tab.isIncognito) {
                    clearHistory()
                    clearCache(true)
                    clearFormData()
                }
                destroy()
            }
            tab.webView = null
            currentList.removeAt(indexToClose)

            if (currentList.isEmpty()) {
                val replacement = WebTab(url = "about:blank", title = "新标签页")
                _state.value = State(tabs = listOf(replacement), activeTabIndex = 0)
            } else {
                val nextActiveIndex = when {
                    currentState.activeTabIndex >= currentList.size -> currentList.lastIndex
                    currentState.activeTabIndex > indexToClose -> currentState.activeTabIndex - 1
                    else -> currentState.activeTabIndex
                }
                _state.value = State(
                    tabs = currentList,
                    activeTabIndex = nextActiveIndex
                )
            }
        }
    }

    fun closeIncognitoTabs() {
        val currentList = _state.value.tabs.toMutableList()
        val incognitoTabs = currentList.filter { it.isIncognito }
        incognitoTabs.forEach { tab ->
            tab.webView?.apply {
                clearHistory()
                clearCache(true)
                clearFormData()
                destroy()
            }
            tab.webView = null
        }
        currentList.removeAll { it.isIncognito }
        if (currentList.isEmpty()) {
            val replacement = WebTab(url = "about:blank", title = "新标签页")
            _state.value = State(tabs = listOf(replacement), activeTabIndex = 0)
        } else {
            val nextActiveIndex = _state.value.activeTabIndex.coerceIn(0, currentList.lastIndex)
            _state.value = State(tabs = currentList, activeTabIndex = nextActiveIndex)
        }
    }

    fun closeAllTabs() {
        _state.value.tabs.forEach { tab ->
            tab.webView?.apply {
                if (tab.isIncognito) {
                    clearHistory()
                    clearCache(true)
                    clearFormData()
                }
                destroy()
            }
            tab.webView = null
        }
        val replacement = WebTab(url = "about:blank", title = "新标签页")
        _state.value = State(tabs = listOf(replacement), activeTabIndex = 0)
    }

    fun selectTab(index: Int) {
        val currentState = _state.value
        if (index in currentState.tabs.indices && index != currentState.activeTabIndex) {
            _state.value = currentState.copy(activeTabIndex = index)
        }
    }

    fun updateCurrentTab(transform: (WebTab) -> WebTab) {
        val currentState = _state.value
        val currentTab = currentState.currentTab ?: return
        updateTab(currentTab.id, transform)
    }

    fun updateTab(tabId: String, transform: (WebTab) -> WebTab) {
        val currentState = _state.value
        val index = currentState.tabs.indexOfFirst { it.id == tabId }
        if (index >= 0) {
            val currentTab = currentState.tabs[index]
            val updatedTab = transform(currentTab)
            if (updatedTab != currentTab) {
                val updatedTabs = currentState.tabs.toMutableList()
                updatedTabs[index] = updatedTab
                _state.value = currentState.copy(tabs = updatedTabs)
            }
        }
    }
}
