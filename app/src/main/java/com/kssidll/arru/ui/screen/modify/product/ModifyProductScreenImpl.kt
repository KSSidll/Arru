package com.kssidll.arru.ui.screen.modify.product


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.dialog.*
import com.kssidll.arru.ui.component.field.*
import com.kssidll.arru.ui.screen.modify.*
import com.kssidll.arru.ui.theme.*
import kotlinx.coroutines.flow.*
import androidx.compose.material3.Surface as Surface1

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [Product]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyProductScreenState] instance representing the screen state
 * @param categories Categories that can be set for the product
 * @param producers Producers that can be set for current product
 * @param onNewProducerSelected Callback called when a new producer is selected. Provides newly selected producer as parameter
 * @param onNewCategorySelected Callback called when a new category is selected. Provides newly selected category as parameter
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
 * @param onProducerAddButtonClick Callback called when the producer add button is clicked. Provides search value or null as parameter
 * @param onCategoryAddButtonClick Callback called when the category add button is clicked. Provides search value or null as parameter
 * @param onItemProducerLongClick Callback called when the item producer label is long clicked/pressed. Provides producer id as parameter
 * @param onItemCategoryLongClick Callback called when the item category label is long clicked/pressed. Provides category id as parameter
 */
@Composable
fun ModifyProductScreenImpl(
    onBack: () -> Unit,
    state: ModifyProductScreenState,
    categories: Data<List<ProductCategoryWithAltNames>>,
    producers: Data<List<ProductProducer>>,
    onNewProducerSelected: (producer: ProductProducer?) -> Unit,
    onNewCategorySelected: (category: ProductCategory?) -> Unit,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onMerge: ((candidate: Product) -> Unit)? = null,
    mergeCandidates: Flow<Data<List<Product>>> = flowOf(),
    mergeConfirmMessageTemplate: String = String(),
    chosenMergeCandidate: Product? = null,
    onChosenMergeCandidateChange: ((Product?) -> Unit)? = null,
    showMergeConfirmDialog: Boolean = false,
    onShowMergeConfirmDialogChange: ((Boolean) -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_product_add),
    onProducerAddButtonClick: (query: String?) -> Unit,
    onCategoryAddButtonClick: (query: String?) -> Unit,
    onItemProducerLongClick: (producerId: Long) -> Unit,
    onItemCategoryLongClick: (categoryId: Long) -> Unit,
) {
    ModifyScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_product),
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
        deleteWarningMessage = stringResource(id = R.string.item_product_delete_warning_text),
    ) {
        if (state.isProducerSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isProducerSearchDialogExpanded.value = false
                },
                items = producers,
                itemText = { it.name },
                onItemClick = {
                    onNewProducerSelected(it)
                    state.isProducerSearchDialogExpanded.value = false
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isProducerSearchDialogExpanded.value = false
                    onItemProducerLongClick(it.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = onProducerAddButtonClick,
                addButtonDescription = stringResource(R.string.item_product_producer_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
            )
        } else if (state.isCategorySearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isCategorySearchDialogExpanded.value = false
                },
                items = categories,
                onItemClick = {
                    onNewCategorySelected(it?.category)
                    state.isCategorySearchDialogExpanded.value = false
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isCategorySearchDialogExpanded.value = false
                    onItemCategoryLongClick(it.category.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                itemText = { it.category.name },
                onAddButtonClick = onCategoryAddButtonClick,
                addButtonDescription = stringResource(R.string.item_product_category_add_description),
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 500.dp)
            ) {
                StyledOutlinedTextField(
                    enabled = state.name.value.isEnabled(),
                    singleLine = true,
                    value = state.name.value.data ?: String(),
                    onValueChange = {
                        state.name.value = Field.Loaded(it)
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.item_product)
                        )
                    },
                    supportingText = {
                        if (state.attemptedToSubmit.value) {
                            state.name.value.error?.ErrorText()
                        }
                    },
                    isError = if (state.attemptedToSubmit.value) state.name.value.isError() else false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ItemHorizontalPadding.times(2))
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                SearchField(
                    enabled = state.selectedProductProducer.value.isEnabled(),
                    value = state.selectedProductProducer.value.data?.name ?: String(),
                    onClick = {
                        state.isProducerSearchDialogExpanded.value = true
                    },
                    onLongClick = {
                        state.selectedProductProducer.value.data?.let {
                            onItemProducerLongClick(it.id)
                        }
                    },
                    label = stringResource(R.string.item_product_producer),
                    onAddButtonClick = {
                        onProducerAddButtonClick(null)
                    },
                    addButtonDescription = stringResource(R.string.item_product_producer_add_description),
                    optional = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ItemHorizontalPadding)
                )

                Spacer(modifier = Modifier.height(12.dp))

                SearchField(
                    enabled = state.selectedProductCategory.value.isEnabled(),
                    value = state.selectedProductCategory.value.data?.name ?: String(),
                    onClick = {
                        state.isCategorySearchDialogExpanded.value = true
                    },
                    onLongClick = {
                        state.selectedProductCategory.value.data?.let {
                            onItemCategoryLongClick(it.id)
                        }
                    },
                    label = stringResource(R.string.item_product_category),
                    onAddButtonClick = {
                        onCategoryAddButtonClick(null)
                    },
                    addButtonDescription = stringResource(R.string.item_product_category_add_description),
                    supportingText = {
                        if (state.attemptedToSubmit.value) {
                            state.selectedProductCategory.value.error?.ErrorText()
                        }
                    },
                    error = if (state.attemptedToSubmit.value) state.selectedProductCategory.value.isError() else false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ItemHorizontalPadding)
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
fun ModifyProductScreenImplPreview() {
    ArrugarqTheme {
        Surface1(modifier = Modifier.fillMaxSize()) {
            ModifyProductScreenImpl(
                onBack = {},
                state = ModifyProductScreenState(),
                categories = Data.Loading(),
                producers = Data.Loading(),
                onNewProducerSelected = {},
                onNewCategorySelected = {},
                onSubmit = {},
                onProducerAddButtonClick = {},
                onItemProducerLongClick = {},
                onCategoryAddButtonClick = {},
                onItemCategoryLongClick = {},
            )
        }
    }
}
