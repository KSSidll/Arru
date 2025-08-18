package com.kssidll.arru.ui.screen.modify.productcategory.addproductcategory

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class AddProductCategoryViewModel @Inject constructor(
    override val categoryRepository: ProductCategoryRepositorySource,
): ModifyProductCategoryViewModel() {

    /**
     * Tries to add a product category to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addCategory(): InsertResult = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = categoryRepository.insert(screenState.name.value.data.orEmpty())

        if (result.isError()) {
            when (result.error!!) {
                is InsertResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is InsertResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()
}