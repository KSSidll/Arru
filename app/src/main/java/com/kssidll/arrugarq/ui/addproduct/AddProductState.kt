package com.kssidll.arrugarq.ui.addproduct

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kssidll.arrugarq.data.data.ProductCategory
import com.kssidll.arrugarq.data.data.ProductProducer

data class AddProductState(
    var selectedProductCategory: MutableState<ProductCategory?> = mutableStateOf(null),
    var selectedProductProducer: MutableState<ProductProducer?> = mutableStateOf(null),

    var name: MutableState<String> = mutableStateOf(String()),
)
