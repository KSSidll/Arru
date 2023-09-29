package com.kssidll.arrugarq.presentation.screen.additem

import android.annotation.*
import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.component.field.*
import com.kssidll.arrugarq.presentation.component.dialog.*
import com.kssidll.arrugarq.presentation.component.other.*
import com.kssidll.arrugarq.presentation.theme.*
import kotlinx.coroutines.flow.*
import java.text.*
import java.util.*

private fun isDateError(value: Long?) = value == null
private fun isPriceError(value: Float?) = value == null
private fun isQuantityError(value: Long?) = value == null
private fun isProductError(value: Product?) = value == null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    onItemAdd: (AddItemData) -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: (Long) -> Unit,
    onShopAdd: () -> Unit,
    productsWithAltNames: Flow<List<ProductWithAltNames>>,
    variants: Flow<List<ProductVariant>>,
    shops: Flow<List<Shop>>,
    state: AddItemState,
    onSelectProduct: (Product) -> Unit = {},
) {

    Column {
        val datePickerState = rememberDatePickerState()

        var isDatePickerDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }
        var isShopSearchDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }
        var isProductSearchDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }
        var isVariantSearchDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }

        var attemptedToSubmit: Boolean by remember {
            mutableStateOf(false)
        }

        SecondaryAppBar(onBack = onBack) {
            Text(text = stringResource(R.string.item))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {

            if (isDatePickerDialogExpanded) {

                DatePickerDialog(
                    onDismissRequest = {
                        isDatePickerDialogExpanded = false
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                isDatePickerDialogExpanded = false
                                if (datePickerState.selectedDateMillis != null) {
                                    state.date.value = datePickerState.selectedDateMillis
                                }
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Green.copy(alpha = 0.60f)
                                )
                            }
                        }

                    }
                ) {
                    DatePicker(
                        state = datePickerState
                    )
                }
            }

            if (isShopSearchDialogExpanded) {
                FuzzySearchableListDialog(
                    onDismissRequest = {
                        isShopSearchDialogExpanded = false
                    },
                    items = shops.collectAsState(emptyList()).value,
                    itemText = { it.name },
                    onItemClick = {
                        state.selectedShop.value = it
                        isShopSearchDialogExpanded = false
                    },
                    onAddButtonClick = onShopAdd,
                    addButtonDescription = stringResource(R.string.add_shop_description),
                    showDefaultValueItem = true,
                    defaultItemText = stringResource(R.string.no_value),
                )
            }

            if (isProductSearchDialogExpanded) {
                FuzzySearchableListDialog(
                    onDismissRequest = {
                        isProductSearchDialogExpanded = false
                    },
                    items = productsWithAltNames.collectAsState(emptyList()).value,
                    onItemClick = {
                        state.selectedProduct.value = it?.product
                        isProductSearchDialogExpanded = false
                        it?.product?.let { product -> onSelectProduct(product) }
                    },
                    itemText = { it.product.name },
                    onAddButtonClick = onProductAdd,
                    addButtonDescription = stringResource(R.string.add_product_description),
                )
            }

            if (isVariantSearchDialogExpanded) {
                FuzzySearchableListDialog(
                    onDismissRequest = {
                        isVariantSearchDialogExpanded = false
                    },
                    items = variants.collectAsState(emptyList()).value,
                    itemText = { it.name },
                    onItemClick = {
                        state.selectedVariant.value = it
                        isVariantSearchDialogExpanded = false
                    },
                    onAddButtonClick = { onVariantAdd(state.selectedProduct.value!!.id) },
                    addButtonDescription = stringResource(R.string.add_product_variant_description),
                    showDefaultValueItem = true,
                    defaultItemText = stringResource(R.string.item_product_variant_default_value),
                )
            }

            Column {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Bottom,
                ) {

                    val datePickerToggleInteractionSource =
                        remember { MutableInteractionSource() }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val date = state.date.value
                        SearchField(
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(0.85f),
                            value = if (date != null) SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date) else String(),
                            showAddButton = false,
                            label = stringResource(R.string.item_date),
                            onClick = {
                                isDatePickerDialogExpanded = true
                            },
                            interactionSource = datePickerToggleInteractionSource,
                            error = if (attemptedToSubmit) isDateError(state.date.value) else false
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val isPriceEnabled = state.selectedProduct.value != null
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        StyledOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            singleLine = true,
                            enabled = isPriceEnabled,
                            value = state.price.value,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = {
                                state.price.value = it
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.item_price),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .alpha(0.5F)
                                )
                            },
                            isError = if (attemptedToSubmit) isPriceError(state.price.value.toFloatOrNull()) else false
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val isQuantityEnabled = state.selectedProduct.value != null
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StyledOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            singleLine = true,
                            enabled = isQuantityEnabled,
                            value = state.quantity.value,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = {
                                state.quantity.value = it
                            },
                            label = {
                                Text(
                                    text = if (state.selectedVariant.value == null)
                                        stringResource(R.string.item_product_variant_default_value)
                                    else
                                        stringResource(R.string.item_quantity),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .alpha(0.5F)
                                )
                            },
                            isError = if (attemptedToSubmit) isQuantityError(state.quantity.value.toLongOrNull()) else false,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    SearchField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        optional = true,
                        value = state.selectedShop.value?.name ?: String(),
                        onClick = {
                            isShopSearchDialogExpanded = true
                        },
                        label = stringResource(R.string.item_shop),
                        onAddButtonClick = {
                            onShopAdd()
                        },
                        addButtonDescription = stringResource(R.string.add_shop_description),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SearchField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        value = state.selectedProduct.value?.name ?: String(),
                        onClick = {
                            isProductSearchDialogExpanded = true
                        },
                        label = stringResource(R.string.item_product),
                        error = if (attemptedToSubmit) isProductError(state.selectedProduct.value) else false,
                        onAddButtonClick = {
                            onProductAdd()
                        },
                        addButtonDescription = stringResource(R.string.add_product_description),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val isVariantEnabled = state.selectedProduct.value != null

                    SearchField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enabled = isVariantEnabled,
                        value = state.selectedVariant.value?.name
                            ?: stringResource(R.string.item_product_variant_default_value),
                        onClick = {
                            isVariantSearchDialogExpanded = true
                        },
                        label = stringResource(R.string.item_product_variant),
                        onAddButtonClick = {
                            state.selectedProduct.value?.let { onVariantAdd(it.id) }
                        },
                        addButtonDescription = stringResource(R.string.add_product_variant_description),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxHeight(0.4f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        onClick = {
                            attemptedToSubmit = true

                            val product: Product? = state.selectedProduct.value
                            val variant: ProductVariant? = state.selectedVariant.value
                            val shop: Shop? = state.selectedShop.value

                            val quantity: Long? = state.quantity.value.toLongOrNull()
                            val price: Float? = state.price.value.toFloatOrNull()
                            val date: Long? = state.date.value

                            if (
                                !isDateError(date) &&
                                !isPriceError(price) &&
                                !isQuantityError(quantity) &&
                                !isProductError(product)
                            ) {
                                onItemAdd(
                                    AddItemData(
                                        productId = product!!.id,
                                        variantId = Optional.ofNullable(variant?.id),
                                        shopId = Optional.ofNullable(shop?.id),
                                        quantity = quantity!!,
                                        price = price!!,
                                        date = date!!,
                                    )
                                )
                                onBack()
                            }
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.add_item_description),
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.item_add),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(
    group = "AddItemScreen",
    name = "Add Item Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddItemScreen",
    name = "Add Item Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddItemScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddItemScreen(
                onBack = {},
                onItemAdd = {},
                onProductAdd = {},
                onVariantAdd = {},
                onShopAdd = {},
                productsWithAltNames = flowOf(),
                variants = flowOf(),
                shops = flowOf(
                    listOf(
                        Shop(
                            0,
                            "test"
                        )
                    )
                ),
                state = AddItemState()
            )
        }
    }
}
