package com.kssidll.arru.ui.screen.search.productcategorylist

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProductCategoryListRoute(
    onCategoryClick: (categoryId: Long) -> Unit,
    onCategoryLongClick: (categoryId: Long) -> Unit,
    viewModel: ProductCategoryListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SearchList(
        filter = uiState.filter,
        onFilterChange = { viewModel.handleEvent(ProductCategoryListSearchEvent.SetFilter(it)) },
        items = uiState.allProductCategories,
        onItemClick = { onCategoryClick(it.id) },
        onItemLongClick = { onCategoryLongClick(it.id) },
    )
}
