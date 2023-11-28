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
 * @param onProductAddButtonClick Callback called when the product add button is clicked
 * @param onVariantAddButtonClick Callback called when the variant add button is clicked. Provides product id as parameter
 * @param onShopAddButtonClick Callback called when the shop add button is clicked
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
    onProductAddButtonClick: () -> Unit,
    onVariantAddButtonClick: (productId: Long) -> Unit,
    onShopAddButtonClick: () -> Unit,
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
                            state.date.value = datePickerState.selectedDateMillis
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
                    state.selectedShop.value = it
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
                    state.selectedProduct.value = it?.product
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
                    state.selectedVariant.value = it
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isVariantSearchDialogExpanded.value = false
                    onItemVariantLongClick(it.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = {
                    state.selectedProduct.value?.let {
                        onVariantAddButtonClick(it.id)
                    }
                },
                addButtonDescription = stringResource(R.string.item_product_variant_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.item_product_variant_default_value),
            )
        }

        val date = state.date.value
        SearchField(
            enabled = !state.loadingDate.value,
            value = if (date != null) SimpleDateFormat(
                "MMM d, yyyy",
                Locale.getDefault()
            ).format(date) else String(),
            showAddButton = false,
            label = stringResource(R.string.item_date),
            onClick = {
                state.isDatePickerDialogExpanded.value = true
            },
            error = if (state.attemptedToSubmit.value) state.dateError.value else false,
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
            val priceEnabled = state.selectedProduct.value != null && !state.loadingPrice.value

            StyledOutlinedTextField(
                singleLine = true,
                enabled = priceEnabled,
                value = state.price.value,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = {
                    if (it.isBlank()) {
                        state.price.value = String()
                    } else if (RegexHelper.isFloat(
                            it,
                            2
                        )
                    ) {
                        state.price.value = it
                    }

                    state.validatePrice()
                },
                label = {
                    Text(
                        text = stringResource(R.string.item_price),
                        fontSize = 16.sp,
                    )
                },
                isError = if (state.attemptedToSubmit.value) state.priceError.value else false,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(
                    enabled = priceEnabled,
                    onClick = {
                        if (state.validatePrice()) {
                            val value = StringHelper.toDoubleOrNull(state.price.value)
                                ?: error("Price validation failed, got null instead of float")

                            state.price.value = "%.2f".format(
                                value.plus(0.5f)
                            )
                        }

                        if (state.price.value.isBlank()) {
                            state.price.value = "%.2f".format(0f)
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
                    enabled = priceEnabled,
                    onClick = {
                        if (state.validatePrice()) {
                            val value = StringHelper.toDoubleOrNull(state.price.value)
                                ?: error("Price validation failed, got null instead of float")

                            state.price.value = "%.2f".format(
                                if (value > 0.5f) {
                                    value.minus(0.5f)
                                } else {
                                    0f
                                }
                            )
                        }

                        if (state.price.value.isBlank()) {
                            state.price.value = "%.2f".format(0f)
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
            val quantityEnabled =
                state.selectedProduct.value != null && !state.loadingQuantity.value

            StyledOutlinedTextField(
                singleLine = true,
                enabled = quantityEnabled,
                value = state.quantity.value,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = {
                    if (it.isBlank()) {
                        state.quantity.value = String()
                    } else if (RegexHelper.isFloat(
                            it,
                            3
                        )
                    ) {
                        state.quantity.value = it
                    }

                    state.validateQuantity()
                },
                label = {
                    Text(
                        text = if (state.selectedVariant.value == null)
                            stringResource(R.string.item_product_variant_default_value)
                        else
                            stringResource(R.string.item_quantity),
                        fontSize = 16.sp,
                    )
                },
                isError = if (state.attemptedToSubmit.value) state.quantityError.value else false,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(
                    enabled = quantityEnabled,
                    onClick = {
                        if (state.validateQuantity()) {
                            val value = StringHelper.toDoubleOrNull(state.quantity.value)
                                ?: error("Quantity validation failed, got null instead of float")

                            state.quantity.value = "%.3f".format(
                                value.plus(1f)
                            )
                        }

                        if (state.quantity.value.isBlank()) {
                            state.quantity.value = "%.3f".format(0f)
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
                    enabled = quantityEnabled,
                    onClick = {
                        if (state.validateQuantity()) {
                            val value = StringHelper.toDoubleOrNull(state.quantity.value)
                                ?: error("Quantity validation failed, got null instead of float")

                            state.quantity.value = "%.3f".format(
                                if (value > 1f) {
                                    value.minus(1f)
                                } else {
                                    0f
                                }
                            )
                        }

                        if (state.quantity.value.isBlank()) {
                            state.quantity.value = "%.3f".format(0f)
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
            enabled = !state.loadingShop.value,
            optional = true,
            value = state.selectedShop.value?.name ?: String(),
            onClick = {
                state.isShopSearchDialogExpanded.value = true
            },
            label = stringResource(R.string.item_shop),
            onAddButtonClick = {
                onShopAddButtonClick()
            },
            addButtonDescription = stringResource(R.string.item_shop_add_description),
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SearchField(
            enabled = !state.loadingProduct.value,
            value = state.selectedProduct.value?.name ?: String(),
            onClick = {
                state.isProductSearchDialogExpanded.value = true
            },
            label = stringResource(R.string.item_product),
            error = if (state.attemptedToSubmit.value) state.selectedProductError.value else false,
            onAddButtonClick = {
                onProductAddButtonClick()
            },
            addButtonDescription = stringResource(R.string.item_product_add_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = ItemHorizontalPadding)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SearchField(
            enabled = state.selectedProduct.value != null && !state.loadingVariant.value,
            value = state.selectedVariant.value?.name
                ?: stringResource(R.string.item_product_variant_default_value),
            onClick = {
                state.isVariantSearchDialogExpanded.value = true
            },
            label = stringResource(R.string.item_product_variant),
            onAddButtonClick = {
                state.selectedProduct.value?.let {
                    onVariantAddButtonClick(it.id)
                }
            },
            addButtonDescription = stringResource(R.string.item_product_variant_add_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = ItemHorizontalPadding)
        )
    }
}

/**
 * Data representing [ModifyItemScreenImpl] screen state
 */
data class ModifyItemScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val selectedProduct: MutableState<Product?> = mutableStateOf(null),
    val selectedProductError: MutableState<Boolean> = mutableStateOf(false),

    val selectedVariant: MutableState<ProductVariant?> = mutableStateOf(null),

    val selectedShop: MutableState<Shop?> = mutableStateOf(null),

    val quantity: MutableState<String> = mutableStateOf(String()),
    val quantityError: MutableState<Boolean> = mutableStateOf(false),

    val price: MutableState<String> = mutableStateOf(String()),
    val priceError: MutableState<Boolean> = mutableStateOf(false),

    val date: MutableState<Long?> = mutableStateOf(null),
    val dateError: MutableState<Boolean> = mutableStateOf(false),

    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isShopSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isProductSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isVariantSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),

    val loadingProduct: MutableState<Boolean> = mutableStateOf(false),
    val loadingVariant: MutableState<Boolean> = mutableStateOf(false),
    val loadingShop: MutableState<Boolean> = mutableStateOf(false),
    val loadingQuantity: MutableState<Boolean> = mutableStateOf(false),
    val loadingPrice: MutableState<Boolean> = mutableStateOf(false),
    val loadingDate: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates selectedProduct field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyItemScreenState.validateSelectedProduct(): Boolean {
    return !(selectedProduct.value == null).also { selectedProductError.value = it }
}

/**
 * Validates quantity field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyItemScreenState.validateQuantity(): Boolean {
    return !(StringHelper.toDoubleOrNull(quantity.value) == null).also { quantityError.value = it }
}

/**
 * Validates price field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyItemScreenState.validatePrice(): Boolean {
    return !(StringHelper.toDoubleOrNull(price.value) == null).also { priceError.value = it }
}

/**
 * Validates date field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyItemScreenState.validateDate(): Boolean {
    return !(date.value == null).also { dateError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
fun ModifyItemScreenState.validate(): Boolean {
    val product = validateSelectedProduct()
    val quantity = validateQuantity()
    val price = validatePrice()
    val date = validateDate()

    return product && quantity && price && date
}

/**
 * Performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
fun ModifyItemScreenState.extractItemOrNull(itemId: Long = 0): Item? {
    if (!validate()) return null

    return Item(
        id = itemId,
        productId = selectedProduct.value?.id ?: return null,
        variantId = selectedVariant.value?.id,
        shopId = selectedShop.value?.id,
        actualQuantity = StringHelper.toDoubleOrNull(quantity.value) ?: return null,
        actualPrice = StringHelper.toDoubleOrNull(price.value) ?: return null,
        date = date.value ?: return null,
    )
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
                onVariantAddButtonClick = {},
                onItemVariantLongClick = {},
                onProductChange = {},
            )
        }
    }
}
