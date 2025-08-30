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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.ui.component.dialog.DeleteWarningConfirmDialog
import com.kssidll.arru.ui.component.dialog.MergeConfirmDialog
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList

abstract class ModifyScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),
    val showDeleteWarning: MutableState<Boolean> = mutableStateOf(false),
    val deleteWarningConfirmed: MutableState<Boolean> = mutableStateOf(false),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ModifyScreen(
    onBack: () -> Unit,
    title: String,
    onSubmit: () -> Unit,
    submitButtonText: String,
    onDelete: () -> Unit,
    isDeleteVisible: Boolean,
    isDeleteWarningMessageVisible: Boolean,
    onDeleteWarningMessageVisibleChange: (Boolean) -> Unit,
    deleteWarningMessage: String,
    isDeleteWarningConfirmed: Boolean,
    onDeleteWarningConfirmedChange: (Boolean) -> Unit,
    onMerge: () -> Unit,
    isMergeVisible: Boolean,
    isMergeSearchDialogVisible: Boolean,
    onMergeSearchDialogVisibleChange: (Boolean) -> Unit,
    mergeSearchDialogCandidateTextTransformation: (T) -> String,
    isMergeConfirmVisible: Boolean,
    onMergeConfirmVisibleChange: (Boolean) -> Unit,
    mergeConfirmMessage: String,
    mergeCandidates: ImmutableList<T>,
    onChosenMergeCandidateChange: (T?) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) where T : FuzzySearchSource {
    Box(modifier = modifier) {
        if (isDeleteWarningMessageVisible) {
            DeleteWarningConfirmDialog(
                message = deleteWarningMessage,
                warningConfirmed = isDeleteWarningConfirmed,
                onWarningConfirmedChange = { onDeleteWarningConfirmedChange(it) },
                onCancel = {
                    onDeleteWarningConfirmedChange(false)
                    onDeleteWarningMessageVisibleChange(false)
                },
                onSubmit = {
                    onDelete()
                    onDeleteWarningMessageVisibleChange(false)
                },
            )
        } else if (isMergeSearchDialogVisible) {
            SearchableListDialog(
                onDismissRequest = { onMergeSearchDialogVisibleChange(false) },
                items = mergeCandidates,
                itemText = { mergeSearchDialogCandidateTextTransformation(it) },
                onItemClick = {
                    onChosenMergeCandidateChange(it)
                    onMergeSearchDialogVisibleChange(false)
                    onMergeConfirmVisibleChange(true)
                },
                onItemClickLabel = stringResource(id = R.string.merge_action_chose_candidate),
                showAddButton = false,
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        } else if (isMergeConfirmVisible) {
            MergeConfirmDialog(
                message = mergeConfirmMessage,
                onCancel = { onMergeConfirmVisibleChange(false) },
                onConfirm = {
                    onMergeConfirmVisibleChange(false)
                    onMerge()
                },
            )
        }

        Scaffold(
            topBar = {
                SecondaryAppBar(
                    onBack = { onBack() },
                    title = { Text(text = title, style = Typography.titleLarge) },
                    actions = {
                        if (isMergeVisible) {
                            IconButton(onClick = { onMergeSearchDialogVisibleChange(true) }) {
                                Icon(
                                    imageVector = Icons.Rounded.Merge,
                                    contentDescription = stringResource(id = R.string.merge_action),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(27.dp),
                                )
                            }
                        }

                        if (isDeleteVisible) {
                            IconButton(onClick = { onDelete() }) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteForever,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(27.dp),
                                )
                            }
                        }
                    },
                )
            }
        ) {
            Box(Modifier.padding(it).consumeWindowInsets(it)) {
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
                boxMaxHeight.minus(
                    SubmitButtonHeight + SubmitButtonMinTopPadding + SubmitButtonMinBottomPadding
                )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier =
                    Modifier.fillMaxWidth()
                        .heightIn(min = minHeight, max = maxHeight)
                        .verticalScroll(rememberScrollState()),
            ) {
                content()

                Spacer(
                    modifier =
                        Modifier.height(SubmitButtonMaxTopPadding - SubmitButtonMinTopPadding)
                )
            }

            Spacer(modifier = Modifier.height(SubmitButtonMinTopPadding))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    onClick = { onSubmit() },
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(SubmitButtonHeight)
                            .padding(
                                start = SubmitButtonHorizontalPadding,
                                end = SubmitButtonHorizontalPadding,
                            ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(text = submitButtonText, style = Typography.titleLarge)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyScreen(
                onBack = {},
                title = "test",
                onSubmit = {},
                submitButtonText = "Submit it",
                onDelete = {},
                isDeleteVisible = false,
                isDeleteWarningMessageVisible = false,
                onDeleteWarningMessageVisibleChange = {},
                deleteWarningMessage = String(),
                isDeleteWarningConfirmed = false,
                onDeleteWarningConfirmedChange = {},
                onMerge = {},
                isMergeVisible = false,
                isMergeSearchDialogVisible = false,
                onMergeSearchDialogVisibleChange = {},
                mergeSearchDialogCandidateTextTransformation = { String() },
                isMergeConfirmVisible = false,
                onMergeConfirmVisibleChange = {},
                mergeConfirmMessage = String(),
                mergeCandidates = emptyImmutableList(),
                onChosenMergeCandidateChange = {},
                content = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ModifyScreenNoDeletePreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyScreen(
                onBack = {},
                title = "test",
                onSubmit = {},
                submitButtonText = "Submit it",
                onDelete = {},
                isDeleteVisible = true,
                isDeleteWarningMessageVisible = false,
                onDeleteWarningMessageVisibleChange = {},
                deleteWarningMessage = String(),
                isDeleteWarningConfirmed = false,
                onDeleteWarningConfirmedChange = {},
                onMerge = {},
                isMergeVisible = true,
                isMergeSearchDialogVisible = false,
                onMergeSearchDialogVisibleChange = {},
                mergeSearchDialogCandidateTextTransformation = { String() },
                isMergeConfirmVisible = false,
                onMergeConfirmVisibleChange = {},
                mergeConfirmMessage = String(),
                mergeCandidates = emptyImmutableList(),
                onChosenMergeCandidateChange = {},
                content = {},
            )
        }
    }
}
