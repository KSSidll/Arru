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
    navigateSettings: () -> Unit,
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    HomeScreen(
        navigateSettings = navigateSettings,
        onAddItem = onAddItem,
        onDashboardCategoryCardClick = onDashboardCategoryCardClick,
        onDashboardShopCardClick = onDashboardShopCardClick,
        onItemEdit = onItemEdit,
        onProductSelect = onProductSelect,
        onProductEdit = onProductEdit,
        onShopSelect = onShopSelect,
        onShopEdit = onShopEdit,
        onCategorySelect = onCategorySelect,
        onCategoryEdit = onCategoryEdit,
        onProducerSelect = onProducerSelect,
        onProducerEdit = onProducerEdit,
    )
}

internal enum class HomeScreenLocations(
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
internal fun HomeScreenLocations.getTranslation(): String {
    return when (this) {
        HomeScreenLocations.Dashboard -> stringResource(R.string.dashboard_nav_label)
        HomeScreenLocations.Search -> stringResource(R.string.search_nav_label)
        HomeScreenLocations.Transactions -> stringResource(R.string.transactions_nav_label)
    }
}
