package com.kssidll.arrugarq.ui.screen.category.editcategory


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.category.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditCategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditCategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        if (!viewModel.updateState(categoryId)) {
            onBack()
        }
    }

    EditCategoryScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            viewModel.updateCategory(categoryId)
            onBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteCategory(categoryId)) {
                    onBackDelete()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_category_edit),
    )

}
