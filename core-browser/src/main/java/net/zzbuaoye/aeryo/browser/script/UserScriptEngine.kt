package net.zzbuaoye.aeryo.browser.script

import android.webkit.WebView

data class UserScript(
    val id: String,
    val name: String,
    val matchHost: String = "*", // "*" means all domains
    val scriptCode: String,
    val isEnabled: Boolean = true
)

/**
 * Aeryo 自定义 JavaScript 注入与脚本引擎
 */
object UserScriptEngine {
    private val scripts = mutableListOf<UserScript>()

    init {
        // 默认内置高效脚本：长按视频速度控制/网页夜间模式样式支持等
        scripts.add(
            UserScript(
                id = "video_speed_control",
                name = "视频播放加速支持",
                matchHost = "*",
                scriptCode = """
                    (function() {
                        window.__aeryoSetVideoSpeed = function(rate) {
                            var videos = document.getElementsByTagName('video');
                            for(var i=0; i<videos.length; i++) {
                                videos[i].playbackRate = rate;
                            }
                        };
                    })();
                """.trimIndent()
            )
        )
    }

    fun getScripts(): List<UserScript> = scripts

    fun addScript(script: UserScript) {
        scripts.add(script)
    }

    fun removeScript(id: String) {
        scripts.removeAll { it.id == id }
    }

    fun injectScriptsForUrl(webView: WebView, url: String) {
        scripts.filter { it.isEnabled }.forEach { script ->
            if (script.matchHost == "*" || url.contains(script.matchHost)) {
                webView.evaluateJavascript(script.scriptCode, null)
            }
        }
    }
}
