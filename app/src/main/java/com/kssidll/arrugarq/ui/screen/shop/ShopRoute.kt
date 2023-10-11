package com.kssidll.arrugarq.ui.screen.shop


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    onBack: () -> Unit,
) {
    val shop: ShopViewModel = hiltViewModel()

    ShopScreen(

    )
}