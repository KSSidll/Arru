package com.kssidll.arru.ui.screen.modify.productcategory.addproductcategory

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddProductCategoryViewModel
@Inject
constructor(override val categoryRepository: ProductCategoryRepositorySource) :
    ModifyProductCategoryViewModel() {

    /**
     * Tries to add a product category to the repository
     *
     * @return resulting [InsertResult]
     */
    // suspend fun addCategory(): InsertResult =
    //     viewModelScope
    //         .async {
    //             screenState.attemptedToSubmit.value = true
    //
    //             val result = categoryRepository.insert(screenState.name.value.data.orEmpty())
    //
    //             if (result.isError()) {
    //                 when (result.error!!) {
    //                     is InsertResult.InvalidName -> {
    //                         screenState.name.apply {
    //                             value = value.toError(FieldError.InvalidValueError)
    //                         }
    //                     }
    //
    //                     is InsertResult.DuplicateName -> {
    //                         screenState.name.apply {
    //                             value = value.toError(FieldError.DuplicateValueError)
    //                         }
    //                     }
    //                 }
    //             }
    //
    //             return@async result
    //         }
    //         .await()
}
