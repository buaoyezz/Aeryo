package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.zzbuaoye.aeryo.core.ui.AeryoWindowSurface
import net.zzbuaoye.aeryo.core.ui.aeryoBlurEffect
import net.zzbuaoye.aeryo.core.ui.aeryoCardColor
import net.zzbuaoye.aeryo.core.ui.aeryoSecondaryTextColor
import net.zzbuaoye.aeryo.core.ui.aeryoWindowColor
import net.zzbuaoye.aeryo.core.ui.rememberAeryoWindowBackdrop
import top.yukonga.miuix.kmp.blur.LayerBackdrop

@Composable
internal fun miuixWindowBackgroundColor(): Color = aeryoWindowColor()

@Composable
internal fun miuixWindowCardColor(): Color = aeryoCardColor()

@Composable
internal fun miuixWindowSecondaryTextColor(): Color = aeryoSecondaryTextColor()

@Composable
internal fun rememberMiuixWindowBackdrop(): LayerBackdrop? = rememberAeryoWindowBackdrop()

@Composable
internal fun MiuixBlurredBar(
    backdrop: LayerBackdrop?,
    blurEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.aeryoBlurEffect(
            backdrop = backdrop,
            enabled = blurEnabled,
        ),
    ) {
        content()
    }
}

@Composable
internal fun MiuixWindowSurface(modifier: Modifier = Modifier) {
    AeryoWindowSurface(modifier)
}
