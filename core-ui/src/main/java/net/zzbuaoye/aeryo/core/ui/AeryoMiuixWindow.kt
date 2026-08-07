package net.zzbuaoye.aeryo.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/**
 * Aeryo 的普通功能页始终使用 Miuix 默认的中性窗口底色。
 * Monet 只负责重点色，不把整张页面染成强调色。
 */
@Composable
fun aeryoWindowColor(): Color =
    if (MiuixTheme.colorScheme.background.luminance() < 0.5f) {
        darkColorScheme().surface
    } else {
        lightColorScheme().surface
    }

@Composable
fun aeryoCardColor(): Color = MiuixTheme.colorScheme.surfaceContainer

@Composable
fun aeryoSecondaryTextColor(): Color = MiuixTheme.colorScheme.onSurfaceVariantSummary

/**
 * 与 Miuix 官方示例及 InstallerX-Revived 相同：先绘制实色底，再捕获内容供标题栏模糊。
 */
@Composable
fun rememberAeryoWindowBackdrop(enabled: Boolean = true): LayerBackdrop? {
    if (!enabled || !isRuntimeShaderSupported()) return null
    val surfaceColor = aeryoWindowColor()
    return rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
}

@Composable
fun LayerBackdrop?.aeryoTopBarColor(): Color =
    if (this == null) aeryoWindowColor() else Color.Transparent

fun Modifier.aeryoBackdropSource(backdrop: LayerBackdrop?): Modifier =
    if (backdrop == null) this else then(Modifier.layerBackdrop(backdrop))

@Composable
fun Modifier.aeryoBlurEffect(
    backdrop: LayerBackdrop?,
    enabled: Boolean = true,
    blurRadius: Float = 25f,
    shape: Shape = RectangleShape,
): Modifier {
    if (!enabled || backdrop == null) return this
    return then(
        Modifier.textureBlur(
            backdrop = backdrop,
            shape = shape,
            blurRadius = blurRadius,
            colors = BlurDefaults.blurColors(
                blendColors = listOf(
                    BlendColorEntry(aeryoWindowColor().copy(alpha = 0.8f)),
                ),
            ),
        ),
    )
}

@Composable
fun AeryoWindowSurface(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(aeryoWindowColor()),
    )
}
