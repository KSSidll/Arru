package com.kssidll.arru.ui.screen.modify.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.field.SearchField
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.screen.modify.ModifyScreen
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.disabledAlpha

private val ItemHorizontalPadding: Dp = 20.dp

@Composable
fun ModifyItemScreenImpl(
    uiState: ModifyItemUiState,
    onEvent: (event: ModifyItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_add),
) {
    ModifyScreen(
        onBack = { onEvent(ModifyItemEvent.NavigateBack) },
        title = stringResource(id = R.string.item),
        onSubmit = { onEvent(ModifyItemEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyItemEvent.DeleteItem) },
        isDeleteVisible = uiState.isDeleteEnabled,
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
        modifier = modifier,
    ) {
        if (uiState.isProductSearchDialogExpanded) {
            SearchableListDialog(
                onDismissRequest = {
                    onEvent(ModifyItemEvent.SetProductSearchDialogVisibility(false))
                },
                items = uiState.allProducts,
                onItemClick = {
                    onEvent(ModifyItemEvent.SetProductSearchDialogVisibility(false))
                    it?.id?.let { productId -> onEvent(ModifyItemEvent.SelectProduct(productId)) }
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    onEvent(ModifyItemEvent.SetProductSearchDialogVisibility(false))
                    onEvent(ModifyItemEvent.NavigateEditProduct(it.id))
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                itemText = { it.name },
                onAddButtonClick = {},
                addButtonDescription = stringResource(R.string.item_product_add_description),
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        } else if (uiState.isProductVariantSearchDialogExpanded) {
            SearchableListDialog(
                onDismissRequest = {
                    onEvent(ModifyItemEvent.SetProductVariantSearchDialogVisibility(false))
                },
                items = uiState.allProductVariants,
                itemText = { it.name },
                onItemClick = {
                    onEvent(ModifyItemEvent.SetProductVariantSearchDialogVisibility(false))
                    onEvent(ModifyItemEvent.SelectProductVariant(it?.id))
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    onEvent(ModifyItemEvent.SetProductVariantSearchDialogVisibility(false))
                    onEvent(ModifyItemEvent.NavigateEditProductVariant(it.id))
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = { query ->
                    uiState.selectedProduct.data?.let {
                        onEvent(ModifyItemEvent.NavigateAddProductVariant(it.id, query))
                    }
                },
                addButtonDescription =
                    stringResource(R.string.item_product_variant_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.item_product_variant_default_value),
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 500.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
            ) {
                StyledOutlinedTextField(
                    singleLine = true,
                    enabled = uiState.price.isEnabled(),
                    value = uiState.price.data ?: String(),
                    keyboardOptions =
                        KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { onEvent(ModifyItemEvent.SetPrice(it)) },
                    label = { Text(text = stringResource(R.string.item_price), fontSize = 16.sp) },
                    supportingText = { uiState.price.error?.ErrorText() },
                    isError = uiState.price.isError(),
                    modifier = Modifier.weight(1f),
                )

                Column(modifier = Modifier.fillMaxHeight()) {
                    IconButton(
                        enabled = uiState.price.isEnabled(),
                        onClick = { onEvent(ModifyItemEvent.IncrementPrice) },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor =
                                    MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                            ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription =
                                stringResource(id = R.string.item_price_increment_by_half),
                        )
                    }

                    IconButton(
                        enabled = uiState.price.isEnabled(),
                        onClick = { onEvent(ModifyItemEvent.DecrementPrice) },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor =
                                    MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                            ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription =
                                stringResource(id = R.string.item_price_decrement_by_half),
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
            ) {
                StyledOutlinedTextField(
                    singleLine = true,
                    enabled = uiState.quantity.isEnabled(),
                    value = uiState.quantity.data ?: String(),
                    keyboardOptions =
                        KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { onEvent(ModifyItemEvent.SetQuantity(it)) },
                    label = {
                        Text(
                            text =
                                if (uiState.selectedProductVariant.data == null)
                                    stringResource(R.string.item_product_variant_default_value)
                                else stringResource(R.string.item_quantity),
                            fontSize = 16.sp,
                        )
                    },
                    supportingText = { uiState.quantity.error?.ErrorText() },
                    isError = uiState.quantity.isError(),
                    modifier = Modifier.weight(1f),
                )

                Column(modifier = Modifier.fillMaxHeight()) {
                    IconButton(
                        enabled = uiState.quantity.isEnabled(),
                        onClick = { onEvent(ModifyItemEvent.IncrementQuantity) },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor =
                                    MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                            ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription =
                                stringResource(id = R.string.item_quantity_increment_by_one),
                        )
                    }

                    IconButton(
                        enabled = uiState.quantity.isEnabled(),
                        onClick = { onEvent(ModifyItemEvent.DecrementQuantity) },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor =
                                    MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                            ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription =
                                stringResource(id = R.string.item_quantity_decrement_by_one),
                        )
                    }
                }
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                enabled = uiState.selectedProduct.isEnabled(),
                value = uiState.selectedProduct.data?.name ?: String(),
                onClick = {
                    if (uiState.allProducts.isNotEmpty()) {
                        onEvent(ModifyItemEvent.SetProductSearchDialogVisibility(true))
                    } else {
                        onEvent(ModifyItemEvent.NavigateAddProduct(String()))
                    }
                },
                onLongClick = {
                    uiState.selectedProduct.data?.let {
                        onEvent(ModifyItemEvent.NavigateEditProduct(it.id))
                    }
                },
                label = stringResource(R.string.item_product),
                supportingText = { uiState.selectedProduct.error?.ErrorText() },
                error = uiState.selectedProduct.isError(),
                onAddButtonClick = { onEvent(ModifyItemEvent.NavigateAddProduct(String())) },
                addButtonDescription = stringResource(R.string.item_product_add_description),
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )

            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                enabled =
                    uiState.selectedProduct.data != null &&
                        uiState.selectedProductVariant.isEnabled(),
                value =
                    uiState.selectedProductVariant.data?.name
                        ?: stringResource(R.string.item_product_variant_default_value),
                onClick = {
                    uiState.selectedProduct.data?.let {
                        if (uiState.allProductVariants.isNotEmpty()) {
                            onEvent(ModifyItemEvent.SetProductVariantSearchDialogVisibility(true))
                        } else {
                            onEvent(ModifyItemEvent.NavigateAddProductVariant(it.id, String()))
                        }
                    }
                },
                onLongClick = {
                    uiState.selectedProductVariant.data?.let {
                        onEvent(ModifyItemEvent.NavigateEditProductVariant(it.id))
                    }
                },
                label = stringResource(R.string.item_product_variant),
                onAddButtonClick = {
                    uiState.selectedProduct.data?.let {
                        onEvent(ModifyItemEvent.NavigateAddProductVariant(it.id, String()))
                    }
                },
                addButtonDescription =
                    stringResource(R.string.item_product_variant_add_description),
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )
        }
    }
}

@PreviewLightDark
@ExpandedPreviews
@Composable
private fun ModifyItemScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyItemScreenImpl(uiState = ModifyItemUiState(), onEvent = {})
        }
    }
}
