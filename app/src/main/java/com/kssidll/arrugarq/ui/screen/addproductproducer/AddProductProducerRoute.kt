package com.kssidll.arrugarq.ui.screen.addproductproducer

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

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