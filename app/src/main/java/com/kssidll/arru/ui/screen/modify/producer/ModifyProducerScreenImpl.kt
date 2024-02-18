package com.kssidll.arru.ui.screen.modify.producer


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.field.*
import com.kssidll.arru.ui.screen.modify.*
import com.kssidll.arru.ui.theme.*
import kotlinx.coroutines.flow.*

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [ProductProducer]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyProducerScreenState] instance representing the screen state
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param onMerge Callback called when the merge action is triggered. Provides merge candidate as parameter. Setting to null will hide merge action
 * @param mergeCandidates List of potential candidates for merge operation
 * @param mergeConfirmMessageTemplate Template of a message to show in merge operation confirmation dialog, {value_2} will be replaced with name of merge candidate
 * @param chosenMergeCandidate Currently chosen merge candidate if any
 * @param onChosenMergeCandidateChange Callback called when the [chosenMergeCandidate] should change. Provides candidate as Parameter
 * @param showMergeConfirmDialog Whether to show the merge confirmation dialog
 * @param onShowMergeConfirmDialogChange Callback called when the [showMergeConfirmDialog] flag should change. Provides new flag value as parameter
 * @param submitButtonText Text displayed in the submit button, defaults to product add string resource
 */
@Composable
fun ModifyProducerScreenImpl(
    onBack: () -> Unit,
    state: ModifyProducerScreenState,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onMerge: ((candidate: ProductProducer) -> Unit)? = null,
    mergeCandidates: Flow<List<ProductProducer>> = flowOf(),
    mergeConfirmMessageTemplate: String = String(),
    chosenMergeCandidate: ProductProducer? = null,
    onChosenMergeCandidateChange: ((ProductProducer?) -> Unit)? = null,
    showMergeConfirmDialog: Boolean = false,
    onShowMergeConfirmDialogChange: ((Boolean) -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_product_producer_add),
) {
    // TODO add adaptive layout handling
    ModifyScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_product_producer),
        onSubmit = onSubmit,
        onDelete = onDelete,
        onMerge = onMerge,
        mergeCandidates = mergeCandidates,
        mergeCandidatesTextTransformation = { it.name },
        mergeConfirmMessageTemplate = mergeConfirmMessageTemplate,
        chosenMergeCandidate = chosenMergeCandidate,
        onChosenMergeCandidateChange = onChosenMergeCandidateChange,
        showMergeConfirmDialog = showMergeConfirmDialog,
        onShowMergeConfirmDialogChange = onShowMergeConfirmDialogChange,
        submitButtonText = submitButtonText,
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage = stringResource(id = R.string.item_product_producer_delete_warning_text),
    ) {
        StyledOutlinedTextField(
            enabled = state.name.value.isEnabled(),
            singleLine = true,
            value = state.name.value.data ?: String(),
            onValueChange = {
                state.name.value = Field.Loaded(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit()
                }
            ),
            label = {
                Text(
                    text = stringResource(R.string.item_product_producer),
                )
            },
            supportingText = {
                if (state.attemptedToSubmit.value) {
                    state.name.value.error?.errorText()
                }
            },
            isError = if (state.attemptedToSubmit.value) state.name.value.isError() else false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )
    }
}

@PreviewLightDark
@Composable
fun ModifyProducerScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyProducerScreenImpl(
                onBack = {},
                state = ModifyProducerScreenState(),
                onSubmit = {},
            )
        }
    }
}
