package com.kssidll.arru.ui.screen.search.categorylist


import androidx.compose.runtime.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryListRoute(
    onCategoryClick: (categoryId: Long) -> Unit,
    onCategoryLongClick: (categoryId: Long) -> Unit,
) {
    val viewModel: CategoryListViewModel = hiltViewModel()

    SearchList(
        filter = viewModel.filter,
        onFilterChange = {
            viewModel.filter = it
        },
        items = viewModel.items()
            .collectAsState(initial = Data.Loading()).value,
        onItemClick = {
            onCategoryClick(it.category.id)
        },
        onItemLongClick = {
            onCategoryLongClick(it.category.id)
        },
    )
}
