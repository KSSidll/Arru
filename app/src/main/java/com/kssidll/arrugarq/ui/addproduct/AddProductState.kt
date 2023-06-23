package com.kssidll.arrugarq.ui.addproduct

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kssidll.arrugarq.data.data.ProductCategory

data class AddProductState(
    var selectedProductCategory: MutableState<ProductCategory?> = mutableStateOf(null),

    var name: MutableState<String> = mutableStateOf(String()),
)
