package net.zzbuaoye.aeryo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import net.zzbuaoye.aeryo.settings.data.UserPreferences
import net.zzbuaoye.aeryo.ui.AeryoMainScreen
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val preferences = remember { UserPreferences(context) }
            val nightModeEnabled by preferences.nightModeEnabled.collectAsState(initial = false)
            val themeMode by preferences.themeMode.collectAsState(initial = UserPreferences.THEME_MODE_SYSTEM)
            val themePalette by preferences.themePalette.collectAsState(initial = UserPreferences.THEME_PALETTE_TONAL_SPOT)
            val themeKeyColor by preferences.themeKeyColor.collectAsState(initial = UserPreferences.DEFAULT_THEME_KEY_COLOR)
            
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
}
