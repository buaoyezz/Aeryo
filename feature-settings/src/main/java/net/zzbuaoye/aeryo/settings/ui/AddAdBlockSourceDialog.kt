package net.zzbuaoye.aeryo.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun AddAdBlockSourceDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onAdd: (name: String, url: String) -> Unit,
    renderInRootScaffold: Boolean = false
) {
    var name by remember(show) { mutableStateOf("") }
    var url by remember(show) { mutableStateOf("") }

    OverlayDialog(
        show = show,
        title = "添加规则订阅源",
        summary = "名称和包含规则的txt文件的URL",
        onDismissRequest = onDismiss,
        renderInRootScaffold = renderInRootScaffold
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("名称 (如: EasyList)", modifier = Modifier.padding(bottom = 4.dp))
            MiuixDialogTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "如: EasyList"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("规则订阅链接 (.txt)", modifier = Modifier.padding(bottom = 4.dp))
            MiuixDialogTextField(
                value = url,
                onValueChange = { url = it },
                placeholder = "https://example.com/rules.txt"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(
                    text = "取消",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                TextButton(
                    text = "添加",
                    onClick = {
                        if (name.isNotBlank() && url.isNotBlank()) {
                            onAdd(name, url)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
