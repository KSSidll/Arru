package com.kssidll.arru.ui.screen.modify


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.dialog.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*
import kotlinx.coroutines.flow.*

abstract class ModifyScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),
    val showDeleteWarning: MutableState<Boolean> = mutableStateOf(false),
    val deleteWarningConfirmed: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * @param T Type of item, doesn't matter if doesn't support merging
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param title Text displayed on the top app bar
 * @param onSubmit Called to request data submission
 * @param onDelete Called to request a delete operation, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param onMerge Called to request a merge operation. Provides merge candidate as parameter
 * @param mergeCandidates List of candidates for merge operation as flow
 * @param mergeCandidatesTextTransformation Transformation used to determine what to display on the merge candidate item card
 * @param mergeConfirmMessageTemplate Template of a message to show in merge operation confirmation dialog, {value_2} will be replaced with name of merge candidate
 * @param submitButtonText Text displayed in the submit button
 * @param showDeleteWarning Mutable flag that exposes whether a delete warning dialog is shown, optional as it is handled internally by the component, but exposed for state dependent actions
 * @param deleteWarningConfirmed Mutable flag that exposes whether user confirmed the action of deletion in the warning dialog, exposed for state dependant actions
 * @param deleteWarningMessage Text displayed inside of the delete warning dialog
 * @param content Component content, has bottom center alignment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ModifyScreen(
    onBack: () -> Unit,
    title: String,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onMerge: ((candidate: T) -> Unit)? = null,
    mergeCandidates: Flow<List<T>> = flowOf(),
    mergeCandidatesTextTransformation: ((T) -> String)? = null,
    mergeConfirmMessageTemplate: String = String(),
    chosenMergeCandidate: T? = null,
    onChosenMergeCandidateChange: ((T?) -> Unit)? = null,
    showMergeConfirmDialog: Boolean = false,
    onShowMergeConfirmDialogChange: ((Boolean) -> Unit)? = null,
    submitButtonText: String,
    showDeleteWarning: MutableState<Boolean> = remember { mutableStateOf(false) },
    deleteWarningConfirmed: MutableState<Boolean> = remember { mutableStateOf(false) },
    deleteWarningMessage: String = String(),
    content: @Composable ColumnScope.() -> Unit,
) where T: FuzzySearchSource {
    var showMergeSearchDialog by remember {
        mutableStateOf(false)
    }

    Box {
        if (showDeleteWarning.value) {
            DeleteWarningConfirmDialog(
                message = deleteWarningMessage,
                warningConfirmed = deleteWarningConfirmed.value,
                onWarningConfirmedChange = {
                    deleteWarningConfirmed.value = it
                },
                onCancel = {
                    deleteWarningConfirmed.value = false
                    showDeleteWarning.value = false
                },
                onSubmit = {
                    onDelete?.invoke()
                    showDeleteWarning.value = false
                },
            )
        } else if (showMergeSearchDialog) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    showMergeSearchDialog = false
                },
                items = mergeCandidates.collectAsState(initial = emptyList()).value,
                itemText = {
                    mergeCandidatesTextTransformation?.invoke(it)
                        .orEmpty()
                },
                onItemClick = {
                    onChosenMergeCandidateChange?.invoke(it)
                    showMergeSearchDialog = false
                    onShowMergeConfirmDialogChange?.invoke(true)
                },
                onItemClickLabel = stringResource(id = R.string.merge_action_chose_candidate),
                showAddButton = false,
            )
        } else if (showMergeConfirmDialog) {
            MergeConfirmDialog(
                message = mergeConfirmMessageTemplate.replace(
                    "{value_2}",
                    chosenMergeCandidate?.let { mergeCandidatesTextTransformation?.invoke(it) }
                        .orEmpty()
                ),
                onCancel = {
                    onShowMergeConfirmDialogChange?.invoke(false)
                },
                onConfirm = {
                    chosenMergeCandidate?.let { onMerge?.invoke(it) }
                    onShowMergeConfirmDialogChange?.invoke(false)
                }
            )
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
                        if (onMerge != null) {
                            IconButton(
                                onClick = {
                                    showMergeSearchDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Merge,
                                    contentDescription = stringResource(id = R.string.merge_action),
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(27.dp),
                                )
                            }
                        }

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

@PreviewLightDark
@Composable
fun EditScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyScreen<FuzzySearchSource>(
                onBack = {},
                title = "test",
                onSubmit = {},
                onDelete = {},
                onMerge = {},
                submitButtonText = "Submit It",
                content = {},
                mergeCandidatesTextTransformation = { String() },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ModifyScreenNoDeletePreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyScreen<FuzzySearchSource>(
                onBack = {},
                title = "test",
                onSubmit = {},
                submitButtonText = "Submit It",
                content = {},
                mergeCandidatesTextTransformation = { String() },
            )
        }
    }
}
