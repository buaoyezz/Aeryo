package net.zzbuaoye.aeryo.settings.data

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

data class SearchEngine(
    val id: String,
    val name: String,
    val searchUrl: String,
    val suggestionUrl: String = "",
    val isPreset: Boolean = false
) {
    fun buildSearchUrl(query: String): String {
        val encoded = Uri.encode(query)
        return if (searchUrl.contains("%s")) {
            searchUrl.replace("%s", encoded)
        } else {
            "$searchUrl$encoded"
        }
    }

    fun buildSuggestionUrl(query: String): String? {
        val trimmed = suggestionUrl.trim()
        if (trimmed.isBlank()) return null
        val encoded = Uri.encode(query)
        return if (trimmed.contains("%s")) {
            trimmed.replace("%s", encoded)
        } else {
            "$trimmed$encoded"
        }
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("searchUrl", searchUrl)
        put("suggestionUrl", suggestionUrl)
        put("isPreset", isPreset)
    }

    companion object {
        val PRESET_GOOGLE = SearchEngine(
            id = "google",
            name = "Google",
            searchUrl = "https://www.google.com/search?q=%s",
            suggestionUrl = "https://suggestqueries.google.com/complete/search?client=firefox&q=%s",
            isPreset = true
        )

        val PRESET_BING = SearchEngine(
            id = "bing",
            name = "Bing",
            searchUrl = "https://www.bing.com/search?setlang=zh-hans&cc=cn&q=%s",
            suggestionUrl = "https://api.bing.com/osjson.aspx?query=%s",
            isPreset = true
        )

        val PRESET_BAIDU = SearchEngine(
            id = "baidu",
            name = "百度",
            searchUrl = "https://www.baidu.com/s?wd=%s",
            suggestionUrl = "https://suggestion.baidu.com/su?wd=%s&action=opensearch",
            isPreset = true
        )

        val PRESET_YANDEX = SearchEngine(
            id = "yandex",
            name = "Yandex",
            searchUrl = "https://yandex.com/search/?text=%s",
            suggestionUrl = "https://suggest.yandex.com/suggest-ya.cgi?part=%s&v=4&uil=en",
            isPreset = true
        )

        val PRESET_ENGINES = listOf(
            PRESET_GOOGLE,
            PRESET_BING,
            PRESET_BAIDU,
            PRESET_YANDEX
        )

        fun fromJson(json: JSONObject): SearchEngine? {
            val id = json.optString("id").takeIf { it.isNotBlank() } ?: return null
            val name = json.optString("name").takeIf { it.isNotBlank() } ?: "Custom"
            val searchUrl = json.optString("searchUrl").takeIf { it.isNotBlank() } ?: return null
            val suggestionUrl = json.optString("suggestionUrl")
            val isPreset = json.optBoolean("isPreset", false)
            return SearchEngine(
                id = id,
                name = name,
                searchUrl = searchUrl,
                suggestionUrl = suggestionUrl,
                isPreset = isPreset
            )
        }

        fun parseCustomEnginesJson(jsonStr: String?): List<SearchEngine> {
            if (jsonStr.isNullOrBlank()) return emptyList()
            return runCatching {
                val array = JSONArray(jsonStr)
                buildList {
                    for (i in 0 until array.length()) {
                        val obj = array.optJSONObject(i) ?: continue
                        fromJson(obj)?.let { add(it) }
                    }
                }
            }.getOrDefault(emptyList())
        }

        fun serializeCustomEnginesJson(engines: List<SearchEngine>): String {
            val array = JSONArray()
            engines.filterNot { it.isPreset }.forEach {
                array.put(it.toJson())
            }
            return array.toString()
        }
    }
}
