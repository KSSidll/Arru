package com.kssidll.arru.ui.screen.modify


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Merge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.ui.component.dialog.DeleteWarningConfirmDialog
import com.kssidll.arru.ui.component.dialog.MergeConfirmDialog
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
    submitButtonText: String,
    onDelete: (() -> Unit)? = null,
    onMerge: ((candidate: T) -> Unit)? = null,
    mergeCandidates: Flow<ImmutableList<T>> = flowOf(),
    mergeCandidatesTextTransformation: ((T) -> String)? = null,
    mergeConfirmMessageTemplate: String = String(),
    chosenMergeCandidate: T? = null,
    onChosenMergeCandidateChange: ((T?) -> Unit)? = null,
    showMergeConfirmDialog: Boolean = false,
    onShowMergeConfirmDialogChange: ((Boolean) -> Unit)? = null,
    showDeleteWarning: MutableState<Boolean> = remember { mutableStateOf(false) },
    deleteWarningConfirmed: MutableState<Boolean> = remember { mutableStateOf(false) },
    deleteWarningMessage: String = String(),
    content: @Composable ColumnScope.() -> Unit
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
            SearchableListDialog(
                onDismissRequest = {
                    showMergeSearchDialog = false
                },
                items = mergeCandidates.collectAsState(initial = emptyList<T>().toImmutableList()).value,
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
                calculateScore = { item, query ->
                    item.fuzzyScore(query)
                }
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
                                    tint = MaterialTheme.colorScheme.primary,
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
            Box(
                Modifier
                    .padding(it)
                    .consumeWindowInsets(it)
            ) {
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
private fun EditScreenPreview() {
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
private fun ModifyScreenNoDeletePreview() {
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
