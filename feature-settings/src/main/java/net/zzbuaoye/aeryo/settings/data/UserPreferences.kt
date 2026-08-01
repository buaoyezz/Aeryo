package net.zzbuaoye.aeryo.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

        const val DOWNLOAD_MODE_SYSTEM = "system"
        const val DOWNLOAD_MODE_BUILT_IN = "built_in"
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
