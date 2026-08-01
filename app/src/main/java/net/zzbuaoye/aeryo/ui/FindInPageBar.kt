package net.zzbuaoye.aeryo.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun FindInPageBar(
    onQueryChanged: (String) -> Unit,
    onFindNext: () -> Unit,
    onFindPrevious: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        insideMargin = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = MiuixIcons.Search,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(6.dp)
            )
            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChanged(it)
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onFindNext() }),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            text = "在页面中查找",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 15.sp
                        )
                    }
                    inner()
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.size(4.dp))
            IconButton(onClick = onFindPrevious, modifier = Modifier.size(36.dp)) {
                Icon(MiuixIcons.ExpandLess, contentDescription = "上一个", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onFindNext, modifier = Modifier.size(36.dp)) {
                Icon(MiuixIcons.ExpandMore, contentDescription = "下一个", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                Icon(MiuixIcons.Close, contentDescription = "关闭查找", modifier = Modifier.size(20.dp))
            }
        }
    }
}
