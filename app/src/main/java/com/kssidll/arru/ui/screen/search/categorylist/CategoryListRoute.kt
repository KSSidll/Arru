package com.kssidll.arru.ui.screen.search.categorylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun CategoryListRoute(
    onCategoryClick: (categoryId: Long) -> Unit,
    onCategoryLongClick: (categoryId: Long) -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel(),
) {
    SearchList(
        filter = viewModel.filter,
        onFilterChange = { viewModel.filter = it },
        items = viewModel.items().collectAsState(initial = emptyImmutableList()).value,
        onItemClick = { onCategoryClick(it.id) },
        onItemLongClick = { onCategoryLongClick(it.id) },
    )
}
