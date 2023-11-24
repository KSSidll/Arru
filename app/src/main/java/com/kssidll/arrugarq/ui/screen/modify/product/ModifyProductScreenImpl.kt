package com.kssidll.arrugarq.ui.screen.modify.product


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.ui.component.dialog.*
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.screen.modify.*
import com.kssidll.arrugarq.ui.theme.*
import androidx.compose.material3.Surface as Surface1

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [Product]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyProductScreenState] instance representing the screen state
 * @param categories Categories that can be set for the product
 * @param producers Producers that can be set for current product
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button, defaults to product add string resource
 * @param onProducerAddButtonClick Callback called when the producer add button is clicked
 * @param onCategoryAddButtonClick Callback called when the category add button is clicked
 * @param onItemProducerLongClick Callback called when the item producer label is long clicked/pressed. Provides producer id as parameter
 * @param onItemCategoryLongClick Callback called when the item category label is long clicked/pressed. Provides category id as parameter
 */
@Composable
fun ModifyProductScreenImpl(
    onBack: () -> Unit,
    state: ModifyProductScreenState,
    categories: List<ProductCategoryWithAltNames>,
    producers: List<ProductProducer>,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_product_add),
    onProducerAddButtonClick: () -> Unit,
    onCategoryAddButtonClick: () -> Unit,
    onItemProducerLongClick: (producerId: Long) -> Unit,
    onItemCategoryLongClick: (categoryId: Long) -> Unit,
) {
    ModifyScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_product),
        onSubmit = onSubmit,
        onDelete = onDelete,
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
                    state.selectedProductProducer.value = it
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
                    state.selectedProductCategory.value = it?.category
                    state.validateSelectedProductCategory()
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
            StyledOutlinedTextField(
                enabled = !state.loadingName.value,
                singleLine = true,
                value = state.name.value,
                onValueChange = {
                    state.name.value = it
                    state.validateName()
                },
                label = {
                    Text(
                        text = stringResource(R.string.item_product)
                    )
                },
                isError = if (state.attemptedToSubmit.value) state.nameError.value else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ItemHorizontalPadding.times(2))
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                enabled = !state.loadingProductProducer.value,
                value = state.selectedProductProducer.value?.name ?: String(),
                onClick = {
                    state.isProducerSearchDialogExpanded.value = true
                },
                label = stringResource(R.string.item_product_producer),
                onAddButtonClick = {
                    onProducerAddButtonClick()
                },
                addButtonDescription = stringResource(R.string.item_product_producer_add_description),
                optional = true,
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(horizontal = ItemHorizontalPadding)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                enabled = !state.loadingProductCategory.value,
                value = state.selectedProductCategory.value?.name ?: String(),
                onClick = {
                    state.isCategorySearchDialogExpanded.value = true
                },
                label = stringResource(R.string.item_product_category),
                onAddButtonClick = {
                    onCategoryAddButtonClick()
                },
                addButtonDescription = stringResource(R.string.item_product_category_add_description),
                error = if (state.attemptedToSubmit.value) state.selectedProductCategoryError.value else false,
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(horizontal = ItemHorizontalPadding)
            )
        }
    }
}

/**
 * Data representing [ModifyProductScreenImpl] screen state
 */
data class ModifyProductScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val selectedProductCategory: MutableState<ProductCategory?> = mutableStateOf(null),
    val selectedProductCategoryError: MutableState<Boolean> = mutableStateOf(false),

    val selectedProductProducer: MutableState<ProductProducer?> = mutableStateOf(null),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),

    val isCategorySearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    val isProducerSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),

    val loadingProductCategory: MutableState<Boolean> = mutableStateOf(false),
    val loadingProductProducer: MutableState<Boolean> = mutableStateOf(false),
    val loadingName: MutableState<Boolean> = mutableStateOf(false),

    val showDeleteWarning: MutableState<Boolean> = mutableStateOf(false),
    val deleteWarningConfirmed: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates selectedProductCategory field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyProductScreenState.validateSelectedProductCategory(): Boolean {
    return !(selectedProductCategory.value == null).also { selectedProductCategoryError.value = it }
}

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyProductScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
fun ModifyProductScreenState.validate(): Boolean {
    val category = validateSelectedProductCategory()
    val name = validateName()

    return category && name
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
fun ModifyProductScreenState.extractProductOrNull(productId: Long = 0): Product? {
    if (!validate()) return null

    return Product(
        id = productId,
        categoryId = selectedProductCategory.value!!.id,
        producerId = selectedProductProducer.value?.id,
        name = name.value.trim(),
    )
}

@Preview(
    group = "ModifyProductScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ModifyProductScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ModifyProductScreenImplPreview() {
    ArrugarqTheme {
        Surface1(modifier = Modifier.fillMaxSize()) {
            ModifyProductScreenImpl(
                onBack = {},
                state = ModifyProductScreenState(),
                categories = emptyList(),
                producers = emptyList(),
                onSubmit = {},
                onProducerAddButtonClick = {},
                onItemProducerLongClick = {},
                onCategoryAddButtonClick = {},
                onItemCategoryLongClick = {},
            )
        }
    }
}
