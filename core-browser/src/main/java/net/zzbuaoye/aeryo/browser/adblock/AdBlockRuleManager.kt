package net.zzbuaoye.aeryo.browser.adblock

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.HashSet

object AdBlockRuleManager {
    private const val RULE_DIR = "adblock_rules"

    private data class HostPathRule(
        val host: String,
        val pathPattern: String
    )

    // Holds the domains extracted from all enabled rules
    private var blockedDomains = HashSet<String>()
    private var allowedDomains = emptySet<String>()
    private var blockedHostPathRules = emptySet<HostPathRule>()
    private var allowedHostPathRules = emptySet<HostPathRule>()

    fun getBlockedDomains(): HashSet<String> = blockedDomains
    fun getAllowedDomains(): Set<String> = allowedDomains

    fun isAllowedRequest(host: String, url: String): Boolean {
        if (allowedDomains.any { host == it || host.endsWith(".$it") }) return true
        return allowedHostPathRules.any { it.matches(host, url) }
    }

    fun isBlockedRequest(host: String, url: String): Boolean {
        return blockedHostPathRules.any { it.matches(host, url) }
    }

    /**
     * Download a rule file from the given URL and save it with the given ID.
     */
    suspend fun downloadRuleFile(context: Context, id: String, urlString: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dir = File(context.filesDir, RULE_DIR)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val targetFile = File(dir, "$id.txt")
                
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { input ->
                        targetFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    /**
     * Loads enabled rule files from disk into memory.
     * Simple parser extracting domains from ||domain.com^ format.
     */
    suspend fun loadEnabledRules(context: Context, enabledIds: List<String>) {
        withContext(Dispatchers.IO) {
            val newBlockedDomains = HashSet<String>()
            val newAllowedDomains = HashSet<String>()
            val newBlockedHostPathRules = HashSet<HostPathRule>()
            val newAllowedHostPathRules = HashSet<HostPathRule>()
            val dir = File(context.filesDir, RULE_DIR)
            
            if (dir.exists()) {
                for (id in enabledIds) {
                    val file = File(dir, "$id.txt")
                    if (file.exists()) {
                        file.useLines { lines ->
                            lines.forEach { line ->
                                val trimmed = line.trim()
                                // Skip comments, metadata, and CSS element hiding rules (##, #@#, #?#, #$#)
                                if (trimmed.isNotEmpty() &&
                                    !trimmed.startsWith("!") &&
                                    !trimmed.startsWith("[") &&
                                    !trimmed.contains("##") &&
                                    !trimmed.contains("#@#") &&
                                    !trimmed.contains("#?#") &&
                                    !trimmed.contains("#$#")
                                ) {
                                    val withoutOptions = trimmed.substringBefore('$')
                                    val isException = withoutOptions.startsWith("@@")
                                    val rule = withoutOptions.removePrefix("@@")
                                    if (rule.startsWith("||")) {
                                        val hostAndPath = rule.removePrefix("||")
                                        val host = hostAndPath.substringBeforeAny(listOf("/", "^", "*"))
                                            .lowercase()
                                        if (host.isNotBlank() && host.contains('.')) {
                                            val pathPattern = hostAndPath
                                                .removePrefix(host)
                                                .trimStart('^', '/', '*')
                                                .replace('^', '*')
                                                .trimEnd('|')
                                            if (pathPattern.isBlank()) {
                                                if (isException) newAllowedDomains.add(host)
                                                else newBlockedDomains.add(host)
                                            } else {
                                                val hostPathRule = HostPathRule(host, pathPattern.lowercase())
                                                if (isException) newAllowedHostPathRules.add(hostPathRule)
                                                else newBlockedHostPathRules.add(hostPathRule)
                                            }
                                        }
                                    } else if (rule.length > 5 && !rule.contains(' ') &&
                                        (rule.startsWith("http://") || rule.startsWith("https://"))
                                    ) {
                                        parseAbsoluteRule(rule)?.let { hostPathRule ->
                                            if (hostPathRule.pathPattern.isBlank()) {
                                                if (isException) newAllowedDomains.add(hostPathRule.host)
                                                else newBlockedDomains.add(hostPathRule.host)
                                            } else if (isException) {
                                                newAllowedHostPathRules.add(hostPathRule)
                                            } else {
                                                newBlockedHostPathRules.add(hostPathRule)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            blockedDomains = newBlockedDomains
            allowedDomains = newAllowedDomains
            blockedHostPathRules = newBlockedHostPathRules
            allowedHostPathRules = newAllowedHostPathRules
        }
    }

    private fun parseAbsoluteRule(rule: String): HostPathRule? {
        val withoutScheme = rule.substringAfter("://", "")
        if (withoutScheme.isBlank()) return null
        val host = withoutScheme.substringBefore('/').substringBefore('^').substringBefore('*').lowercase()
        if (host.isBlank() || !host.contains('.')) return null
        val pathPattern = withoutScheme.substringAfter('/', "")
            .replace('^', '*')
            .trimStart('/')
            .trimEnd('|')
            .lowercase()
        return HostPathRule(host, pathPattern)
    }

    private fun HostPathRule.matches(requestHost: String, requestUrl: String): Boolean {
        if (requestHost != host && !requestHost.endsWith(".$host")) return false
        if (pathPattern.isBlank()) return true

        val requestPath = requestUrl
            .substringAfter("://", requestUrl)
            .substringAfter('/', "")
            .lowercase()
        var searchFrom = 0
        for (part in pathPattern.split('*').filter(String::isNotBlank)) {
            val matchIndex = requestPath.indexOf(part, searchFrom)
            if (matchIndex < 0) return false
            searchFrom = matchIndex + part.length
        }
        return true
    }

    private fun String.substringBeforeAny(delimiters: List<String>): String {
        val index = delimiters.mapNotNull { delimiter -> indexOf(delimiter).takeIf { it >= 0 } }.minOrNull()
        return if (index == null) this else substring(0, index)
    }
}
