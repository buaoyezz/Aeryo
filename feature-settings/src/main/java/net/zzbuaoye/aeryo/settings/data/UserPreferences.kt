package net.zzbuaoye.aeryo.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "aeryo_settings")

class UserPreferences(private val context: Context) {

    companion object {
        val SEARCH_ENGINE_KEY = stringPreferencesKey("search_engine")
        val CUSTOM_SEARCH_ENGINES_KEY = stringPreferencesKey("custom_search_engines")
        val USER_AGENT_KEY = stringPreferencesKey("user_agent")
        val AD_BLOCK_KEY = booleanPreferencesKey("ad_block_enabled")
        val JAVASCRIPT_KEY = booleanPreferencesKey("javascript_enabled")
        val MENU_ORDER_KEY = stringPreferencesKey("menu_order")
        val ADDRESS_BAR_ANIMATION_KEY = booleanPreferencesKey("address_bar_animation_enabled")
        val NIGHT_MODE_KEY = booleanPreferencesKey("night_mode_enabled")
        val DOWNLOAD_MODE_KEY = stringPreferencesKey("download_mode")
        val DOWNLOAD_PATH_KEY = stringPreferencesKey("download_directory_path")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val THEME_PALETTE_KEY = stringPreferencesKey("theme_palette")
        val THEME_KEY_COLOR_KEY = longPreferencesKey("theme_key_color")
        val GLASS_EFFECT_KEY = booleanPreferencesKey("glass_effect_enabled")
        val BLUR_EFFECT_KEY = booleanPreferencesKey("blur_effect_enabled")
        val DO_NOT_TRACK_KEY = booleanPreferencesKey("do_not_track_enabled")
        val BLOCK_THIRD_PARTY_COOKIES_KEY = booleanPreferencesKey("block_third_party_cookies")
        val CLEAR_ON_EXIT_KEY = booleanPreferencesKey("clear_on_exit")
        val TAB_VIEW_MODE_KEY = stringPreferencesKey("tab_view_mode")
        val AD_BLOCK_AUTO_UPDATE_INTERVAL_KEY = stringPreferencesKey("ad_block_auto_update_interval")
        val PRIVACY_BIOMETRIC_KEY = booleanPreferencesKey("privacy_biometric_enabled")
        val VIDEO_AUTO_LANDSCAPE_KEY = booleanPreferencesKey("video_auto_landscape_enabled")
        val VIDEO_KEEP_SCREEN_ON_KEY = booleanPreferencesKey("video_keep_screen_on_enabled")
        val VIDEO_PIP_AUTO_KEY = booleanPreferencesKey("video_pip_auto_enabled")
        val VIDEO_GESTURE_KEY = booleanPreferencesKey("video_gesture_enabled")
        val BACKGROUND_PLAYBACK_KEY = booleanPreferencesKey("background_playback_enabled")
        val MEDIA_SNIFFER_KEY = booleanPreferencesKey("media_sniffer_enabled")
        val MEDIA_SNIFFER_FLOATING_KEY = booleanPreferencesKey("media_sniffer_floating_enabled")
        val MEDIA_SNIFFER_BADGE_MODE_KEY = stringPreferencesKey("media_sniffer_badge_mode")
        val EDGE_SWIPE_NAVIGATION_KEY = booleanPreferencesKey("edge_swipe_navigation_enabled")
        val SMART_DARK_MODE_KEY = booleanPreferencesKey("smart_dark_mode_enabled")
        val QUICK_SCROLL_BUTTON_KEY = booleanPreferencesKey("quick_scroll_button_enabled")

        const val MEDIA_SNIFFER_BADGE_DOT = "dot"
        const val MEDIA_SNIFFER_BADGE_COUNT = "count"
        const val MEDIA_SNIFFER_BADGE_NONE = "none"

        const val AD_BLOCK_AUTO_UPDATE_12H = "12h"
        const val AD_BLOCK_AUTO_UPDATE_3D = "3d"
        const val AD_BLOCK_AUTO_UPDATE_7D = "7d"
        const val AD_BLOCK_AUTO_UPDATE_15D = "15d"
        const val AD_BLOCK_AUTO_UPDATE_30D = "30d"
        const val AD_BLOCK_AUTO_UPDATE_NEVER = "never"

        const val TAB_VIEW_MODE_GRID = "grid"
        const val TAB_VIEW_MODE_HALF = "half_screen"

        const val DOWNLOAD_MODE_SYSTEM = "system"
        const val DOWNLOAD_MODE_BUILT_IN = "built_in"
        const val THEME_MODE_SYSTEM = "monet_system"
        // Kept only so installations from older versions can be migrated safely.
        // These modes are no longer exposed by the settings UI.
        const val THEME_MODE_LIGHT = "light"
        const val THEME_MODE_DARK = "dark"
        const val THEME_MODE_MONET_LIGHT = "monet_light"
        const val THEME_MODE_MONET_DARK = "monet_dark"
        const val THEME_PALETTE_TONAL_SPOT = "tonal_spot"
        const val THEME_PALETTE_VIBRANT = "vibrant"
        const val THEME_PALETTE_EXPRESSIVE = "expressive"
        const val THEME_PALETTE_NEUTRAL = "neutral"
        const val DEFAULT_THEME_KEY_COLOR = 0xFF3482FFL
        const val THEME_KEY_BLUE = 0xFF3482FFL
        const val THEME_KEY_GREEN = 0xFF2E9B69L
        const val THEME_KEY_ORANGE = 0xFFE47732L
        const val THEME_KEY_PURPLE = 0xFF8656C9L
        const val THEME_KEY_RED = 0xFFD94C5CL

        // search engine urls 
        const val ENGINE_GOOGLE = "https://www.google.com/search?q="
        const val ENGINE_BING = "https://www.bing.com/search?setlang=zh-hans&cc=cn&q="
        const val ENGINE_BAIDU = "https://www.baidu.com/s?wd="
        const val ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q="
        const val ENGINE_YAHOO = "https://search.yahoo.com/search?p="
        const val ENGINE_YANDEX = "https://yandex.com/search/?text="
        const val ENGINE_360 = "https://www.so.com/s?q="
        const val ENGINE_SOGOU = "https://www.sogou.com/web?query="

        fun resolveSearchEngine(keyOrUrl: String?, allEngines: List<SearchEngine> = SearchEngine.PRESET_ENGINES): SearchEngine {
            val value = keyOrUrl?.trim().orEmpty()
            if (value.isBlank()) return SearchEngine.PRESET_BING

            // 1. Direct ID match
            allEngines.firstOrNull { it.id.equals(value, ignoreCase = true) }?.let { return it }

            // 2. Direct Search URL match (exact or template-stripped)
            allEngines.firstOrNull {
                it.searchUrl.equals(value, ignoreCase = true) ||
                    it.searchUrl.replace("%s", "").equals(value.replace("%s", ""), ignoreCase = true)
            }?.let { return it }

            // 3. Domain matching for preset backward compatibility
            val lower = value.lowercase()
            return when {
                lower.contains("google.com") -> SearchEngine.PRESET_GOOGLE
                lower.contains("bing.com") -> SearchEngine.PRESET_BING
                lower.contains("baidu.com") -> SearchEngine.PRESET_BAIDU
                lower.contains("yandex.com") -> SearchEngine.PRESET_YANDEX
                // If it looks like a valid custom URL, create an on-the-fly custom engine
                value.startsWith("http://") || value.startsWith("https://") -> {
                    SearchEngine(
                        id = "custom_${value.hashCode()}",
                        name = "自定义",
                        searchUrl = value,
                        isPreset = false
                    )
                }
                else -> SearchEngine.PRESET_BING
            }
        }

        fun normalizeSearchEngine(engineUrl: String?): String {
            return resolveSearchEngine(engineUrl).searchUrl
        }

        fun normalizeThemeMode(mode: String?): String = when (mode) {
            THEME_MODE_LIGHT -> THEME_MODE_MONET_LIGHT
            THEME_MODE_DARK -> THEME_MODE_MONET_DARK
            THEME_MODE_MONET_LIGHT -> THEME_MODE_MONET_LIGHT
            THEME_MODE_MONET_DARK -> THEME_MODE_MONET_DARK
            else -> THEME_MODE_SYSTEM
        }
    }

