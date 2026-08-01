package net.zzbuaoye.aeryo.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "aeryo_settings")

class UserPreferences(private val context: Context) {

    companion object {
        val SEARCH_ENGINE_KEY = stringPreferencesKey("search_engine")
        val USER_AGENT_KEY = stringPreferencesKey("user_agent")
        val AD_BLOCK_KEY = booleanPreferencesKey("ad_block_enabled")
        val JAVASCRIPT_KEY = booleanPreferencesKey("javascript_enabled")
        val MENU_ORDER_KEY = stringPreferencesKey("menu_order")
        val ADDRESS_BAR_ANIMATION_KEY = booleanPreferencesKey("address_bar_animation_enabled")
        val NIGHT_MODE_KEY = booleanPreferencesKey("night_mode_enabled")
        val DOWNLOAD_MODE_KEY = stringPreferencesKey("download_mode")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val THEME_PALETTE_KEY = stringPreferencesKey("theme_palette")
        val THEME_KEY_COLOR_KEY = longPreferencesKey("theme_key_color")
        val GLASS_EFFECT_KEY = booleanPreferencesKey("glass_effect_enabled")
        val BLUR_EFFECT_KEY = booleanPreferencesKey("blur_effect_enabled")
        val LOGO_VARIANT_KEY = stringPreferencesKey("logo_variant")
        val LOGO_STROKE_WIDTH_KEY = floatPreferencesKey("logo_stroke_width")
        val LOGO_CUSTOM_COLOR_KEY = longPreferencesKey("logo_custom_color")
        val LOGO_CUSTOM_COLOR_ENABLED_KEY = booleanPreferencesKey("logo_custom_color_enabled")
        val LOGO_CUSTOM_IMAGE_URI_KEY = stringPreferencesKey("logo_custom_image_uri")
        val LOGO_CUSTOM_TEXT_KEY = stringPreferencesKey("logo_custom_text")
        val LOGO_OFFSET_X_KEY = floatPreferencesKey("logo_offset_x")
        val LOGO_OFFSET_Y_KEY = floatPreferencesKey("logo_offset_y")
        val LAUNCHER_ICON_VARIANT_KEY = stringPreferencesKey("launcher_icon_variant")
        val DO_NOT_TRACK_KEY = booleanPreferencesKey("do_not_track_enabled")
        val BLOCK_THIRD_PARTY_COOKIES_KEY = booleanPreferencesKey("block_third_party_cookies")
        val CLEAR_ON_EXIT_KEY = booleanPreferencesKey("clear_on_exit")

        const val DOWNLOAD_MODE_SYSTEM = "system"
        const val DOWNLOAD_MODE_BUILT_IN = "built_in"
        const val THEME_MODE_SYSTEM = "monet_system"
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
        const val LOGO_VARIANT_DEFAULT = "default"
        const val LOGO_VARIANT_AURORA = "aurora"
        const val LOGO_VARIANT_SUNSET = "sunset"
        const val LOGO_VARIANT_MINT = "mint"
        const val LOGO_VARIANT_MONO = "mono"
        const val LOGO_VARIANT_CUSTOM_IMAGE = "custom_image"
        const val LOGO_VARIANT_CUSTOM_TEXT = "custom_text"
        const val DEFAULT_LOGO_STROKE_WIDTH = 12f
        const val DEFAULT_LOGO_CUSTOM_COLOR = 0xFF4D8DFFL
        const val LAUNCHER_ICON_DEFAULT = "default"
        const val LAUNCHER_ICON_FLAME = "flame"
        const val LAUNCHER_ICON_AURORA = "aurora"
        const val LAUNCHER_ICON_SUNSET = "sunset"
        const val LAUNCHER_ICON_OCEAN = "ocean"
        val PRIVACY_BIOMETRIC_KEY = booleanPreferencesKey("privacy_biometric_enabled")

        // search engine urls 
        const val ENGINE_GOOGLE = "https://www.google.com/search?q="
        const val ENGINE_BING = "https://www.bing.com/search?q="
        const val ENGINE_BAIDU = "https://www.baidu.com/s?wd="
        const val ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q="
        const val ENGINE_YAHOO = "https://search.yahoo.com/search?p="
        const val ENGINE_YANDEX = "https://yandex.com/search/?text="
        const val ENGINE_360 = "https://www.so.com/s?q="
        const val ENGINE_SOGOU = "https://www.sogou.com/web?query="
        
    }

