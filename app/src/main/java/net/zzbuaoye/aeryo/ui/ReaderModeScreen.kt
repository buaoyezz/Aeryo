package net.zzbuaoye.aeryo.ui

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Volume
import compose.icons.tablericons.AdjustmentsHorizontal
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.Locale

enum class ReaderTheme(
    val title: String,
    val backgroundColor: Color,
    val textColor: Color,
    val cardColor: Color
) {
    SEPIA("复古纸张", Color(0xFFF7F1E3), Color(0xFF33271E), Color(0xFFEFE8D6)),
    EYE_GREEN("护眼豆沙", Color(0xFFE3EDE3), Color(0xFF1E3320), Color(0xFFD6E4D6)),
    DARK("纯黑夜间", Color(0xFF121212), Color(0xFFDCDCDC), Color(0xFF1E1E1E)),
    LIGHT("简约纯白", Color(0xFFFFFFFF), Color(0xFF1A1A1A), Color(0xFFF2F2F2))
}

@Composable
fun ReaderModeScreen(
    title: String,
    contentHtml: String,
    plainText: String,
    wordCount: Int,
    url: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedTheme by remember { mutableStateOf(ReaderTheme.SEPIA) }
    var fontSizeSp by remember { mutableFloatStateOf(18f) }
    var showFormatPanel by remember { mutableStateOf(false) }

    // TTS Audio State
    var isTtsPlaying by remember { mutableStateOf(false) }
    var ttsEngine: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        var speech: TextToSpeech? = null
        speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                speech?.language = Locale.CHINESE
                ttsEngine = speech
                isTtsReady = true
            }
        }
        speech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isTtsPlaying = true
            }
            override fun onDone(utteranceId: String?) {
                isTtsPlaying = false
            }
            override fun onError(utteranceId: String?) {
                isTtsPlaying = false
            }
        })

        onDispose {
            speech?.stop()
            speech?.shutdown()
        }
    }

    BackHandler {
        ttsEngine?.stop()
        onBack()
    }

    val paragraphs = remember(plainText) {
        plainText.split("\n\n", "\n")
            .map { it.trim() }
            .filter { it.length > 2 }
    }

    val estReadingMinutes = remember(wordCount) {
        (wordCount / 380).coerceAtLeast(1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(selectedTheme.backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        ttsEngine?.stop()
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = MiuixIcons.Back,
                        contentDescription = "返回",
                        tint = selectedTheme.textColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TTS Toggle Button
                    IconButton(
                        onClick = {
                            if (!isTtsReady || ttsEngine == null) {
                                Toast.makeText(context, "TTS 语音引擎正在初始化...", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            if (isTtsPlaying) {
                                ttsEngine?.stop()
                                isTtsPlaying = false
                            } else {
                                val textToRead = plainText.take(4000)
                                if (textToRead.isBlank()) {
                                    Toast.makeText(context, "未找到可朗读的正文", Toast.LENGTH_SHORT).show()
                                } else {
                                    ttsEngine?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "aeryo_reader_tts")
                                    isTtsPlaying = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isTtsPlaying) TablerIcons.PlayerPause else TablerIcons.Volume,
                            contentDescription = "语音朗读",
                            tint = if (isTtsPlaying) MiuixTheme.colorScheme.primary else selectedTheme.textColor
                        )
                    }

                    // Format Panel Toggle
                    IconButton(
                        onClick = { showFormatPanel = !showFormatPanel }
                    ) {
                        Icon(
                            imageVector = TablerIcons.AdjustmentsHorizontal,
                            contentDescription = "阅读排版",
                            tint = selectedTheme.textColor
                        )
                    }
                }
            }

            // Article Content List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                item {
                    // Title
                    Text(
                        text = title.ifBlank { "阅读模式" },
                        fontSize = (fontSizeSp + 6f).sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedTheme.textColor,
                        lineHeight = (fontSizeSp + 12f).sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Meta Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(selectedTheme.cardColor)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "约 ${estReadingMinutes} 分钟",
                                fontSize = 11.5.sp,
                                color = selectedTheme.textColor.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (wordCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(selectedTheme.cardColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$wordCount 字",
                                    fontSize = 11.5.sp,
                                    color = selectedTheme.textColor.copy(alpha = 0.75f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Paragraphs
                if (paragraphs.isEmpty()) {
                    item {
                        Text(
                            text = "正在解析文章内容，请稍候...",
                            fontSize = fontSizeSp.sp,
                            color = selectedTheme.textColor.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    items(paragraphs.size) { index ->
                        val paragraph = paragraphs[index]
                        Text(
                            text = paragraph,
                            fontSize = fontSizeSp.sp,
                            color = selectedTheme.textColor,
                            lineHeight = (fontSizeSp * 1.75f).sp,
                            modifier = Modifier.padding(bottom = (fontSizeSp * 0.9f).dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }

        // Floating Format Settings Panel
        AnimatedVisibility(
            visible = showFormatPanel,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Theme Selector
                    Text(
                        text = "主题背景",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiuixTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReaderTheme.entries.forEach { theme ->
                            val isSelected = selectedTheme == theme
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(theme.backgroundColor)
                                    .clickable { selectedTheme = theme }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = theme.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = theme.textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Font Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "字体大小",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${fontSizeSp.toInt()} sp",
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "A-", fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        Slider(
                            value = fontSizeSp,
                            onValueChange = { fontSizeSp = it },
                            valueRange = 14f..28f,
                            modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
                        )
                        Text(text = "A+", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MiuixTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
