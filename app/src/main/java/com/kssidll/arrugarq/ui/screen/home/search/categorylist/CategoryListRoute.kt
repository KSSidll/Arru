package com.kssidll.arrugarq.ui.screen.home.search.categorylist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.home.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryListRoute(
    onCategorySelect: (categoryId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
) {
    val viewModel: CategoryListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemSelect = {
            onCategorySelect(it.category.id)
        },
        onItemEdit = {
            onCategoryEdit(it.category.id)
        },
    )
}
