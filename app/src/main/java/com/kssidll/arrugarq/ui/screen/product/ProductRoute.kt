package com.kssidll.arrugarq.ui.screen.product


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProductRoute(
    productId: Long,
    onBack: () -> Unit,
) {
    val product: ProductViewModel = hiltViewModel()

    ProductScreen(

    )
}