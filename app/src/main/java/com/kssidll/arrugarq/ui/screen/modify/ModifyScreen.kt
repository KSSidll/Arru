package com.kssidll.arrugarq.ui.screen.modify


import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*

/**
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param title Text displayed on the top app bar
 * @param onSubmit Called to request data submission
 * @param onDelete Called to request a delete operation, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button
 * @param showDeleteWarning Mutable flag that exposes whether a delete warning dialog is shown, optional as it is handled internally by the component, but exposed for state dependent actions
 * @param deleteWarningConfirmed Mutable flag that exposes whether user confirmed the action of deletion in the warning dialog, exposed for state dependant actions
 * @param deleteWarningMessage Text displayed inside of the delete warning dialog
 * @param content Component content, has bottom center alignment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyScreen(
    onBack: () -> Unit,
    title: String,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String,
    showDeleteWarning: MutableState<Boolean> = remember { mutableStateOf(false) },
    deleteWarningConfirmed: MutableState<Boolean> = remember { mutableStateOf(false) },
    deleteWarningMessage: String = String(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Box {
        if (showDeleteWarning.value) {
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
                            text = deleteWarningMessage,
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
                                    deleteWarningConfirmed.value = !deleteWarningConfirmed.value
                                }
                        ) {
                            Checkbox(
                                checked = deleteWarningConfirmed.value,
                                onCheckedChange = {
                                    deleteWarningConfirmed.value = !deleteWarningConfirmed.value
                                },
                                interactionSource = warningConfirmationInteractionSource,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                            )

                            Text(
                                text = stringResource(id = R.string.destructive_action_confirmation_text),
                                style = Typography.bodyLarge,
                            )
                        }

                        Row {
                            Button(
                                onClick = {
                                    showDeleteWarning.value = false
                                    deleteWarningConfirmed.value = false
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.destructive_action_cancel),
                                    style = Typography.bodyLarge,
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                enabled = deleteWarningConfirmed.value,
                                onClick = {
                                    onDelete?.invoke()
                                    showDeleteWarning.value = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(disabledAlpha),
                                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(disabledAlpha),
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.destructive_action_confirm),
                                    style = Typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                SecondaryAppBar(
                    onBack = {
                        onBack()
                    },
                    title = {
                        Text(
                            text = title,
                            style = Typography.titleLarge,
                        )
                    },
                    actions = {
                        if (onDelete != null) {
                            IconButton(
                                onClick = {
                                    onDelete()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteForever,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(27.dp),
                                )
                            }
                        }
                    }
                )
            }
        ) {
            Box(Modifier.padding(it)) {
                EditScreenContent(
                    onSubmit = onSubmit,
                    submitButtonText = submitButtonText,
                    content = content,
                )
            }
        }
    }
}

private val SubmitButtonHeight: Dp = 70.dp
private val SubmitButtonMaxBottomPadding: Dp = 150.dp
private val SubmitButtonMinBottomPadding: Dp = 6.dp
private val SubmitButtonMaxTopPadding: Dp = 20.dp
private val SubmitButtonMinTopPadding: Dp = 14.dp
private val SubmitButtonHorizontalPadding: Dp = 20.dp

@Composable
private fun EditScreenContent(
    onSubmit: () -> Unit,
    submitButtonText: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints {
        val boxMaxHeight = maxHeight

        Column {
            val minHeight = boxMaxHeight.minus(SubmitButtonHeight + SubmitButtonMaxBottomPadding)
            val maxHeight =
                boxMaxHeight.minus(SubmitButtonHeight + SubmitButtonMinTopPadding + SubmitButtonMinBottomPadding)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = minHeight,
                        max = maxHeight
                    )
                    .verticalScroll(rememberScrollState())
            ) {

                content()

                Spacer(modifier = Modifier.height(SubmitButtonMaxTopPadding - SubmitButtonMinTopPadding))
            }

            Spacer(modifier = Modifier.height(SubmitButtonMinTopPadding))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = {
                        onSubmit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SubmitButtonHeight)
                        .padding(
                            start = SubmitButtonHorizontalPadding,
                            end = SubmitButtonHorizontalPadding,
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = submitButtonText,
                        style = Typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Preview(
    group = "EditScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "EditScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun EditScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyScreen(
                onBack = {},
                title = "test",
                onSubmit = {},
                onDelete = {},
                submitButtonText = "Submit It",
                content = {},
            )
        }
    }
}

@Preview(
    group = "ModifyScreen",
    name = "Dark No Delete",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ModifyScreen",
    name = "Light No Delete",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ModifyScreenNoDeletePreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyScreen(
                onBack = {},
                title = "test",
                onSubmit = {},
                submitButtonText = "Submit It",
                content = {},
            )
        }
    }
}
