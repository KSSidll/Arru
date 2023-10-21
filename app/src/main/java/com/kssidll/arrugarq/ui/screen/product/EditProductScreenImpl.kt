package com.kssidll.arrugarq.ui.screen.product


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
import com.kssidll.arrugarq.ui.screen.shared.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.flow.*
import androidx.compose.material3.Surface as Surface1

private val ItemHorizontalPadding: Dp = 20.dp

@Composable
fun EditProductScreenImpl(
    onBack: () -> Unit,
    state: EditProductScreenState,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onProducerAdd: () -> Unit,
    onCategoryAdd: () -> Unit,
) {
    EditScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_product),
        onDelete = onDelete,
        onSubmit = onSubmit,
        submitButtonText = stringResource(id = R.string.item_product_add),
        submitButtonDescription = stringResource(id = R.string.item_product_add_description),
    ) {

        if (state.isProducerSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isProducerSearchDialogExpanded.value = false
                },
                items = state.producers.value.collectAsState(initial = emptyList()).value,
                itemText = { it.name },
                onItemClick = {
                    state.selectedProductProducer.value = it
                    state.isProducerSearchDialogExpanded.value = false
                },
                onAddButtonClick = onProducerAdd,
                addButtonDescription = stringResource(R.string.item_product_producer_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
            )
        } else if (state.isCategorySearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isCategorySearchDialogExpanded.value = false
                },
                items = state.categoriesWithAltNames.value.collectAsState(initial = emptyList()).value,
                onItemClick = {
                    state.selectedProductCategory.value = it?.productCategory
                    state.validateSelectedProductCategory()
                    state.isCategorySearchDialogExpanded.value = false
                },
                itemText = { it.productCategory.name },
                onAddButtonClick = onCategoryAdd,
                addButtonDescription = stringResource(R.string.item_product_category_add_description),
            )
        } else {
            StyledOutlinedTextField(
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
                value = state.selectedProductProducer.value?.name ?: String(),
                onClick = {
                    state.isProducerSearchDialogExpanded.value = true
                },
                label = stringResource(R.string.item_product_producer),
                onAddButtonClick = {
                    onProducerAdd()
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
                value = state.selectedProductCategory.value?.name ?: String(),
                onClick = {
                    state.isCategorySearchDialogExpanded.value = true
                },
                label = stringResource(R.string.item_product_category),
                onAddButtonClick = {
                    onCategoryAdd()
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

data class EditProductScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val selectedProductCategory: MutableState<ProductCategory?> = mutableStateOf(null),
    val selectedProductCategoryError: MutableState<Boolean> = mutableStateOf(false),

    val selectedProductProducer: MutableState<ProductProducer?> = mutableStateOf(null),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),

    val isCategorySearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    val isProducerSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),

    val categoriesWithAltNames: MutableState<Flow<List<ProductCategoryWithAltNames>>> = mutableStateOf(flowOf()),
    val producers: MutableState<Flow<List<ProductProducer>>> = mutableStateOf(flowOf()),
)

/**
 * Validates selectedProductCategory field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun EditProductScreenState.validateSelectedProductCategory(): Boolean {
    return !(selectedProductCategory.value == null).also { selectedProductCategoryError.value = it }
}

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun EditProductScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
fun EditProductScreenState.validate(): Boolean {
    val category = validateSelectedProductCategory()
    val name = validateName()

    return category && name
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
fun EditProductScreenState.extractProductOrNull(): Product? {
    if (!validate()) return null

    return Product(
        categoryId = selectedProductCategory.value!!.id,
        producerId = selectedProductProducer.value?.id,
        name = name.value.trim(),
    )
}

@Preview(
    group = "EditProductScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "EditProductScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun EditProductScreenImplPreview() {
    ArrugarqTheme {
        Surface1(modifier = Modifier.fillMaxSize()) {
            EditProductScreenImpl(
                onBack = {},
                state = EditProductScreenState(),
                onSubmit = {},
                onProducerAdd = {},
                onCategoryAdd = {},
            )
        }
    }
}
