package net.zzbuaoye.aeryo.browser.adblock

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

/**
 * 极简高效广告拦截引擎 (AdBlockEngine)
 */
object AdBlockEngine {
    private var isEnabled: Boolean = true
    @Volatile private var blockedRequestCount: Long = 0L



    // 网页美容 (Cosmetic Filter) CSS 注入脚本
    const val COSMETIC_CSS_SCRIPT = """
        (function() {
            var css = '.ad,.ads,.ad-container,.ad-wrapper,.advertisement,.sponsor,.sponsored,' +
                '[id^="ad-"],[id^="ad_"],[id*="-ad-"],[class^="ad-"],[class*=" ad-"],' +
                '[class*="advert"],[aria-label*="广告"],iframe[src*="ad"]{display:none!important}';
            var style = document.getElementById('aeryo-adblock-style') || document.createElement('style');
            style.id = 'aeryo-adblock-style';
            style.textContent = css;
            document.head && document.head.appendChild(style);
            if (!window.__aeryoAdObserver && document.body) {
                window.__aeryoAdObserver = new MutationObserver(function() {
                    document.querySelectorAll('.ad,.ads,.advertisement,[class*="advert"]').forEach(function(node) {
                        node.style.setProperty('display','none','important');
                    });
                });
                window.__aeryoAdObserver.observe(document.body, {childList:true, subtree:true});
            }
        })();
    """

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun getBlockedRequestCount(): Long = blockedRequestCount

    fun isAdRequest(request: WebResourceRequest): Boolean {
        if (!isEnabled) return false
        val uri = request.url ?: return false
        val host = uri.host?.lowercase() ?: return false
        if (AdBlockRuleManager.getAllowedDomains().any { host == it || host.endsWith(".$it") }) return false
        
        // Fast exact match or subdomain match
        val blockedDomains = AdBlockRuleManager.getBlockedDomains()
        
        // Check exact match
        if (blockedDomains.contains(host)) {
            blockedRequestCount++
            return true
        }
        
        // Check subdomains (e.g. ad.example.com -> check example.com)
        var tempHost = host
        var dotIndex = tempHost.indexOf('.')
        while (dotIndex != -1) {
            tempHost = tempHost.substring(dotIndex + 1)
            if (blockedDomains.contains(tempHost)) {
                blockedRequestCount++
                return true
            }
            dotIndex = tempHost.indexOf('.')
        }
        if (AdBlockRuleManager.getBlockedUrlFragments().any { it.isNotBlank() && uri.toString().contains(it, true) }) {
            blockedRequestCount++
            return true
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
