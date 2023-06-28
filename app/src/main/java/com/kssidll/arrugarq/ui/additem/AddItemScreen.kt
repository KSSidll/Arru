package com.kssidll.arrugarq.ui.additem

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.data.ProductWithAltNames
import com.kssidll.arrugarq.data.data.Shop
import com.kssidll.arrugarq.ui.shared.SecondaryAppBar
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Optional

@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    onItemAdd: (AddItemData) -> Unit,
    onProductAdd: () -> Unit,
    onShopAdd: () -> Unit,
    productsWithAltNames: Flow<List<ProductWithAltNames>>,
    shops: Flow<List<Shop>>,
    state: AddItemState,
) {
    Column {

        var isProductSearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var isShopSearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        BackHandler (
            enabled = isProductSearchExpanded or isShopSearchExpanded
        ) {
            isProductSearchExpanded = false
            isShopSearchExpanded = false
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
                    !isShopSearchExpanded
                ) {
                    onBack()
                }
                isProductSearchExpanded = false
                isShopSearchExpanded = false
            }
        ) {
            Text(text = "Item")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            if (isProductSearchExpanded) {
                AddItemSearchProduct(
                    productsWithAltNames = productsWithAltNames,
                    onItemClick = { product: Product ->
                        state.selectedProduct.value = product
                        isProductSearchExpanded = false
                    },
                )
            } else if (isShopSearchExpanded) {
                AddItemSearchShop(
                    shops = shops,
                    onItemClick = { shop ->
                        state.selectedShop.value = shop
                        isShopSearchExpanded = false
                    }
                )
            } else {
                Column {
                    Column (
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Bottom,
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                singleLine = true,
                                value = state.unitMeasure.value,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                onValueChange = {
                                    state.unitMeasure.value = it
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    cursorColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                ),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                suffix = {
                                    Text(
                                        text = "Unit Measure",
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                    )
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                singleLine = true,
                                value = state.date.value,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                onValueChange = {
                                    state.date.value = it
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
                                        text = "Date",
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                    )
                                },
                                isError = dateError
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                singleLine = true,
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
                                        text = "Price",
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                    )
                                },
                                isError = priceError
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                OutlinedTextField(
                                    singleLine = true,
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
                                            text = "Quantity",
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .alpha(0.5F)
                                        )
                                    },
                                    isError = quantityError
                                )
                            }


                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
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
                                        focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                                    suffix = {
                                        Text(
                                            text = "Shop",
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .alpha(0.5F)
                                                .padding(end = 6.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        BoxWithConstraints {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .aspectRatio(1F)
                                                    .clickable {
                                                        onShopAdd()
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                                Canvas(modifier = Modifier.fillMaxSize()) {
                                                    drawLine(
                                                        color = lineColor,
                                                        start = Offset(0F, 0F),
                                                        end = Offset(0F, size.height),
                                                        strokeWidth = Dp.Hairline.value
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add new Shop",
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            }

                                        }
                                    }
                                )
                            }

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
                                            text = "Product",
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .alpha(0.5F)
                                                .padding(end = 6.dp)
                                        )
                                    },
                                    isError = productError,
                                    trailingIcon = {
                                        BoxWithConstraints {
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
                                                        start = Offset(0F, 0F),
                                                        end = Offset(0F, size.height),
                                                        strokeWidth = Dp.Hairline.value
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add new Product",
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            }

                                        }
                                    }
                                )
                            }

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
                                val shop: Shop? = state.selectedShop.value

                                val quantity: Long? = state.quantity.value.toLongOrNull()
                                val unitMeasure: Long? = state.unitMeasure.value.toLongOrNull()
                                val price: Float? = state.price.value.toFloatOrNull()
                                val date: Long? = state.date.value.toLongOrNull()

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
                                            shopId = Optional.ofNullable(shop?.id),
                                            quantity = quantity!!,
                                            unitMeasure = Optional.ofNullable(unitMeasure),
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
                                    contentDescription = "Add Item",
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Item",
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
@Preview(group = "AddItemScreen", name = "Add Item Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddItemScreen", name = "Add Item Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddItemScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddItemScreen(
                onBack = {},
                onItemAdd = {},
                onProductAdd = {},
                onShopAdd = {},
                productsWithAltNames = flowOf(),
                shops = flowOf(
                    listOf(
                        Shop(0, "test")
                    )
                ),
                state = AddItemState()
            )
        }
    }
}
