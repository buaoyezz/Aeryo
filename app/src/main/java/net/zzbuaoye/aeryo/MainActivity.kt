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

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val preferences = remember { UserPreferences(context) }
            val nightModeEnabled by preferences.nightModeEnabled.collectAsState(initial = false)
            
            val controller = remember(nightModeEnabled) {
                ThemeController(
                    colorSchemeMode = if (nightModeEnabled) {
                        ColorSchemeMode.MonetDark
                    } else {
                        ColorSchemeMode.MonetSystem
                    },
                    keyColor = Color(0xFF3482FF)
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
