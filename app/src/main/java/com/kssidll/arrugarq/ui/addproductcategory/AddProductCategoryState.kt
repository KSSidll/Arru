package com.kssidll.arrugarq.ui.addproductcategory

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kssidll.arrugarq.data.data.ProductCategoryType

data class AddProductCategoryState(
    var selectedProductCategoryType: MutableState<ProductCategoryType?> = mutableStateOf(null),

    var name: MutableState<String> = mutableStateOf(String()),
)
