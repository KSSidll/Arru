package com.kssidll.arru.ui.screen.search.start


import androidx.compose.runtime.Composable

@Composable
internal fun StartRoute(
    onProductClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onShopClick: () -> Unit,
    onProducerClick: () -> Unit,
) {
    StartScreen(
        onProductClick = onProductClick,
        onCategoryClick = onCategoryClick,
        onShopClick = onShopClick,
        onProducerClick = onProducerClick,
    )
}
