package com.kssidll.arru.ui.screen.search.start


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.search.component.*
import com.kssidll.arru.ui.theme.*

/**
 * @param onProductClick Callback called when product item is clicked
 * @param onCategoryClick Callback called when category item is clicked
 * @param onShopClick Callback called when shop item is clicked
 * @param onProducerClick Callback called when producer item is clicked
 */
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

/**
 * [StartScreen] content
 * @param onProductClick Callback called when product item is clicked
 * @param onCategoryClick Callback called when category item is clicked
 * @param onShopClick Callback called when shop item is clicked
 * @param onProducerClick Callback called when producer item is clicked
 */
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
            onItemClick = onProducerClick,
        )
        SearchItemHorizontalDivider()

        SearchItem(
            text = stringResource(id = R.string.item_shop),
            onItemClick = onShopClick,
        )

        SearchItemHorizontalDivider()

        SearchItem(
            text = stringResource(id = R.string.item_product_category),
            onItemClick = onCategoryClick,
        )

        SearchItemHorizontalDivider()

        SearchItem(
            text = stringResource(id = R.string.item_product),
            onItemClick = onProductClick,
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
