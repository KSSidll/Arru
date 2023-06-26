package com.kssidll.arrugarq.ui.addproductproducer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductProducerRoute(
    onBack: () -> Unit,
) {
    val addProductProducerViewModel: AddProductProducerViewModel = hiltViewModel()

    AddProductProducerScreen (
        onBack = onBack,
        onProducerAdd = {
            addProductProducerViewModel.addProducer(it)
        }
    )
}