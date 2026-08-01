package net.zzbuaoye.aeryo.settings.data
    
data class AdBlockSource(
    val id: String,
    val name: String,
    val url: String,
    val isEnabled: Boolean = true,
    val lastUpdated: Long = 0L,
    val ruleCount: Int = 0
) {
    fun serialize(): String {
        return "$id|%|$name|%|$url|%|$isEnabled|%|$lastUpdated|%|$ruleCount"
    }

    companion object {
        fun deserialize(data: String): AdBlockSource? {
            val parts = data.split("|%|")
            if (parts.size != 6) return null
            return try {
                AdBlockSource(
                    id = parts[0],
                    name = parts[1],
                    url = parts[2],
                    isEnabled = parts[3].toBoolean(),
                    lastUpdated = parts[4].toLong(),
                    ruleCount = parts[5].toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
