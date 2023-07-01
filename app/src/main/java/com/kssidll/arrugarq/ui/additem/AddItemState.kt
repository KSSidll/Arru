package com.kssidll.arrugarq.ui.additem

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.data.ProductVariant
import com.kssidll.arrugarq.data.data.Shop

data class AddItemState(
    var selectedProduct: MutableState<Product?> = mutableStateOf(null),
    var selectedVariant: MutableState<ProductVariant?> = mutableStateOf(null),
    var selectedShop: MutableState<Shop?> = mutableStateOf(null),

    var quantity: MutableState<String> = mutableStateOf(String()),
    var price: MutableState<String> = mutableStateOf(String()),

    var date: MutableState<Long?> = mutableStateOf(null),
)
