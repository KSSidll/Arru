package com.kssidll.arrugarq.ui.screen.modify.product.addproduct

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

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