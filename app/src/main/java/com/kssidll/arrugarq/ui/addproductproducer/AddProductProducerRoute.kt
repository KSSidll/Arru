package com.kssidll.arrugarq.ui.addproductproducer

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.*

@Composable
fun AddProductProducerRoute(
    onBack: () -> Unit,
) {
    val addProductProducerViewModel: AddProductProducerViewModel = hiltViewModel()

    AddProductProducerScreen(
        onBack = onBack,
        onProducerAdd = {
            addProductProducerViewModel.addProducer(it)
        }
    )
}