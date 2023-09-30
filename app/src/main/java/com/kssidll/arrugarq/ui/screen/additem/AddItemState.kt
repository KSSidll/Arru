package com.kssidll.arrugarq.ui.screen.additem

import androidx.compose.runtime.*
import com.kssidll.arrugarq.data.data.*

data class AddItemState(
    var selectedProduct: MutableState<Product?> = mutableStateOf(null),
    var selectedVariant: MutableState<ProductVariant?> = mutableStateOf(null),
    var selectedShop: MutableState<Shop?> = mutableStateOf(null),

    var quantity: MutableState<String> = mutableStateOf(String()),
    var price: MutableState<String> = mutableStateOf(String()),

    var date: MutableState<Long?> = mutableStateOf(null),
)
