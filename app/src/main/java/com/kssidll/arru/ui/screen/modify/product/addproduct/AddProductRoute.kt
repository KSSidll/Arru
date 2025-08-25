package com.kssidll.arru.ui.screen.modify.product.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.modify.product.ModifyProductScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductRoute(
    defaultName: String?,
    navigateBack: (productId: Long?) -> Unit,
    navigateAddProductCategory: (query: String?) -> Unit,
    navigateAddProductProducer: (query: String?) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    providedProducerId: Long?,
    providedCategoryId: Long?,
    viewModel: AddProductViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.screenState.name.value = Field.Loaded(defaultName) }

    LaunchedEffect(providedProducerId) { viewModel.setSelectedProducer(providedProducerId) }

    LaunchedEffect(providedCategoryId) { viewModel.setSelectedCategory(providedCategoryId) }

    ModifyProductScreenImpl(
        onBack = { navigateBack(null) },
        state = viewModel.screenState,
        categories = viewModel.allCategories().collectAsState(initial = emptyImmutableList()).value,
        producers = viewModel.allProducers().collectAsState(initial = emptyImmutableList()).value,
        onNewProducerSelected = { viewModel.onNewProducerSelected(it) },
        onNewCategorySelected = { viewModel.onNewCategorySelected(it) },
        onSubmit = {
            scope.launch {
                val id = viewModel.addProduct()
                if (id != null) {
                    navigateBack(id)
                }
            }
        },
        onCategoryAddButtonClick = navigateAddProductCategory,
        onProducerAddButtonClick = navigateAddProductProducer,
        onItemCategoryLongClick = navigateEditProductCategory,
        onItemProducerLongClick = navigateEditProductProducer,
    )
}
