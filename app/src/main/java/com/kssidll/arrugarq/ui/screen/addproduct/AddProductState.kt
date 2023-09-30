package com.kssidll.arrugarq.ui.screen.addproduct

import androidx.compose.runtime.*
import com.kssidll.arrugarq.data.data.*

data class AddProductState(
    var selectedProductCategory: MutableState<ProductCategory?> = mutableStateOf(null),
    var selectedProductProducer: MutableState<ProductProducer?> = mutableStateOf(null),

    var name: MutableState<String> = mutableStateOf(String()),
)
