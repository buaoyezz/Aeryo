package net.zzbuaoye.aeryo.browser

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.webkit.ProfileStore
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

private const val INCOGNITO_PROFILE_NAME = "aeryo_incognito"
private const val TAG = "AeryoWebView"

internal fun supportsIsolatedIncognitoProfile(): Boolean = runCatching {
    WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE)
}.getOrDefault(false)

/** Must be the first operation performed on a newly constructed WebView. */
internal fun assignIncognitoProfile(webView: WebView): Boolean {
    if (!supportsIsolatedIncognitoProfile()) return false
    WebViewCompat.setProfile(webView, INCOGNITO_PROFILE_NAME)
    Log.i(TAG, "Using isolated WebView profile for incognito tab")
    return true
}

internal fun cookieManagerFor(webView: WebView, usesIncognitoProfile: Boolean): CookieManager =
    if (usesIncognitoProfile) {
        WebViewCompat.getProfile(webView).cookieManager
    } else {
        CookieManager.getInstance()
    }

internal fun deleteIncognitoProfile() {
    if (!supportsIsolatedIncognitoProfile()) return
    runCatching {
        ProfileStore.getInstance().deleteProfile(INCOGNITO_PROFILE_NAME)
    }.onSuccess { deleted ->
        if (deleted) Log.i(TAG, "Deleted isolated incognito WebView profile")
    }.onFailure { error ->
        Log.w(TAG, "Could not delete isolated incognito WebView profile", error)
    }
}
