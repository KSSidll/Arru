package com.kssidll.arru.ui.screen.modify.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface as Surface1
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.field.SearchField
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.screen.modify.ModifyScreen
import com.kssidll.arru.ui.theme.ArruTheme
import kotlinx.collections.immutable.toImmutableList

private val ItemHorizontalPadding: Dp = 20.dp

@Composable
fun ModifyProductScreenImpl(
    uiState: ModifyProductUiState,
    onEvent: (event: ModifyProductEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_product_add),
) {
    ModifyScreen(
        onBack = { onEvent(ModifyProductEvent.NavigateBack) },
        title = stringResource(id = R.string.item_product),
        onSubmit = { onEvent(ModifyProductEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyProductEvent.DeleteProduct) },
        isDeleteVisible = uiState.isDeleteVisible,
        isDeleteWarningMessageVisible = uiState.isDangerousDeleteDialogVisible,
        onDeleteWarningMessageVisibleChange = {
            onEvent(ModifyProductEvent.SetDangerousDeleteDialogVisibility(it))
        },
        deleteWarningMessage = stringResource(id = R.string.item_product_delete_warning_text),
        isDeleteWarningConfirmed = uiState.isDangerousDeleteDialogConfirmed,
        onDeleteWarningConfirmedChange = {
            onEvent(ModifyProductEvent.SetDangerousDeleteDialogConfirmation(it))
        },
        onMerge = { onEvent(ModifyProductEvent.MergeProduct(uiState.selectedMergeCandidate)) },
        isMergeVisible = uiState.isMergeVisible,
        isMergeSearchDialogVisible = uiState.isMergeSearchDialogVisible,
        onMergeSearchDialogVisibleChange = {
            onEvent(ModifyProductEvent.SetMergeSearchDialogVisibility(it))
        },
        mergeSearchDialogCandidateTextTransformation = { it.name },
        isMergeConfirmVisible = uiState.isMergeConfirmationDialogVisible,
        onMergeConfirmVisibleChange = {
            onEvent(ModifyProductEvent.SetMergeConfirmationDialogVisibility(it))
        },
        mergeConfirmMessage =
            stringResource(R.string.merge_action_message_template)
                .replace("{value_2", uiState.selectedMergeCandidate?.name ?: "???")
                .replace("{value_1", uiState.currentProduct?.name ?: "???"),
        mergeCandidates =
            uiState.allProducts.filterNot { it.id == uiState.currentProduct?.id }.toImmutableList(),
        onChosenMergeCandidateChange = { onEvent(ModifyProductEvent.SelectMergeCandidate(it)) },
        modifier = modifier,
    ) {
        if (uiState.isProductProducerSearchDialogExpanded) {
            SearchableListDialog(
                onDismissRequest = {
                    onEvent(ModifyProductEvent.SetProductProducerSearchDialogVisibility(false))
                },
                items = uiState.allProductProducers,
                itemText = { it.name },
                onItemClick = {
                    onEvent(ModifyProductEvent.SelectProductProducer(it?.id))
                    onEvent(ModifyProductEvent.SetProductProducerSearchDialogVisibility(false))
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    onEvent(ModifyProductEvent.SetProductProducerSearchDialogVisibility(false))
                    onEvent(ModifyProductEvent.NavigateEditProductProducer(it.id))
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = { onEvent(ModifyProductEvent.NavigateAddProductProducer(it)) },
                addButtonDescription =
                    stringResource(R.string.item_product_producer_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        } else if (uiState.isProductCategorySearchDialogExpanded) {
            SearchableListDialog(
                onDismissRequest = {
                    onEvent(ModifyProductEvent.SetProductCategorySearchDialogVisibility(false))
                },
                items = uiState.allProductCategories,
                onItemClick = {
                    onEvent(ModifyProductEvent.SelectProductCategory(it?.id))
                    onEvent(ModifyProductEvent.SetProductCategorySearchDialogVisibility(false))
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    onEvent(ModifyProductEvent.SetProductCategorySearchDialogVisibility(false))
                    onEvent(ModifyProductEvent.NavigateEditProductCategory(it.id))
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                itemText = { it.name },
                onAddButtonClick = { onEvent(ModifyProductEvent.NavigateAddProductCategory(it)) },
                addButtonDescription =
                    stringResource(R.string.item_product_category_add_description),
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 500.dp),
        ) {
            StyledOutlinedTextField(
                enabled = uiState.name.isEnabled(),
                singleLine = true,
                value = uiState.name.data ?: String(),
                onValueChange = { onEvent(ModifyProductEvent.SetName(it)) },
                label = { Text(text = stringResource(R.string.item_product)) },
                supportingText = { uiState.name.error?.ErrorText() },
                isError = uiState.name.isError(),
                modifier =
                    Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                enabled = uiState.selectedProductProducer.isEnabled(),
                value = uiState.selectedProductProducer.data?.name ?: String(),
                onClick = {
                    if (uiState.allProductProducers.isNotEmpty()) {
                        onEvent(ModifyProductEvent.SetProductProducerSearchDialogVisibility(true))
                    } else {
                        onEvent(ModifyProductEvent.NavigateAddProductProducer(null))
                    }
                },
                onLongClick = {
                    uiState.selectedProductProducer.data?.let {
                        onEvent(ModifyProductEvent.NavigateEditProductProducer(it.id))
                    }
                },
                label = stringResource(R.string.item_product_producer),
                onAddButtonClick = { onEvent(ModifyProductEvent.NavigateAddProductProducer(null)) },
                addButtonDescription =
                    stringResource(R.string.item_product_producer_add_description),
                optional = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )

            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                enabled = uiState.selectedProductCategory.isEnabled(),
                value = uiState.selectedProductCategory.data?.name ?: String(),
                onClick = {
                    if (uiState.allProductCategories.isNotEmpty()) {
                        onEvent(ModifyProductEvent.SetProductCategorySearchDialogVisibility(true))
                    } else {
                        onEvent(ModifyProductEvent.NavigateAddProductProducer(null))
                    }
                },
                onLongClick = {
                    uiState.selectedProductCategory.data?.let {
                        onEvent(ModifyProductEvent.NavigateEditProductCategory(it.id))
                    }
                },
                label = stringResource(R.string.item_product_category),
                onAddButtonClick = { onEvent(ModifyProductEvent.NavigateAddProductCategory(null)) },
                addButtonDescription =
                    stringResource(R.string.item_product_category_add_description),
                supportingText = { uiState.selectedProductCategory.error?.ErrorText() },
                error = uiState.selectedProductCategory.isError(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )
        }
    }
}

@PreviewLightDark
@ExpandedPreviews
@Composable
private fun ModifyProductScreenImplPreview() {
    ArruTheme {
        Surface1(modifier = Modifier.fillMaxSize()) {
            ModifyProductScreenImpl(uiState = ModifyProductUiState(), onEvent = {})
        }
    }
}
