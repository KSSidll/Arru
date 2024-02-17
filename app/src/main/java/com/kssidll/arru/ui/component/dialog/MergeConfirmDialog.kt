package com.kssidll.arru.ui.component.dialog

import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.kssidll.arru.R
import com.kssidll.arru.ui.theme.*

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

@Preview(
    group = "MergeConfirmDialog",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "MergeConfirmDialog",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
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
