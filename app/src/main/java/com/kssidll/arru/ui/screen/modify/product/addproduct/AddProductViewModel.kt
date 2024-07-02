package com.kssidll.arru.ui.screen.modify.product.addproduct

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.product.ModifyProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    override val productRepository: ProductRepositorySource,
    override val categoryRepository: CategoryRepositorySource,
    override val producerRepository: ProducerRepositorySource,
): ModifyProductViewModel() {

    /**
     * Tries to add a product to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addProduct() = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = productRepository.insert(
            name = screenState.name.value.data.orEmpty(),
            categoryId = screenState.selectedProductCategory.value.data?.id
                ?: Product.INVALID_CATEGORY_ID,
            producerId = screenState.selectedProductProducer.value.data?.id
        )

        if (result.isError()) {
            when (result.error!!) {
                InsertResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                InsertResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }

                InsertResult.InvalidCategoryId -> {
                    screenState.selectedProductCategory.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                InsertResult.InvalidProducerId -> {
                    screenState.selectedProductProducer.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()
}