    val searchEngine: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SEARCH_ENGINE_KEY] ?: ENGINE_BING
    }

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
        prefs[DOWNLOAD_MODE_KEY] ?: DOWNLOAD_MODE_SYSTEM
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE_KEY] ?: THEME_MODE_SYSTEM
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

    val logoVariant: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LOGO_VARIANT_KEY] ?: LOGO_VARIANT_DEFAULT
    }

    val logoStrokeWidth: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[LOGO_STROKE_WIDTH_KEY] ?: DEFAULT_LOGO_STROKE_WIDTH
    }

    val logoCustomColor: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LOGO_CUSTOM_COLOR_KEY] ?: DEFAULT_LOGO_CUSTOM_COLOR
    }

    val logoCustomColorEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[LOGO_CUSTOM_COLOR_ENABLED_KEY] ?: false
    }

    val logoCustomImageUri: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LOGO_CUSTOM_IMAGE_URI_KEY].orEmpty()
    }

    val logoCustomText: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LOGO_CUSTOM_TEXT_KEY] ?: "A"
    }

    val logoOffsetX: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[LOGO_OFFSET_X_KEY] ?: 0f
    }

    val logoOffsetY: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[LOGO_OFFSET_Y_KEY] ?: 0f
    }

    val launcherIconVariant: Flow<String> = context.dataStore.data.map { prefs ->
        when (prefs[LAUNCHER_ICON_VARIANT_KEY]) {
            "firefox" -> LAUNCHER_ICON_FLAME
            null -> LAUNCHER_ICON_DEFAULT
            else -> prefs[LAUNCHER_ICON_VARIANT_KEY] ?: LAUNCHER_ICON_DEFAULT
        }
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

    val adBlockSources: Flow<List<AdBlockSource>> = context.dataStore.data.map { prefs ->
        val serialized = prefs[stringPreferencesKey("ad_block_sources")]
        if (serialized.isNullOrEmpty()) {
            listOf(
                AdBlockSource(
                    id = "easylist_china",
                    name = "EasyList China (中文规则)",
                    url = "https://easylist-downloads.adblockplus.org/easylistchina.txt",
                    isEnabled = false
                ),
                AdBlockSource(
                    id = "easylist",
                    name = "EasyList (通用规则)",
                    url = "https://easylist-downloads.adblockplus.org/easylist.txt",
                    isEnabled = false
                )
            )
        } else {
            serialized.split(";;;").mapNotNull { AdBlockSource.deserialize(it) }
        }
    }

    suspend fun setSearchEngine(engineUrl: String) {
        context.dataStore.edit { prefs ->
            prefs[SEARCH_ENGINE_KEY] = engineUrl
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

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = when (mode) {
                THEME_MODE_LIGHT,
                THEME_MODE_DARK,
                THEME_MODE_MONET_LIGHT,
                THEME_MODE_MONET_DARK -> mode
                else -> THEME_MODE_SYSTEM
            }
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

    suspend fun setLogoVariant(variant: String) {
        context.dataStore.edit { prefs ->
            prefs[LOGO_VARIANT_KEY] = when (variant) {
                LOGO_VARIANT_DEFAULT,
                LOGO_VARIANT_AURORA,
                LOGO_VARIANT_SUNSET,
                LOGO_VARIANT_MINT,
                LOGO_VARIANT_MONO,
                LOGO_VARIANT_CUSTOM_IMAGE,
                LOGO_VARIANT_CUSTOM_TEXT -> variant
                else -> LOGO_VARIANT_DEFAULT
            }
        }
    }

    suspend fun setLogoStrokeWidth(width: Float) {
        context.dataStore.edit { prefs ->
            prefs[LOGO_STROKE_WIDTH_KEY] = width.coerceIn(6f, 22f)
        }
    }

    suspend fun setLogoCustomColor(color: Long) {
        context.dataStore.edit { prefs -> prefs[LOGO_CUSTOM_COLOR_KEY] = color }
    }

    suspend fun setLogoCustomColorEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[LOGO_CUSTOM_COLOR_ENABLED_KEY] = enabled }
    }

    suspend fun setLogoCustomImageUri(uri: String) {
        context.dataStore.edit { prefs -> prefs[LOGO_CUSTOM_IMAGE_URI_KEY] = uri }
    }

    suspend fun setLogoCustomText(text: String) {
        context.dataStore.edit { prefs ->
            prefs[LOGO_CUSTOM_TEXT_KEY] = text.trim().take(8).ifBlank { "A" }
        }
    }

    suspend fun setLogoOffsetX(offset: Float) {
        context.dataStore.edit { prefs -> prefs[LOGO_OFFSET_X_KEY] = offset.coerceIn(-60f, 60f) }
    }

    suspend fun setLogoOffsetY(offset: Float) {
        context.dataStore.edit { prefs -> prefs[LOGO_OFFSET_Y_KEY] = offset.coerceIn(-60f, 60f) }
    }

    suspend fun setLauncherIconVariant(variant: String) {
        context.dataStore.edit { prefs ->
            prefs[LAUNCHER_ICON_VARIANT_KEY] = when (variant) {
                LAUNCHER_ICON_FLAME,
                LAUNCHER_ICON_AURORA,
                LAUNCHER_ICON_SUNSET,
                LAUNCHER_ICON_OCEAN -> variant
                else -> LAUNCHER_ICON_DEFAULT
            }
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

    suspend fun setAdBlockSources(sources: List<AdBlockSource>) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("ad_block_sources")] = sources.joinToString(";;;") { it.serialize() }
        }
    }
}
