package com.kssidll.arru.ui.screen.modify.productcategory.addproductcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEvent
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEventResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductCategoryRoute(
    defaultName: String?,
    navigateBack: (productCategoryId: Long?) -> Unit,
    viewModel: AddProductCategoryViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.handleEvent(ModifyProductCategoryEvent.SetName(defaultName)) }

    ModifyProductCategoryScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductCategoryEvent.NavigateBack -> navigateBack(null)
                    is ModifyProductCategoryEvent.DeleteProductCategory -> {}
                    is ModifyProductCategoryEvent.MergeProductCategory -> {}
                    is ModifyProductCategoryEvent.SelectMergeCandidate -> {}
                    is ModifyProductCategoryEvent.SetDangerousDeleteDialogConfirmation -> {}
                    is ModifyProductCategoryEvent.SetDangerousDeleteDialogVisibility -> {}
                    is ModifyProductCategoryEvent.SetMergeConfirmationDialogVisibility -> {}
                    is ModifyProductCategoryEvent.SetMergeSearchDialogVisibility -> {}
                    is ModifyProductCategoryEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (result is ModifyProductCategoryEventResult.SuccessInsert) {
                            navigateBack(result.id)
                        }
                    }
                }
            }
        },
    )
}
