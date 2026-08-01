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

    fun getBlockedDomains(): HashSet<String> = blockedDomains

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
                                    // Extremely simple parser for basic MVP: 
                                    // Extract simple domain blocks like ||example.com^
                                    if (trimmed.startsWith("||") && trimmed.endsWith("^")) {
                                        val domain = trimmed.substring(2, trimmed.length - 1)
                                        // Ignore paths for now, just block the domain root
                                        if (!domain.contains("/")) {
                                            newBlockedDomains.add(domain)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            blockedDomains = newBlockedDomains
        }
    }
}
