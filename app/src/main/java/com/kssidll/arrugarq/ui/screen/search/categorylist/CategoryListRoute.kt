package com.kssidll.arrugarq.ui.screen.search.categorylist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryListRoute(
    onCategoryClick: (categoryId: Long) -> Unit,
    onCategoryLongClick: (categoryId: Long) -> Unit,
) {
    val viewModel: CategoryListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemClick = {
            onCategoryClick(it.category.id)
        },
        onItemLongClick = {
            onCategoryLongClick(it.category.id)
        },
    )
}
