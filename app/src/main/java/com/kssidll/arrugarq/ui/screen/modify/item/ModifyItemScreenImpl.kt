package com.kssidll.arrugarq.ui.screen.modify.item


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.component.dialog.*
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.screen.modify.*
import com.kssidll.arrugarq.ui.theme.*
import java.text.*
import java.util.*

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [Item]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyItemScreenState] instance representing the screen state
 * @param shops Shops that can be set for the item
 * @param products Products that can be set for the item
 * @param variants Variants that can be set for current item
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered
 * @param onProductChange Callback called when the product representation in state is changed
 * @param submitButtonText Text displayed in the submit button, defaults to product add string resource
 * @param onProductAddButtonClick Callback called when the product add button is clicked. Provides search value or null as parameter
 * @param onVariantAddButtonClick Callback called when the variant add button is clicked. Provides product id and potentially a search value as parameters
 * @param onShopAddButtonClick Callback called when the shop add button is clicked. Provides search value or null as parameter
 * @param onItemLongClick Callback called when the item is long clicked/pressed. Provides product id as parameter
 * @param onItemVariantLongClick Callback called when the item variant label is long clicked/pressed. Provides variant id as parameter
 * @param onItemShopLongClick Callback called when the item shop label is long clicked/pressed. Provides shop id as parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyItemScreenImpl(
    onBack: () -> Unit,
    state: ModifyItemScreenState,
    shops: List<Shop>,
    products: List<ProductWithAltNames>,
    variants: List<ProductVariant>,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onProductChange: () -> Unit,
    submitButtonText: String = stringResource(id = R.string.item_add),
    onProductAddButtonClick: (query: String?) -> Unit,
    onVariantAddButtonClick: (productId: Long, query: String?) -> Unit,
    onShopAddButtonClick: (query: String?) -> Unit,
    onItemLongClick: (productId: Long) -> Unit,
    onItemVariantLongClick: (variantId: Long) -> Unit,
    onItemShopLongClick: (shopId: Long) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    ModifyScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item),
        onSubmit = onSubmit,
        onDelete = onDelete,
        submitButtonText = submitButtonText,
    ) {
        if (state.isDatePickerDialogExpanded.value) {
            DatePickerDialog(
                onDismissRequest = {
                    state.isDatePickerDialogExpanded.value = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            state.isDatePickerDialogExpanded.value = false
                            state.date.value = Field.Loaded(datePickerState.selectedDateMillis)
                            state.validateDate()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                }
            ) {
                DatePicker(
                    state = datePickerState,
                )
            }
        } else if (state.isShopSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isShopSearchDialogExpanded.value = false
                },
                items = shops,
                itemText = { it.name },
                onItemClick = {
                    state.isShopSearchDialogExpanded.value = false
                    state.selectedShop.value = Field.Loaded(it)
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isShopSearchDialogExpanded.value = false
                    onItemShopLongClick(it.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = onShopAddButtonClick,
                addButtonDescription = stringResource(R.string.item_shop_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
            )
        } else if (state.isProductSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isProductSearchDialogExpanded.value = false
                },
                items = products,
                onItemClick = {
                    state.selectedProduct.value = Field.Loaded(it?.product)
                    state.isProductSearchDialogExpanded.value = false
                    state.validateSelectedProduct()
                    onProductChange()
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isProductSearchDialogExpanded.value = false
                    onItemLongClick(it.product.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                itemText = { it.product.name },
                onAddButtonClick = onProductAddButtonClick,
                addButtonDescription = stringResource(R.string.item_product_add_description),
            )
        } else if (state.isVariantSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isVariantSearchDialogExpanded.value = false
                },
                items = variants,
                itemText = { it.name },
                onItemClick = {
                    state.isVariantSearchDialogExpanded.value = false
                    state.selectedVariant.value = Field.Loaded(it)
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isVariantSearchDialogExpanded.value = false
                    onItemVariantLongClick(it.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = { query ->
                    state.selectedProduct.value.data?.let {
                        onVariantAddButtonClick(
                            it.id,
                            query
                        )
                    }
                },
                addButtonDescription = stringResource(R.string.item_product_variant_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.item_product_variant_default_value),
            )
        }

        val date = state.date.value.data
        SearchField(
            enabled = state.date.value.isEnabled(),
            value = if (date != null) SimpleDateFormat(
                "MMM d, yyyy",
                Locale.getDefault()
            ).format(date) else String(),
            showAddButton = false,
            label = stringResource(R.string.item_date),
            onClick = {
                state.isDatePickerDialogExpanded.value = true
            },
            supportingText = {
                if (state.attemptedToSubmit.value) {
                    state.date.value.error?.errorText()
                }
            },
            error = if (state.attemptedToSubmit.value) state.date.value.isError() else false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding.times(2))
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding.times(2))
        ) {
            StyledOutlinedTextField(
                singleLine = true,
                enabled = state.price.value.isEnabled(),
                value = state.price.value.data ?: String(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = { newValue ->
                    state.price.apply {
                        if (newValue.isBlank()) {
                            value = Field.Loaded(String())
                        } else if (RegexHelper.isFloat(
                                newValue,
                                2
                            )
                        ) {
                            value = Field.Loaded(newValue)
                        }
                    }

                    state.validatePrice()
                },
                label = {
                    Text(
                        text = stringResource(R.string.item_price),
                        fontSize = 16.sp,
                    )
                },
                supportingText = {
                    if (state.attemptedToSubmit.value) {
                        state.price.value.error?.errorText()
                    }
                },
                isError = if (state.attemptedToSubmit.value) state.price.value.isError() else false,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(
                    enabled = state.price.value.isEnabled(),
                    onClick = {
                        if (state.validatePrice()) {
                            val value =
                                state.price.value.data?.let { StringHelper.toDoubleOrNull(it) }
                                    ?: error("Price validation failed, got null instead of float")

                            state.price.value = Field.Loaded("%.2f".format(value.plus(0.5f)))
                        }

                        if (state.price.value.data.isNullOrBlank()) {
                            state.price.value = Field.Loaded("%.2f".format(0f))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(disabledAlpha),
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = stringResource(id = R.string.item_price_increment_by_half),
                    )
                }

                IconButton(
                    enabled = state.price.value.isEnabled(),
                    onClick = {
                        if (state.validatePrice()) {
                            val value =
                                state.price.value.data?.let { StringHelper.toDoubleOrNull(it) }
                                    ?: error("Price validation failed, got null instead of float")

                            state.price.value = Field.Loaded(
                                "%.2f".format(
                                    if (value > 0.5f) value.minus(0.5f) else {
                                        0f
                                    }
                                )
                            )
                        }

                        if (state.price.value.data.isNullOrBlank()) {
                            state.price.value = Field.Loaded("%.2f".format(0f))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(disabledAlpha),
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(id = R.string.item_price_decrement_by_half),
                    )
                }
            }

        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding.times(2))
        ) {
            StyledOutlinedTextField(
                singleLine = true,
                enabled = state.quantity.value.isEnabled(),
                value = state.quantity.value.data ?: String(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = { newValue ->
                    state.quantity.apply {
                        if (newValue.isBlank()) {
                            value = Field.Loaded(String())
                        } else if (RegexHelper.isFloat(
                                newValue,
                                3
                            )
                        ) {
                            value = Field.Loaded(newValue)
                        }
                    }

                    state.validateQuantity()
                },
                label = {
                    Text(
                        text = if (state.selectedVariant.value.data == null)
                            stringResource(R.string.item_product_variant_default_value)
                        else
                            stringResource(R.string.item_quantity),
                        fontSize = 16.sp,
                    )
                },
                supportingText = {
                    if (state.attemptedToSubmit.value) {
                        state.quantity.value.error?.errorText()
                    }
                },
                isError = if (state.attemptedToSubmit.value) state.quantity.value.isError() else false,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(
                    enabled = state.quantity.value.isEnabled(),
                    onClick = {
                        if (state.validateQuantity()) {
                            val value =
                                state.quantity.value.data?.let { StringHelper.toDoubleOrNull(it) }
                                    ?: error("Quantity validation failed, got null instead of float")

                            state.quantity.value = Field.Loaded("%.3f".format(value.plus(1f)))
                        }

                        if (state.quantity.value.data.isNullOrBlank()) {
                            state.quantity.value = Field.Loaded("%.3f".format(0f))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(disabledAlpha),
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = stringResource(id = R.string.item_quantity_increment_by_one),
                    )
                }

                IconButton(
                    enabled = state.quantity.value.isEnabled(),
                    onClick = {
                        if (state.validateQuantity()) {
                            val value =
                                state.quantity.value.data?.let { StringHelper.toDoubleOrNull(it) }
                                    ?: error("Quantity validation failed, got null instead of float")

                            state.quantity.value = Field.Loaded(
                                "%.3f".format(
                                    if (value > 1f) value.minus(1f) else {
                                        0f
                                    }
                                )
                            )
                        }

                        if (state.quantity.value.data.isNullOrBlank()) {
                            state.quantity.value = Field.Loaded("%.3f".format(0f))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(disabledAlpha),
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(id = R.string.item_quantity_decrement_by_one),
                    )
                }
            }

        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        SearchField(
            enabled = state.selectedShop.value.isEnabled(),
            optional = true,
            value = state.selectedShop.value.data?.name ?: String(),
            onClick = {
                state.isShopSearchDialogExpanded.value = true
            },
            onLongClick = {
                state.selectedShop.value.data?.let {
                    onItemShopLongClick(it.id)
                }
            },
            label = stringResource(R.string.item_shop),
            onAddButtonClick = {
                onShopAddButtonClick(null)
            },
            addButtonDescription = stringResource(R.string.item_shop_add_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SearchField(
            enabled = state.selectedProduct.value.isEnabled(),
            value = state.selectedProduct.value.data?.name ?: String(),
            onClick = {
                state.isProductSearchDialogExpanded.value = true
            },
            onLongClick = {
                state.selectedProduct.value.data?.let {
                    onItemLongClick(it.id)
                }
            },
            label = stringResource(R.string.item_product),
            supportingText = {
                if (state.attemptedToSubmit.value) {
                    state.selectedProduct.value.error?.errorText()
                }
            },
            error = if (state.attemptedToSubmit.value) state.selectedProduct.value.isError() else false,
            onAddButtonClick = {
                onProductAddButtonClick(null)
            },
            addButtonDescription = stringResource(R.string.item_product_add_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SearchField(
            enabled = state.selectedProduct.value.data != null && state.selectedVariant.value.isEnabled(),
            value = state.selectedVariant.value.data?.name
                ?: stringResource(R.string.item_product_variant_default_value),
            onClick = {
                state.isVariantSearchDialogExpanded.value = true
            },
            onLongClick = {
                state.selectedVariant.value.data?.let {
                    onItemVariantLongClick(it.id)
                }
            },
            label = stringResource(R.string.item_product_variant),
            onAddButtonClick = {
                state.selectedProduct.value.data?.let {
                    onVariantAddButtonClick(
                        it.id,
                        null
                    )
                }
            },
            addButtonDescription = stringResource(R.string.item_product_variant_add_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )
    }
}

@Preview(
    group = "ModifyItemScreenImpl",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ModifyItemScreenImpl",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ModifyItemScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyItemScreenImpl(
                onBack = {},
                state = ModifyItemScreenState(),
                shops = emptyList(),
                products = emptyList(),
                variants = emptyList(),
                onSubmit = {},
                onShopAddButtonClick = {},
                onItemShopLongClick = {},
                onProductAddButtonClick = {},
                onItemLongClick = {},
                onVariantAddButtonClick = { _, _ -> },
                onItemVariantLongClick = {},
                onProductChange = {},
            )
        }
    }
}
