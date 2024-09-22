package com.kssidll.arru.ui.component.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kssidll.arru.R
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

@Composable
fun MergeConfirmDialog(
    message: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            Row {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        text = stringResource(id = R.string.merge_action_cancel),
                        style = Typography.bodyLarge
                    )
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        text = stringResource(id = R.string.merge_action_confirm),
                        style = Typography.bodyLarge
                    )
                }

            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.merge_action),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
            )
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        modifier = Modifier
            .width(360.dp)
            .heightIn(min = 200.dp)
    )
}

@PreviewLightDark
@Composable
private fun MergeConfirmDialogPreview() {
    ArrugarqTheme {
        Surface {
            MergeConfirmDialog(
                message = "test",
                onCancel = {},
                onConfirm = {},
            )
        }
    }
}
