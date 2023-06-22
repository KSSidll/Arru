package com.kssidll.arrugarq.ui.additem

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.data.Shop
import com.kssidll.arrugarq.ui.shared.SecondaryAppBar
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    onItemAdd: (AddItemData) -> Unit,
    onProductAdd: () -> Unit,
    onShopAdd: () -> Unit,
    products: Flow<List<Product>>,
    shops: Flow<List<Shop>>,
    state: AddItemState,
) {
    Column {
        SecondaryAppBar(onBack = onBack) {}

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            AddItemScreenContent(
                onItemAdd = onItemAdd,
                onProductAdd = onProductAdd,
                onShopAdd = onShopAdd,
                products = products,
                shops = shops,
                state = state,
            )
        }
    }
}

@Composable
fun AddItemScreenContent(
    onItemAdd: (AddItemData) -> Unit,
    onProductAdd: () -> Unit,
    onShopAdd: () -> Unit,
    products: Flow<List<Product>>,
    shops: Flow<List<Shop>>,
    state: AddItemState,
) {
    var isProductSearchExpanded: Boolean by remember {
        mutableStateOf(false)
    }
    var isShopSearchExpanded: Boolean by remember {
        mutableStateOf(false)
    }

    BackHandler (
        enabled = isProductSearchExpanded or isShopSearchExpanded
    ) {
        isProductSearchExpanded = false
        isShopSearchExpanded = false
    }

    var shopError: Boolean by remember {
        mutableStateOf(false)
    }
    var productError: Boolean by remember {
        mutableStateOf(false)
    }
    var quantityError: Boolean by remember {
        mutableStateOf(false)
    }
    var unitMeasureError: Boolean by remember {
        mutableStateOf(false)
    }
    var priceError: Boolean by remember {
        mutableStateOf(false)
    }
    var dateError: Boolean by remember {
        mutableStateOf(false)
    }

    if (isProductSearchExpanded) {
        val collectedProducts = products.collectAsState(initial = emptyList()).value

        Column {
            Text(text = "Product")
            Button(onClick = { isProductSearchExpanded = false }) {
                Text(text = "Go Back")
            }

            LazyColumn {
               items(items = collectedProducts) {
                   Row {
                       Text(text = it.name)
                   }
               }
            }

        }
    } else if (isShopSearchExpanded) {
        val collectedProducts = shops.collectAsState(initial = emptyList()).value

        Column {
            Text(text = "Shop")
            Button(onClick = { isShopSearchExpanded = false }) {
                Text(text = "Go Back")
            }

            LazyColumn {
                items(items = collectedProducts) {
                    Row {
                        Text(text = it.name)
                    }
                }
            }

        }
    } else {
        Column {
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
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    placeholder = {
                        Text(
                            text = "Product",
                            modifier = Modifier
                                .alpha(0.5F)
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
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    placeholder = {
                        Text(
                            text = "Shop",
                            modifier = Modifier
                                .alpha(0.5F)
                        )
                    },
                    isError = shopError,
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
                                    contentDescription = "Add new Shop",
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
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
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        textStyle = TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        placeholder = {
                            Text(
                                text = "Quantity",
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = quantityError
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        textStyle = TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        placeholder = {
                            Text(
                                text = "Unit Measure",
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = unitMeasureError
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
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        textStyle = TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        placeholder = {
                            Text(
                                text = "Price",
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = priceError
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
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        textStyle = TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        placeholder = {
                            Text(
                                text = "Date",
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = dateError
                    )
                }






            }
        }
    }
}

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
                products = flowOf(
                    listOf(
                        Product(0,0,"test1"),
                    )
                ),
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
