package com.kssidll.arrugarq.ui.screen.home.search.start


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.home.search.component.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
internal fun StartScreen(
    onProductClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onShopClick: () -> Unit,
    onProducerClick: () -> Unit,
) {
    StartScreenContent(
        onProductClick = onProductClick,
        onCategoryClick = onCategoryClick,
        onShopClick = onShopClick,
        onProducerClick = onProducerClick,
    )
}

@Composable
private fun StartScreenContent(
    onProductClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onShopClick: () -> Unit,
    onProducerClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {

        SearchItem(
            text = stringResource(id = R.string.item_product_producer),
            onSelect = onProducerClick,
        )
        SearchItemHorizontalDivider()

        SearchItem(
            text = stringResource(id = R.string.item_shop),
            onSelect = onShopClick,
        )

        SearchItemHorizontalDivider()

        SearchItem(
            text = stringResource(id = R.string.item_product_category),
            onSelect = onCategoryClick,
        )

        SearchItemHorizontalDivider()

        SearchItem(
            text = stringResource(id = R.string.item_product),
            onSelect = onProductClick,
        )
    }
}

@Preview(
    group = "StartScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "StartScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
private fun StartScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            StartScreenContent(
                onProductClick = {},
                onCategoryClick = {},
                onShopClick = {},
                onProducerClick = {},
            )
        }
    }
}
