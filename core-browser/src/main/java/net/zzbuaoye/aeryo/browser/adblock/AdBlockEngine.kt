package net.zzbuaoye.aeryo.browser.adblock

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

/**
 * 极简高效广告拦截引擎 (AdBlockEngine)
 */
object AdBlockEngine {
    private var isEnabled: Boolean = true



    // 网页美容 (Cosmetic Filter) CSS 注入脚本
    const val COSMETIC_CSS_SCRIPT = """
        (function() {
            var style = document.createElement('style');
            style.type = 'text/css';
            style.innerHTML = '.ad, .ads, .ad-container, .ad-wrapper, [id^="ad_"], [class*="ad_"], iframe[src*="ad"] { display: none !important; }';
            document.getElementsByTagName('head')[0].appendChild(style);
        })();
    """

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun isAdRequest(request: WebResourceRequest): Boolean {
        if (!isEnabled) return false
        val uri = request.url ?: return false
        val host = uri.host?.lowercase() ?: return false
        
        // Fast exact match or subdomain match
        val blockedDomains = AdBlockRuleManager.getBlockedDomains()
        
        // Check exact match
        if (blockedDomains.contains(host)) return true
        
        // Check subdomains (e.g. ad.example.com -> check example.com)
        var tempHost = host
        var dotIndex = tempHost.indexOf('.')
        while (dotIndex != -1) {
            tempHost = tempHost.substring(dotIndex + 1)
            if (blockedDomains.contains(tempHost)) return true
            dotIndex = tempHost.indexOf('.')
        }
        
        return false
    }

    fun createEmptyResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream("".toByteArray())
        )
    }
}