    val customSearchEngines: Flow<List<SearchEngine>> = context.dataStore.data.map { prefs ->
        SearchEngine.parseCustomEnginesJson(prefs[CUSTOM_SEARCH_ENGINES_KEY])
    }

    val allSearchEngines: Flow<List<SearchEngine>> = context.dataStore.data.map { prefs ->
        val customs = SearchEngine.parseCustomEnginesJson(prefs[CUSTOM_SEARCH_ENGINES_KEY])
        SearchEngine.PRESET_ENGINES + customs
    }

    val currentSearchEngine: Flow<SearchEngine> = context.dataStore.data.map { prefs ->
        val key = prefs[SEARCH_ENGINE_KEY]
        val customs = SearchEngine.parseCustomEnginesJson(prefs[CUSTOM_SEARCH_ENGINES_KEY])
        val all = SearchEngine.PRESET_ENGINES + customs
        resolveSearchEngine(key, all)
    }

    val searchEngine: Flow<String> = currentSearchEngine.map { it.searchUrl }

    val userAgent: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_AGENT_KEY] ?: "MOBILE"
    }

    val adBlockEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AD_BLOCK_KEY] ?: true
    }

    val javaScriptEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[JAVASCRIPT_KEY] ?: true
    }

    val menuOrder: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[MENU_ORDER_KEY]
            ?.split(",")
            ?.filter(String::isNotBlank)
            .orEmpty()
    }

    val addressBarAnimationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ADDRESS_BAR_ANIMATION_KEY] ?: true
    }

    val nightModeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NIGHT_MODE_KEY] ?: false
    }

    val downloadMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DOWNLOAD_MODE_KEY] ?: DOWNLOAD_MODE_BUILT_IN
    }

    val downloadPath: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DOWNLOAD_PATH_KEY]?.takeIf(String::isNotBlank) ?: getDefaultDownloadPath()
    }

    val tabViewMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[TAB_VIEW_MODE_KEY] ?: TAB_VIEW_MODE_GRID
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        normalizeThemeMode(prefs[THEME_MODE_KEY])
    }

    val themePalette: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_PALETTE_KEY] ?: THEME_PALETTE_TONAL_SPOT
    }

    val themeKeyColor: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY_COLOR_KEY] ?: DEFAULT_THEME_KEY_COLOR
    }

    val glassEffectEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[GLASS_EFFECT_KEY] ?: true
    }

    val blurEffectEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BLUR_EFFECT_KEY] ?: false
    }

    val doNotTrackEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DO_NOT_TRACK_KEY] ?: true
    }

    val blockThirdPartyCookies: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BLOCK_THIRD_PARTY_COOKIES_KEY] ?: false
    }

    val clearOnExit: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[CLEAR_ON_EXIT_KEY] ?: false
    }

    val privacyBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PRIVACY_BIOMETRIC_KEY] ?: false
    }

    val videoAutoLandscapeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[VIDEO_AUTO_LANDSCAPE_KEY] ?: true
    }

    val videoKeepScreenOnEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[VIDEO_KEEP_SCREEN_ON_KEY] ?: true
    }

    val videoPipAutoEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[VIDEO_PIP_AUTO_KEY] ?: true
    }

    val videoGestureEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[VIDEO_GESTURE_KEY] ?: true
    }

    val backgroundPlaybackEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BACKGROUND_PLAYBACK_KEY] ?: true
    }

    val mediaSnifferEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MEDIA_SNIFFER_KEY] ?: true
    }

    val mediaSnifferFloatingEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MEDIA_SNIFFER_FLOATING_KEY] ?: false
    }

    val mediaSnifferBadgeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[MEDIA_SNIFFER_BADGE_MODE_KEY] ?: MEDIA_SNIFFER_BADGE_DOT
    }

    val edgeSwipeNavigationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[EDGE_SWIPE_NAVIGATION_KEY] ?: true
    }

    val smartDarkModeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SMART_DARK_MODE_KEY] ?: true
    }

    val quickScrollButtonEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[QUICK_SCROLL_BUTTON_KEY] ?: true
    }

    val adBlockSources: Flow<List<AdBlockSource>> = context.dataStore.data.map { prefs ->
        val serialized = prefs[stringPreferencesKey("ad_block_sources")]
        val defaultSources = listOf(
            AdBlockSource(
                id = "easylist_china",
                name = "EasyList China (中文基础过滤)",
                url = "https://easylist-downloads.adblockplus.org/easylistchina.txt",
                isEnabled = true
            ),
            AdBlockSource(
                id = "easyprivacy",
                name = "EasyPrivacy (隐私与追踪拦截)",
                url = "https://easylist-downloads.adblockplus.org/easyprivacy.txt",
                isEnabled = true
            ),
            AdBlockSource(
                id = "cjx_annoyance",
                name = "CJX's Annoyance (弹窗与反广告拦截)",
                url = "https://raw.githubusercontent.com/cjx82630/cjxlist/master/cjx-annoyance.txt",
                isEnabled = true
            ),
            AdBlockSource(
                id = "anti_ad",
                name = "anti-AD (中文区强力过滤)",
                url = "https://raw.githubusercontent.com/privacy-protection-tools/anti-AD/master/anti-ad-easylist.txt",
                isEnabled = false
            ),
            AdBlockSource(
                id = "easylist",
                name = "EasyList (通用国际规则)",
                url = "https://easylist-downloads.adblockplus.org/easylist.txt",
                isEnabled = false
            ),
            AdBlockSource(
                id = "adguard_mobile",
                name = "AdGuard Mobile (移动端优化过滤)",
                url = "https://filters.adtidy.org/extension/chromium/filters/11.txt",
                isEnabled = false
            )
        )
        if (serialized.isNullOrEmpty()) {
            defaultSources
        } else {
            val savedSources = serialized.split(";;;").mapNotNull { AdBlockSource.deserialize(it) }
            val savedIds = savedSources.map { it.id }.toSet()
            val missingDefaults = defaultSources.filter { it.id !in savedIds }
            savedSources + missingDefaults
        }
    }

    val adBlockAutoUpdateInterval: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[AD_BLOCK_AUTO_UPDATE_INTERVAL_KEY] ?: AD_BLOCK_AUTO_UPDATE_3D
    }

    suspend fun setAdBlockAutoUpdateInterval(interval: String) {
        context.dataStore.edit { prefs ->
            prefs[AD_BLOCK_AUTO_UPDATE_INTERVAL_KEY] = interval
        }
    }

    suspend fun setSearchEngine(engineIdOrUrl: String) {
        context.dataStore.edit { prefs ->
            prefs[SEARCH_ENGINE_KEY] = engineIdOrUrl.trim()
        }
    }

    suspend fun addCustomSearchEngine(name: String, searchUrl: String, suggestionUrl: String = ""): SearchEngine {
        val id = "custom_${System.currentTimeMillis()}"
        val newEngine = SearchEngine(
            id = id,
            name = name.trim(),
            searchUrl = searchUrl.trim(),
            suggestionUrl = suggestionUrl.trim(),
            isPreset = false
        )
        context.dataStore.edit { prefs ->
            val currentCustoms = SearchEngine.parseCustomEnginesJson(prefs[CUSTOM_SEARCH_ENGINES_KEY]).toMutableList()
            currentCustoms.add(newEngine)
            prefs[CUSTOM_SEARCH_ENGINES_KEY] = SearchEngine.serializeCustomEnginesJson(currentCustoms)
        }
        return newEngine
    }

    suspend fun updateCustomSearchEngine(engine: SearchEngine) {
        context.dataStore.edit { prefs ->
            val currentCustoms = SearchEngine.parseCustomEnginesJson(prefs[CUSTOM_SEARCH_ENGINES_KEY]).toMutableList()
            val index = currentCustoms.indexOfFirst { it.id == engine.id }
            if (index >= 0) {
                currentCustoms[index] = engine.copy(isPreset = false)
                prefs[CUSTOM_SEARCH_ENGINES_KEY] = SearchEngine.serializeCustomEnginesJson(currentCustoms)
            }
        }
    }

    suspend fun deleteCustomSearchEngine(id: String) {
        context.dataStore.edit { prefs ->
            val currentCustoms = SearchEngine.parseCustomEnginesJson(prefs[CUSTOM_SEARCH_ENGINES_KEY]).toMutableList()
            currentCustoms.removeAll { it.id == id }
            prefs[CUSTOM_SEARCH_ENGINES_KEY] = SearchEngine.serializeCustomEnginesJson(currentCustoms)
            if (prefs[SEARCH_ENGINE_KEY] == id) {
                prefs[SEARCH_ENGINE_KEY] = SearchEngine.PRESET_BING.id
            }
        }
    }

    suspend fun migrateSearchEngine() {
        context.dataStore.edit { prefs ->
            val current = prefs[SEARCH_ENGINE_KEY]
            if (current.isNullOrBlank()) {
                prefs[SEARCH_ENGINE_KEY] = SearchEngine.PRESET_BING.id
            }
        }
    }

    suspend fun setUserAgent(ua: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_AGENT_KEY] = ua
        }
    }

    suspend fun setAdBlockEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AD_BLOCK_KEY] = enabled
        }
    }

    suspend fun setJavaScriptEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[JAVASCRIPT_KEY] = enabled
        }
    }

    suspend fun setMenuOrder(order: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[MENU_ORDER_KEY] = order.joinToString(",")
        }
    }

    suspend fun setAddressBarAnimationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ADDRESS_BAR_ANIMATION_KEY] = enabled
        }
    }

    suspend fun setTabViewMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[TAB_VIEW_MODE_KEY] = mode
        }
    }

    suspend fun setNightModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NIGHT_MODE_KEY] = enabled
        }
    }

    suspend fun setDownloadMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[DOWNLOAD_MODE_KEY] = when (mode) {
                DOWNLOAD_MODE_BUILT_IN -> DOWNLOAD_MODE_BUILT_IN
                else -> DOWNLOAD_MODE_SYSTEM
            }
        }
    }

    suspend fun setDownloadPath(path: String) {
        context.dataStore.edit { prefs ->
            prefs[DOWNLOAD_PATH_KEY] = path
        }
    }

    fun getDefaultDownloadPath(): String {
        val publicDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
        return publicDir?.absolutePath ?: "/storage/emulated/0/Download"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = normalizeThemeMode(mode)
        }
    }

    suspend fun setThemePalette(palette: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_PALETTE_KEY] = when (palette) {
                THEME_PALETTE_VIBRANT,
                THEME_PALETTE_EXPRESSIVE,
                THEME_PALETTE_NEUTRAL -> palette
                else -> THEME_PALETTE_TONAL_SPOT
            }
        }
    }

    suspend fun setThemeKeyColor(color: Long) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY_COLOR_KEY] = color
        }
    }

    suspend fun setGlassEffectEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[GLASS_EFFECT_KEY] = enabled
        }
    }

    suspend fun setBlurEffectEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BLUR_EFFECT_KEY] = enabled
        }
    }

    suspend fun setDoNotTrackEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DO_NOT_TRACK_KEY] = enabled }
    }

    suspend fun setBlockThirdPartyCookies(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[BLOCK_THIRD_PARTY_COOKIES_KEY] = enabled }
    }

    suspend fun setClearOnExit(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[CLEAR_ON_EXIT_KEY] = enabled }
    }

    suspend fun setPrivacyBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PRIVACY_BIOMETRIC_KEY] = enabled
        }
    }

    suspend fun setVideoAutoLandscapeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VIDEO_AUTO_LANDSCAPE_KEY] = enabled
        }
    }

    suspend fun setVideoKeepScreenOnEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VIDEO_KEEP_SCREEN_ON_KEY] = enabled
        }
    }

    suspend fun setVideoPipAutoEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VIDEO_PIP_AUTO_KEY] = enabled
        }
    }

    suspend fun setVideoGestureEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VIDEO_GESTURE_KEY] = enabled
        }
    }

    suspend fun setBackgroundPlaybackEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BACKGROUND_PLAYBACK_KEY] = enabled
        }
    }

    suspend fun setMediaSnifferEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MEDIA_SNIFFER_KEY] = enabled
        }
    }

    suspend fun setMediaSnifferFloatingEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MEDIA_SNIFFER_FLOATING_KEY] = enabled
        }
    }

    suspend fun setMediaSnifferBadgeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[MEDIA_SNIFFER_BADGE_MODE_KEY] = when (mode) {
                MEDIA_SNIFFER_BADGE_COUNT -> MEDIA_SNIFFER_BADGE_COUNT
                MEDIA_SNIFFER_BADGE_NONE -> MEDIA_SNIFFER_BADGE_NONE
                else -> MEDIA_SNIFFER_BADGE_DOT
            }
        }
    }

    suspend fun setEdgeSwipeNavigationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[EDGE_SWIPE_NAVIGATION_KEY] = enabled
        }
    }

    suspend fun setSmartDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SMART_DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setQuickScrollButtonEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[QUICK_SCROLL_BUTTON_KEY] = enabled
        }
    }

    suspend fun setAdBlockSources(sources: List<AdBlockSource>) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("ad_block_sources")] = sources.joinToString(";;;") { it.serialize() }
        }
    }
}
