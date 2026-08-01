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

    // Holds the domains extracted from all enabled rules
    private var blockedDomains = HashSet<String>()
    private var blockedUrlFragments = emptySet<String>()
    private var allowedDomains = emptySet<String>()

    fun getBlockedDomains(): HashSet<String> = blockedDomains
    fun getBlockedUrlFragments(): Set<String> = blockedUrlFragments
    fun getAllowedDomains(): Set<String> = allowedDomains

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
            val newBlockedFragments = HashSet<String>()
            val newAllowedDomains = HashSet<String>()
            val dir = File(context.filesDir, RULE_DIR)
            
            if (dir.exists()) {
                for (id in enabledIds) {
                    val file = File(dir, "$id.txt")
                    if (file.exists()) {
                        file.useLines { lines ->
                            lines.forEach { line ->
                                val trimmed = line.trim()
                                // Skip comments and empty lines
                                if (trimmed.isNotEmpty() && !trimmed.startsWith("!") && !trimmed.startsWith("[")) {
                                    val withoutOptions = trimmed.substringBefore('$')
                                    val isException = withoutOptions.startsWith("@@")
                                    val rule = withoutOptions.removePrefix("@@")
                                    if (rule.startsWith("||")) {
                                        val hostAndPath = rule.removePrefix("||")
                                        val host = hostAndPath.substringBeforeAny(listOf("/", "^", "*"))
                                            .lowercase()
                                        if (host.isNotBlank() && host.contains('.')) {
                                            if (isException) newAllowedDomains.add(host)
                                            else newBlockedDomains.add(host)
                                        }
                                        val path = hostAndPath.substringAfter('/', "")
                                        if (!isException && path.isNotBlank()) {
                                            newBlockedFragments.add(path.replace("^", ""))
                                        }
                                    } else if (!isException && rule.length > 3 && !rule.contains(' ')) {
                                        newBlockedFragments.add(rule.replace("*", ""))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            blockedDomains = newBlockedDomains
            blockedUrlFragments = newBlockedFragments
            allowedDomains = newAllowedDomains
        }
    }

    private fun String.substringBeforeAny(delimiters: List<String>): String {
        val index = delimiters.mapNotNull { delimiter -> indexOf(delimiter).takeIf { it >= 0 } }.minOrNull()
        return if (index == null) this else substring(0, index)
    }
}
