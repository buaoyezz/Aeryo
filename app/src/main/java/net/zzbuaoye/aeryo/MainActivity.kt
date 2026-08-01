package net.zzbuaoye.aeryo

import android.app.UiModeManager
import android.os.Build
import android.os.Bundle
import android.content.res.Configuration
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import net.zzbuaoye.aeryo.ui.AeryoMainScreen
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = UserPreferences(applicationContext)
        val startupTheme = loadStartupTheme(preferences)
        val startupInDarkTheme = startupTheme.isDarkTheme(isSystemDarkTheme())
        syncApplicationNightMode(startupInDarkTheme)
        if (startupInDarkTheme) {
            setTheme(R.style.Theme_Aeryo_Dark)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nightModeEnabled by preferences.nightModeEnabled.collectAsState(
                initial = startupTheme.nightModeEnabled
            )
            val themeMode by preferences.themeMode.collectAsState(initial = startupTheme.themeMode)
            val themePalette by preferences.themePalette.collectAsState(initial = startupTheme.themePalette)
            val themeKeyColor by preferences.themeKeyColor.collectAsState(initial = startupTheme.themeKeyColor)

            LaunchedEffect(themeMode, nightModeEnabled) {
                val darkTheme = StartupTheme(
                    nightModeEnabled = nightModeEnabled,
                    themeMode = themeMode
                ).isDarkTheme(isSystemDarkTheme())
                syncApplicationNightMode(darkTheme)
            }
            
            val controller = remember(themeMode, themePalette, themeKeyColor, nightModeEnabled) {
                val colorSchemeMode = when {
                    themeMode == UserPreferences.THEME_MODE_LIGHT -> ColorSchemeMode.Light
                    themeMode == UserPreferences.THEME_MODE_DARK -> ColorSchemeMode.Dark
                    themeMode == UserPreferences.THEME_MODE_MONET_LIGHT -> ColorSchemeMode.MonetLight
                    themeMode == UserPreferences.THEME_MODE_MONET_DARK -> ColorSchemeMode.MonetDark
                    nightModeEnabled -> ColorSchemeMode.MonetDark
                    else -> ColorSchemeMode.MonetSystem
                }
                val paletteStyle = when (themePalette) {
                    UserPreferences.THEME_PALETTE_VIBRANT -> ThemePaletteStyle.Vibrant
                    UserPreferences.THEME_PALETTE_EXPRESSIVE -> ThemePaletteStyle.Expressive
                    UserPreferences.THEME_PALETTE_NEUTRAL -> ThemePaletteStyle.Neutral
                    else -> ThemePaletteStyle.TonalSpot
                }
                ThemeController(
                    colorSchemeMode = colorSchemeMode,
                    keyColor = Color(themeKeyColor),
                    paletteStyle = paletteStyle
                )
            }
            MiuixTheme(controller = controller) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MiuixTheme.colorScheme.background
                ) {
                    AeryoMainScreen()
                }
            }
        }
    }

    private fun loadStartupTheme(preferences: UserPreferences): StartupTheme {
        return runBlocking(Dispatchers.IO) {
            runCatching {
                combine(
                    preferences.nightModeEnabled,
                    preferences.themeMode,
                    preferences.themePalette,
                    preferences.themeKeyColor
                ) { nightModeEnabled, themeMode, themePalette, themeKeyColor ->
                    StartupTheme(
                        nightModeEnabled = nightModeEnabled,
                        themeMode = themeMode,
                        themePalette = themePalette,
                        themeKeyColor = themeKeyColor
                    )
                }.first()
            }.getOrElse { StartupTheme() }
        }
    }

    private fun isSystemDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
    }

    private fun syncApplicationNightMode(darkTheme: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val uiModeManager = getSystemService(UiModeManager::class.java) ?: return
            val targetMode = if (darkTheme) UiModeManager.MODE_NIGHT_YES else UiModeManager.MODE_NIGHT_NO
            runCatching {
                uiModeManager.setApplicationNightMode(targetMode)
            }
        }
    }
}

private data class StartupTheme(
    val nightModeEnabled: Boolean = false,
    val themeMode: String = UserPreferences.THEME_MODE_SYSTEM,
    val themePalette: String = UserPreferences.THEME_PALETTE_TONAL_SPOT,
    val themeKeyColor: Long = UserPreferences.DEFAULT_THEME_KEY_COLOR
) {
    fun isDarkTheme(systemDarkTheme: Boolean): Boolean = when (themeMode) {
        UserPreferences.THEME_MODE_DARK,
        UserPreferences.THEME_MODE_MONET_DARK -> true
        UserPreferences.THEME_MODE_LIGHT,
        UserPreferences.THEME_MODE_MONET_LIGHT -> false
        else -> nightModeEnabled || systemDarkTheme
    }
}
