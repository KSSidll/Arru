package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R

@Composable
fun HomeRoute(
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
    onTransactionItemClick: (productId: Long) -> Unit,
    onTransactionItemLongClick: (itemId: Long) -> Unit,
    onTransactionCategoryClick: (categoryId: Long) -> Unit,
    onTransactionProducerClick: (producerId: Long) -> Unit,
    onTransactionShopClick: (shopId: Long) -> Unit,
) {
    HomeScreen(
        onAddItem = onAddItem,
        onDashboardCategoryCardClick = onDashboardCategoryCardClick,
        onDashboardShopCardClick = onDashboardShopCardClick,
        onTransactionItemClick = {
            onTransactionItemClick(it.embeddedItem.item.productId)
        },
        onTransactionItemLongClick = {
            onTransactionItemLongClick(it.embeddedItem.item.id)
        },
        onTransactionCategoryClick = {
            onTransactionCategoryClick(it.id)
        },
        onTransactionProducerClick = {
            onTransactionProducerClick(it.id)
        },
        onTransactionShopClick = {
            onTransactionShopClick(it.id)
        },
    )
}

enum class HomeScreenLocations(
    val initial: Boolean = false,
) {
    Dashboard(initial = true),
    Search(),
    Transactions(),
    ;

    val description: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Dashboard -> stringResource(R.string.navigate_to_dashboard_description)
            Search -> stringResource(R.string.navigate_to_search_description)
            Transactions -> stringResource(R.string.navigate_to_transactions_description)
        }

    val imageVector: ImageVector
        @Composable
        get() = when (this) {
            Dashboard -> Icons.Rounded.Home
            Search -> Icons.Rounded.Search
            Transactions -> Icons.AutoMirrored.Rounded.Notes
        }

    companion object {
        private val idMap = entries.associateBy { it.ordinal }
        fun getByOrdinal(ordinal: Int) = idMap[ordinal]

    }
}

@Composable
@ReadOnlyComposable
fun HomeScreenLocations.getTranslation(): String {
    return when (this) {
        HomeScreenLocations.Dashboard -> stringResource(R.string.dashboard_nav_label)
        HomeScreenLocations.Search -> stringResource(R.string.search_nav_label)
        HomeScreenLocations.Transactions -> stringResource(R.string.transactions_nav_label)
    }
}
