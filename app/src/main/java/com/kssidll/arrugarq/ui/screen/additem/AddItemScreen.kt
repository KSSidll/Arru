package com.kssidll.arrugarq.ui.screen.additem

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.ui.component.dialog.*
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.flow.*
import java.text.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddItemScreen(
    onBack: () -> Unit,
    state: AddItemScreenState,
    onItemAdd: () -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: () -> Unit,
    onShopAdd: () -> Unit,
    productsWithAltNames: Flow<List<ProductWithAltNames>>,
    variants: Flow<List<ProductVariant>>,
    shops: Flow<List<Shop>>,
    onSelectProduct: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(text = stringResource(R.string.item))
                },
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AddItemScreenContent(
                state = state,
                onItemAdd = onItemAdd,
                onProductAdd = onProductAdd,
                onVariantAdd = onVariantAdd,
                onShopAdd = onShopAdd,
                productsWithAltNames = productsWithAltNames,
                variants = variants,
                shops = shops,
                onSelectProduct = onSelectProduct,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemScreenContent(
    state: AddItemScreenState,
    onItemAdd: () -> Unit,
    onProductAdd: () -> Unit,
    onVariantAdd: () -> Unit,
    onShopAdd: () -> Unit,
    productsWithAltNames: Flow<List<ProductWithAltNames>>,
    variants: Flow<List<ProductVariant>>,
    shops: Flow<List<Shop>>,
    onSelectProduct: () -> Unit = {},
) {
    val datePickerState = rememberDatePickerState()

    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
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
        }

        if (state.isShopSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isShopSearchDialogExpanded.value = false
                },
                items = shops.collectAsState(emptyList()).value,
                itemText = { it.name },
                onItemClick = {
                    state.selectedShop.value = it
                    state.isShopSearchDialogExpanded.value = false
                },
                onAddButtonClick = onShopAdd,
                addButtonDescription = stringResource(R.string.add_shop_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
            )
        }

        if (state.isProductSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isProductSearchDialogExpanded.value = false
                },
                items = productsWithAltNames.collectAsState(emptyList()).value,
                onItemClick = {
                    state.selectedProduct.value = it?.product
                    state.isProductSearchDialogExpanded.value = false
                    state.validateSelectedProduct()
                    onSelectProduct()
                },
                itemText = { it.product.name },
                onAddButtonClick = onProductAdd,
                addButtonDescription = stringResource(R.string.add_product_description),
            )
        }

        if (state.isVariantSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isVariantSearchDialogExpanded.value = false
                },
                items = variants.collectAsState(emptyList()).value,
                itemText = { it.name },
                onItemClick = {
                    state.selectedVariant.value = it
                    state.isVariantSearchDialogExpanded.value = false
                },
                onAddButtonClick = { onVariantAdd() },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val date = state.date.value
                    SearchField(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(0.85f),
                        value = if (date != null) SimpleDateFormat(
                            "MMM d, yyyy",
                            Locale.getDefault()
                        ).format(date) else String(),
                        showAddButton = false,
                        label = stringResource(R.string.item_date),
                        onClick = {
                            state.isDatePickerDialogExpanded.value = true
                        },
                        error = if (state.attemptedToSubmit.value) state.dateError.value else false
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
                            state.validatePrice()
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.item_price),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = if (state.attemptedToSubmit.value) state.priceError.value else false,
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
                            state.validateQuantity()
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
                        isError = if (state.attemptedToSubmit.value) state.quantityError.value else false,
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
                        state.isShopSearchDialogExpanded.value = true
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
                        state.isProductSearchDialogExpanded.value = true
                    },
                    label = stringResource(R.string.item_product),
                    error = if (state.attemptedToSubmit.value) state.selectedProductError.value else false,
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
                        state.isVariantSearchDialogExpanded.value = true
                    },
                    label = stringResource(R.string.item_product_variant),
                    onAddButtonClick = {
                        onVariantAdd()
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
                        onItemAdd()
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

@Preview(
    group = "Add Item Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Add Item Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddItemScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddItemScreen(
                onBack = {},
                state = AddItemScreenState(),
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
            )
        }
    }
}
