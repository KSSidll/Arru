package com.kssidll.arrugarq.presentation.screen.additem

import android.annotation.*
import android.content.res.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.components.list.*
import com.kssidll.arrugarq.presentation.components.other.*
import com.kssidll.arrugarq.presentation.theme.*
import kotlinx.coroutines.flow.*
import java.text.*
import java.util.*

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
    val disabledMajorAlphaBonus = 0.50f
    val optionalBorderAlpha = 0.40f

    Column {
        val datePickerState = rememberDatePickerState()

        var isProductSearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var isShopSearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var isVariantSearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var isDatePickerDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }

        BackHandler(
            enabled = isProductSearchExpanded or isShopSearchExpanded or isVariantSearchExpanded
        ) {
            isProductSearchExpanded = false
            isShopSearchExpanded = false
            isVariantSearchExpanded = false
        }

        var productError: Boolean by remember {
            mutableStateOf(false)
        }
        var quantityError: Boolean by remember {
            mutableStateOf(false)
        }
        var priceError: Boolean by remember {
            mutableStateOf(false)
        }
        var dateError: Boolean by remember {
            mutableStateOf(false)
        }

        SecondaryAppBar(
            onBack = {
                if (
                    !isProductSearchExpanded &&
                    !isShopSearchExpanded &&
                    !isVariantSearchExpanded
                ) {
                    onBack()
                }
                isProductSearchExpanded = false
                isShopSearchExpanded = false
                isVariantSearchExpanded = false
            }
        ) {
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

            if (isProductSearchExpanded) {
                FuzzySearchableList(
                    items = productsWithAltNames.collectAsState(emptyList()).value,
                    onItemClick = {
                        state.selectedProduct.value = it?.product
                        isProductSearchExpanded = false
                        productError = false
                        it?.product?.let { product -> onSelectProduct(product) }
                    },
                    itemText = { it.product.name },
                    onAddButtonClick = onProductAdd,
                    addButtonDescription = stringResource(R.string.add_product_description),
                )
            } else if (isVariantSearchExpanded) {
                FuzzySearchableList(
                    items = variants.collectAsState(emptyList()).value,
                    itemText = { it.name },
                    onItemClick = {
                        state.selectedVariant.value = it
                        isVariantSearchExpanded = false
                    },
                    onAddButtonClick = { onVariantAdd(state.selectedProduct.value!!.id) },
                    addButtonDescription = stringResource(R.string.add_product_variant_description),
                    showDefaultValueItem = true,
                    defaultItemText = stringResource(R.string.item_product_variant_default_value),
                )
            } else if (isShopSearchExpanded) {
                FuzzySearchableList(
                    items = shops.collectAsState(emptyList()).value,
                    itemText = { it.name },
                    onItemClick = {
                        state.selectedShop.value = it
                        isShopSearchExpanded = false
                    },
                    onAddButtonClick = onShopAdd,
                    addButtonDescription = stringResource(R.string.add_shop_description),
                    showDefaultValueItem = true,
                    defaultItemText = stringResource(R.string.no_value),
                )
            } else {
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
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(
                                        minWidth = OutlinedTextFieldDefaults.MinWidth,
                                        minHeight = OutlinedTextFieldDefaults.MinHeight
                                    )
                                    .height(OutlinedTextFieldDefaults.MinHeight)
                                    .fillMaxWidth(0.85f)
                                    .border(
                                        shape = OutlinedTextFieldDefaults.shape,
                                        color = if (dateError)
                                            OutlinedTextFieldDefaults.colors().errorIndicatorColor
                                        else OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                                        width = OutlinedTextFieldDefaults.UnfocusedBorderThickness
                                    )
                                    .clickable(
                                        interactionSource = datePickerToggleInteractionSource,
                                        indication = null
                                    ) {
                                        isDatePickerDialogExpanded = true
                                    },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(OutlinedTextFieldDefaults.contentPadding())
                                        .fillMaxSize(),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .scrollable(
                                                    state = rememberScrollState(),
                                                    orientation = Orientation.Horizontal,
                                                    interactionSource = datePickerToggleInteractionSource
                                                ),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            val date = state.date.value
                                            Text(
                                                text = if (date != null)
                                                    SimpleDateFormat(
                                                        "MMM d, yyyy",
                                                        Locale.getDefault()
                                                    ).format(date)
                                                else String(),
                                                style = TextStyle.Default.copy(
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontSize = 16.sp
                                                ),
                                                maxLines = 1,
                                            )
                                        }

                                        Text(
                                            text = stringResource(R.string.item_date),
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .alpha(0.5F)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isPriceEnabled = state.selectedProduct.value != null
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            OutlinedTextField(
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
                                colors = OutlinedTextFieldDefaults.colors(
                                    cursorColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                                ),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                suffix = {
                                    Text(
                                        text = stringResource(R.string.item_price),
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                    )
                                },
                                isError = priceError
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isQuantityEnabled = state.selectedProduct.value != null
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
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
                                colors = OutlinedTextFieldDefaults.colors(
                                    cursorColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                                ),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                suffix = {
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
                                isError = quantityError
                            )
                        }


                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                singleLine = true,
                                value = state.selectedShop.value?.name ?: String(),
                                onValueChange = {

                                },
                                modifier = Modifier
                                    .onFocusEvent {
                                        if (it.isFocused) {
                                            isShopSearchExpanded = true
                                        }
                                    }
                                    .fillMaxSize(),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = optionalBorderAlpha),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = optionalBorderAlpha)
                                ),
                                suffix = {
                                    Text(
                                        text = stringResource(R.string.item_shop),
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                            .padding(end = 6.dp)
                                    )
                                },
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1F)
                                            .clickable {
                                                onShopAdd()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val lineColor =
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawLine(
                                                color = lineColor,
                                                start = Offset(
                                                    0F,
                                                    0F
                                                ),
                                                end = Offset(
                                                    0F,
                                                    size.height
                                                ),
                                                strokeWidth = Dp.Hairline.value
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.add_shop_description),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .alpha(1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                singleLine = true,
                                value = state.selectedProduct.value?.name ?: String(),
                                onValueChange = {

                                },
                                modifier = Modifier
                                    .onFocusEvent {
                                        if (it.isFocused) {
                                            isProductSearchExpanded = true
                                        }
                                    }
                                    .fillMaxSize(),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                suffix = {
                                    Text(
                                        text = stringResource(R.string.item_product),
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                            .padding(end = 6.dp)
                                    )
                                },
                                isError = productError,
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1F)
                                            .clickable {
                                                onProductAdd()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val lineColor = MaterialTheme.colorScheme.onBackground
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawLine(
                                                color = lineColor,
                                                start = Offset(
                                                    0F,
                                                    0F
                                                ),
                                                end = Offset(
                                                    0F,
                                                    size.height
                                                ),
                                                strokeWidth = Dp.Hairline.value
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.add_product_description),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isVariantEnabled = state.selectedProduct.value != null

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .alpha(if (isVariantEnabled) 1f else disabledMajorAlphaBonus),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                singleLine = true,
                                enabled = isVariantEnabled,
                                value = state.selectedVariant.value?.name
                                    ?: stringResource(R.string.item_product_variant_default_value),
                                onValueChange = {

                                },
                                modifier = Modifier
                                    .onFocusEvent {
                                        if (it.isFocused) {
                                            isVariantSearchExpanded = true
                                        }
                                    }
                                    .fillMaxSize(),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = if (isVariantEnabled) 1f else disabledMajorAlphaBonus
                                    ),
                                    fontSize = 16.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = optionalBorderAlpha),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = optionalBorderAlpha)
                                ),
                                suffix = {
                                    Text(
                                        text = stringResource(R.string.item_product_variant),
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                            .padding(end = 6.dp)
                                    )
                                },
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1F)
                                            .clickable {
                                                state.selectedProduct.value?.let { onVariantAdd(it.id) }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val lineColor = MaterialTheme.colorScheme.onBackground
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawLine(
                                                color = lineColor,
                                                start = Offset(
                                                    0F,
                                                    0F
                                                ),
                                                end = Offset(
                                                    0F,
                                                    size.height
                                                ),
                                                strokeWidth = Dp.Hairline.value
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.add_product_variant_description),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }



                    Row(
                        modifier = Modifier.fillMaxHeight(0.4f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {

                        Button(
                            onClick = {
                                val product: Product? = state.selectedProduct.value
                                val variant: ProductVariant? = state.selectedVariant.value
                                val shop: Shop? = state.selectedShop.value

                                val quantity: Long? = state.quantity.value.toLongOrNull()
                                val price: Float? = state.price.value.toFloatOrNull()
                                val date: Long? = state.date.value

                                productError = product == null
                                quantityError = quantity == null
                                priceError = price == null
                                dateError = date == null

                                if (
                                    !productError &&
                                    !quantityError &&
                                    !priceError &&
                                    !dateError
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
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
