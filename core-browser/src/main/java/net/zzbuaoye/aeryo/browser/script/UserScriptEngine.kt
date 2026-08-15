package net.zzbuaoye.aeryo.browser.script

import android.webkit.WebView
import org.json.JSONObject
import org.json.JSONTokener

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
        // 默认内置高效脚本：网页视频倍速控制、播放辅助、跨 DOM 视频控制等
        scripts.add(
            UserScript(
                id = "video_speed_control",
                name = "视频播放与倍速控制引擎",
                matchHost = "*",
                scriptCode = """
                    (function() {
                        if (window.__aeryoVideoEngineLoaded) return;
                        window.__aeryoVideoEngineLoaded = true;
                        window.__aeryoCurrentPlaybackRate = 1.0;
                        window.__aeryoPreBoostPlaybackRate = 1.0;

                        function applySpeedToAllVideos(rate) {
                            window.__aeryoCurrentPlaybackRate = rate;
                            var videos = document.querySelectorAll('video');
                            for (var i = 0; i < videos.length; i++) {
                                try {
                                    videos[i].playbackRate = rate;
                                    videos[i].defaultPlaybackRate = rate;
                                } catch (_) {}
                            }
                        }

                        window.__aeryoSetVideoSpeed = function(rate) {
                            window.__aeryoPreBoostPlaybackRate = rate;
                            applySpeedToAllVideos(rate);
                        };

                        window.__aeryoBoostSpeed = function(boostRate) {
                            var target = boostRate || 2.0;
                            var videos = document.querySelectorAll('video');
                            for (var i = 0; i < videos.length; i++) {
                                try {
                                    videos[i].playbackRate = target;
                                } catch (_) {}
                            }
                        };

                        window.__aeryoRestoreSpeed = function() {
                            applySpeedToAllVideos(window.__aeryoPreBoostPlaybackRate || 1.0);
                        };

                        window.__aeryoTogglePlay = function() {
                            var videos = document.querySelectorAll('video');
                            if (videos.length > 0) {
                                var v = videos[0];
                                if (v.paused) {
                                    v.play();
                                } else {
                                    v.pause();
                                }
                            }
                        };

                        window.__aeryoSeekVideo = function(offsetSeconds) {
                            var videos = document.querySelectorAll('video');
                            if (videos.length > 0) {
                                var v = videos[0];
                                v.currentTime = Math.max(0, Math.min(v.duration || Infinity, v.currentTime + offsetSeconds));
                            }
                        };

                        // 监听后续动态插入页面的 video 元素，自动应用当前倍速
                        try {
                            var observer = new MutationObserver(function(mutations) {
                                if (window.__aeryoCurrentPlaybackRate !== 1.0) {
                                    var videos = document.querySelectorAll('video');
                                    for (var i = 0; i < videos.length; i++) {
                                        if (videos[i].playbackRate !== window.__aeryoCurrentPlaybackRate) {
                                            videos[i].playbackRate = window.__aeryoCurrentPlaybackRate;
                                        }
                                    }
                                }
                            });
                            observer.observe(document.documentElement || document.body, { childList: true, subtree: true });
                        } catch (_) {}
                    })();
                """.trimIndent()
            )
        )

        scripts.add(
            UserScript(
                id = "page_visibility_spoof",
                name = "后台播放与防暂停反劫持",
                matchHost = "*",
                scriptCode = """
                    (function() {
                        if (window.__aeryoVisibilitySpoofLoaded) return;
                        window.__aeryoVisibilitySpoofLoaded = true;

                        try {
                            Object.defineProperty(document, 'hidden', {
                                get: function() { return false; },
                                configurable: true
                            });
                            Object.defineProperty(document, 'visibilityState', {
                                get: function() { return 'visible'; },
                                configurable: true
                            });
                            Object.defineProperty(document, 'webkitHidden', {
                                get: function() { return false; },
                                configurable: true
                            });
                            Object.defineProperty(document, 'webkitVisibilityState', {
                                get: function() { return 'visible'; },
                                configurable: true
                            });
                        } catch (_) {}

                        var blockEvent = function(e) {
                            e.stopImmediatePropagation();
                        };
                        window.addEventListener('visibilitychange', blockEvent, true);
                        document.addEventListener('visibilitychange', blockEvent, true);
                        window.addEventListener('webkitvisibilitychange', blockEvent, true);
                        document.addEventListener('webkitvisibilitychange', blockEvent, true);

                        if (typeof AudioContext !== 'undefined') {
                            try {
                                AudioContext.prototype.suspend = function() {
                                    return Promise.resolve();
                                };
                            } catch (_) {}
                        }
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

    fun setScriptEnabled(id: String, isEnabled: Boolean) {
        val index = scripts.indexOfFirst { it.id == id }
        if (index != -1) {
            scripts[index] = scripts[index].copy(isEnabled = isEnabled)
        }
    }

    fun injectScriptsForUrl(webView: WebView, url: String) {
        scripts.filter { it.isEnabled }.forEach { script ->
            if (script.matchHost == "*" || url.contains(script.matchHost)) {
                webView.evaluateJavascript(script.scriptCode, null)
            }
        }
    }

    fun applySmartDarkMode(webView: WebView, enable: Boolean) {
        val script = if (enable) {
            """
                (function() {
                    var styleId = '__aeryo_smart_dark_style';

                    function isPageAlreadyDark() {
                        try {
                            if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
                                return true;
                            }
                            var docEl = document.documentElement;
                            if (docEl) {
                                var cs = window.getComputedStyle(docEl).colorScheme;
                                if (cs && cs.includes('dark')) return true;
                                var theme = (docEl.getAttribute('data-theme') || docEl.getAttribute('theme') || '').toLowerCase();
                                if (theme.includes('dark') || docEl.classList.contains('dark') || docEl.classList.contains('night')) return true;
                            }
                            var body = document.body;
                            if (body) {
                                var bTheme = (body.getAttribute('data-theme') || body.getAttribute('theme') || '').toLowerCase();
                                if (bTheme.includes('dark') || body.classList.contains('dark') || body.classList.contains('night')) return true;
                                var bodyStyle = window.getComputedStyle(body);
                                var bg = bodyStyle.backgroundColor;
                                if (bg && bg !== 'transparent' && bg !== 'rgba(0, 0, 0, 0)') {
                                    var match = bg.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)/);
                                    if (match) {
                                        var r = parseInt(match[1], 10);
                                        var g = parseInt(match[2], 10);
                                        var b = parseInt(match[3], 10);
                                        var lum = 0.2126 * (r / 255) + 0.7152 * (g / 255) + 0.0722 * (b / 255);
                                        if (lum < 0.40) return true;
                                    }
                                }
                            }
                        } catch(e) {}
                        return false;
                    }

                    function updateSmartDark() {
                        var existing = document.getElementById(styleId);
                        if (isPageAlreadyDark()) {
                            if (existing) existing.remove();
                            return;
                        }
                        if (!existing) {
                            var style = document.createElement('style');
                            style.id = styleId;
                            style.type = 'text/css';
                            style.textContent = `
                                :root {
                                    color-scheme: dark !important;
                                }
                                html, body {
                                    background-color: #121214 !important;
                                    color: #e2e2e6 !important;
                                }
                            `;
                            (document.head || document.documentElement).appendChild(style);
                        }
                    }

                    updateSmartDark();
                    if (!window.__aeryoDarkObserver) {
                        window.__aeryoDarkObserver = new MutationObserver(function() {
                            updateSmartDark();
                        });
                        window.__aeryoDarkObserver.observe(document.documentElement, {
                            attributes: true,
                            attributeFilter: ['class', 'style', 'data-theme', 'theme']
                        });
                    }
                })();
            """.trimIndent()
        } else {
            """
                (function() {
                    var el = document.getElementById('__aeryo_smart_dark_style');
                    if (el) el.remove();
                    if (window.__aeryoDarkObserver) {
                        window.__aeryoDarkObserver.disconnect();
                        window.__aeryoDarkObserver = null;
                    }
                })();
            """.trimIndent()
        }
        webView.evaluateJavascript(script, null)
    }

    fun extractArticleContent(
        webView: WebView,
        callback: (title: String, contentHtml: String, plainText: String, wordCount: Int) -> Unit
    ) {
        val extractScript = """
            (function() {
                try {
                    var articleEl = document.querySelector('article') ||
                                    document.querySelector('main') ||
                                    document.querySelector('.article-content') ||
                                    document.querySelector('.post-content') ||
                                    document.querySelector('#article-content') ||
                                    document.querySelector('#content') ||
                                    document.querySelector('.content') ||
                                    document.body;

                    var clone = articleEl.cloneNode(true);

                    var toRemove = clone.querySelectorAll('script, style, nav, header, footer, iframe, aside, .ad, .advert, .comments, .sidebar, [role="banner"], [role="navigation"]');
                    for (var i = 0; i < toRemove.length; i++) {
                        toRemove[i].remove();
                    }

                    var title = document.title || '';
                    var h1 = document.querySelector('h1');
                    if (h1 && h1.innerText) {
                        title = h1.innerText.trim();
                    }

                    var contentHtml = clone.innerHTML;
                    var text = clone.innerText || clone.textContent || '';
                    var wordCount = text.replace(/\s+/g, '').length;

                    return JSON.stringify({
                        title: title,
                        contentHtml: contentHtml,
                        plainText: text.substring(0, 8000),
                        wordCount: wordCount
                    });
                } catch(e) {
                    return JSON.stringify({
                        title: document.title || '',
                        contentHtml: document.body ? document.body.innerHTML : '',
                        plainText: document.body ? (document.body.innerText || '') : '',
                        wordCount: 0
                    });
                }
            })();
        """.trimIndent()

        webView.evaluateJavascript(extractScript) { resultJson ->
            if (resultJson != null && resultJson != "null") {
                try {
                    val rawJson = if (resultJson.startsWith("\"") && resultJson.endsWith("\"")) {
                        JSONTokener(resultJson).nextValue() as String
                    } else {
                        resultJson
                    }
                    val parsed = JSONObject(rawJson)
                    val title = parsed.optString("title", "")
                    val contentHtml = parsed.optString("contentHtml", "")
                    val plainText = parsed.optString("plainText", "")
                    val wordCount = parsed.optInt("wordCount", 0)
                    callback(title, contentHtml, plainText, wordCount)
                } catch (e: Exception) {
                    callback(webView.title.orEmpty(), "", "", 0)
                }
            } else {
                callback(webView.title.orEmpty(), "", "", 0)
            }
        }
    }

    fun scrollToTop(webView: WebView) {
        webView.evaluateJavascript("window.scrollTo({ top: 0, behavior: 'smooth' });", null)
    }

    fun scrollToBottom(webView: WebView) {
        webView.evaluateJavascript("window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });", null)
    }
}
