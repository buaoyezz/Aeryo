package net.zzbuaoye.aeryo.settings.data

data class ClearDataOptions(
    val clearCache: Boolean = true,
    val clearFormData: Boolean = true,
    val clearHistory: Boolean = true,
    val clearClosedTabs: Boolean = true,
    val clearWebStorage: Boolean = true,
    val clearCookies: Boolean = true,
    val clearAppCache: Boolean = true
) {
    val hasAnySelected: Boolean
        get() = clearCache || clearFormData || clearHistory || clearClosedTabs || clearWebStorage || clearCookies || clearAppCache
}
