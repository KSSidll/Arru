package com.kssidll.arru.ui.screen.modify.product.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.product.ModifyProductScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductRoute(
    defaultName: String?,
    navigateBack: (productId: Long?) -> Unit,
    navigateCategoryAdd: (query: String?) -> Unit,
    navigateProducerAdd: (query: String?) -> Unit,
    navigateCategoryEdit: (categoryId: Long) -> Unit,
    navigateProducerEdit: (producerId: Long) -> Unit,
    providedProducerId: Long?,
    providedCategoryId: Long?,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddProductViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    LaunchedEffect(providedProducerId) {
        viewModel.setSelectedProducer(providedProducerId)
    }

    LaunchedEffect(providedCategoryId) {
        viewModel.setSelectedCategory(providedCategoryId)
    }

    ModifyProductScreenImpl(
        onBack = {
            navigateBack(null)
        },
        state = viewModel.screenState,
        categories = viewModel.allCategories()
            .collectAsState(initial = Data.Loading()).value,
        producers = viewModel.allProducers()
            .collectAsState(initial = Data.Loading()).value,
        onNewProducerSelected = {
            viewModel.onNewProducerSelected(it)
        },
        onNewCategorySelected = {
            viewModel.onNewCategorySelected(it)
        },
        onSubmit = {
            scope.launch {
                val result = viewModel.addProduct()
                if (result.isNotError()) {
                    navigateBack(result.id)
                }
            }
        },
        onCategoryAddButtonClick = navigateCategoryAdd,
        onProducerAddButtonClick = navigateProducerAdd,
        onItemCategoryLongClick = navigateCategoryEdit,
        onItemProducerLongClick = navigateProducerEdit,
    )
}
