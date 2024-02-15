package com.kssidll.arrugarq.ui.screen.modify.product.addproduct

import androidx.compose.runtime.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductRoute(
    defaultName: String?,
    navigateBack: () -> Unit,
    navigateCategoryAdd: (query: String?) -> Unit,
    navigateProducerAdd: (query: String?) -> Unit,
    navigateCategoryEdit: (categoryId: Long) -> Unit,
    navigateProducerEdit: (producerId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddProductViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyProductScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        categories = viewModel.allCategories()
            .collectAsState(initial = emptyList()).value,
        producers = viewModel.allProducers()
            .collectAsState(initial = emptyList()).value,
        onSubmit = {
            scope.launch {
                // TODO don't check for null, check if not error once implemented error type
                val result = viewModel.addProduct()
                if (result != null) navigateBack()
            }
        },
        onCategoryAddButtonClick = navigateCategoryAdd,
        onProducerAddButtonClick = navigateProducerAdd,
        onItemCategoryLongClick = navigateCategoryEdit,
        onItemProducerLongClick = navigateProducerEdit,
    )
}
