package com.kssidll.arru.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.kssidll.arru.ui.theme.disabledAlpha

/**
 * @param message Message to show in the dialog
 * @param warningConfirmed Whether the warning has been confirmed
 * @param onWarningConfirmedChange Callback called when the [warningConfirmed] is to be changed. Provides new [warningConfirmed] state as parameter
 * @param onCancel Callback called when the dialog is calcelled
 * @param onSubmit Callback called when the dialog is submitted
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteWarningConfirmDialog(
    message: String,
    warningConfirmed: Boolean,
    onWarningConfirmedChange: (newState: Boolean) -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = {},
        modifier = Modifier
            .width(360.dp)
            .heightIn(min = 200.dp)
    ) {
        Surface(
            shape = ShapeDefaults.ExtraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = message,
                    style = Typography.bodyLarge,
                    modifier = Modifier.weight(1F)
                )

                val warningConfirmationInteractionSource =
                    remember { MutableInteractionSource() }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = warningConfirmationInteractionSource,
                            indication = null
                        ) {
                            onWarningConfirmedChange(warningConfirmed.not())
                        }
                ) {
                    Checkbox(
                        checked = warningConfirmed,
                        onCheckedChange = {
                            onWarningConfirmedChange(warningConfirmed.not())
                        },
                        interactionSource = warningConfirmationInteractionSource,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                    )

                    Text(
                        text = stringResource(id = com.kssidll.arru.R.string.destructive_action_confirmation_text),
                        style = Typography.bodyLarge,
                    )
                }

                Row {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = com.kssidll.arru.R.string.destructive_action_cancel),
                            style = Typography.bodyLarge,
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        enabled = warningConfirmed,
                        onClick = onSubmit,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                disabledAlpha
                            ),
                            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                                disabledAlpha
                            ),
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = com.kssidll.arru.R.string.destructive_action_confirm),
                            style = Typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DeleteWarningConfirmDialogPreview() {
    ArrugarqTheme {
        DeleteWarningConfirmDialog(
            message = "test",
            warningConfirmed = true,
            onWarningConfirmedChange = {},
            onCancel = {},
            onSubmit = {},
        )
    }
}
