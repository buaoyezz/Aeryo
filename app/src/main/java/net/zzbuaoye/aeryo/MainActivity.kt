package net.zzbuaoye.aeryo

import android.os.Build
import android.os.Bundle
import android.content.res.Configuration
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
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
        resetLegacyApplicationNightMode()
        val preferences = UserPreferences(applicationContext)
        val startupTheme = loadStartupTheme(preferences)
        val startupInDarkTheme = startupTheme.isDarkTheme(isSystemDarkTheme())
        if (startupInDarkTheme) {
            setTheme(R.style.Theme_Aeryo_Dark)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by preferences.themeMode.collectAsState(initial = startupTheme.themeMode)
            val themePalette by preferences.themePalette.collectAsState(initial = startupTheme.themePalette)
            val themeKeyColor by preferences.themeKeyColor.collectAsState(initial = startupTheme.themeKeyColor)

            LaunchedEffect(themeMode) {
                val darkTheme = StartupTheme(
                    themeMode = themeMode
                ).isDarkTheme(isSystemDarkTheme())
                syncSystemBars(darkTheme)
            }

            val controller = remember(themeMode, themePalette, themeKeyColor) {
                val colorSchemeMode = when (UserPreferences.normalizeThemeMode(themeMode)) {
                    UserPreferences.THEME_MODE_MONET_LIGHT -> ColorSchemeMode.MonetLight
                    UserPreferences.THEME_MODE_MONET_DARK -> ColorSchemeMode.MonetDark
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
                SoftThemeTransition {
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

    private fun loadStartupTheme(preferences: UserPreferences): StartupTheme {
        return runBlocking(Dispatchers.IO) {
            runCatching {
                combine(
                    preferences.themeMode,
                    preferences.themePalette,
                    preferences.themeKeyColor
                ) { themeMode, themePalette, themeKeyColor ->
                    StartupTheme(
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

    private fun resetLegacyApplicationNightMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                getSystemService(android.app.UiModeManager::class.java)
                    ?.setApplicationNightMode(android.app.UiModeManager.MODE_NIGHT_AUTO)
            }
        }
    }

    private fun syncSystemBars(darkTheme: Boolean) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
}

private data class StartupTheme(
    val themeMode: String = UserPreferences.THEME_MODE_SYSTEM,
    val themePalette: String = UserPreferences.THEME_PALETTE_TONAL_SPOT,
    val themeKeyColor: Long = UserPreferences.DEFAULT_THEME_KEY_COLOR
) {
    fun isDarkTheme(systemDarkTheme: Boolean): Boolean = when (UserPreferences.normalizeThemeMode(themeMode)) {
        UserPreferences.THEME_MODE_MONET_DARK -> true
        UserPreferences.THEME_MODE_MONET_LIGHT -> false
        else -> systemDarkTheme
    }
}

@Composable
private fun SoftThemeTransition(content: @Composable () -> Unit) {
    val backgroundColor = MiuixTheme.colorScheme.background
    var previousBackgroundColor by remember { mutableStateOf(backgroundColor) }
    var transitionColor by remember { mutableStateOf(backgroundColor) }
    val overlayAlpha = remember { Animatable(0f) }

    LaunchedEffect(backgroundColor) {
        if (backgroundColor == previousBackgroundColor) return@LaunchedEffect

        transitionColor = previousBackgroundColor
        previousBackgroundColor = backgroundColor
        overlayAlpha.snapTo(0.84f)
        overlayAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 420,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (overlayAlpha.value > 0.001f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(transitionColor.copy(alpha = overlayAlpha.value))
            )
        }
    }
}
