package com.kssidll.arrugarq.ui.screen.modify.item.additem

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.ItemRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    override val itemRepository: ItemRepositorySource,
    override val productRepository: ProductRepositorySource,
    override val variantsRepository: VariantRepositorySource,
    override val shopRepository: ShopRepositorySource,
): ModifyItemViewModel() {

    init {
        loadLastItem()
    }

    /**
     * Tries to add an item to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addItem() = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = itemRepository.insert(
            productId = screenState.selectedProduct.value.data?.id ?: -1,
            variantId = screenState.selectedVariant.value.data?.id,
            quantity = screenState.quantity.value.data?.filter { it.isDigit() }
                .orEmpty()
                .toLongOrNull() ?: -1,
            price = screenState.price.value.data?.filter { it.isDigit() }
                .orEmpty()
                .toLongOrNull() ?: -1,
        )

        if (result.isError()) {
            when (result.error!!) {
                is InsertResult.InvalidProductId -> {
                    screenState.selectedProduct.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is InsertResult.InvalidVariantId -> {
                    screenState.selectedVariant.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is InsertResult.InvalidQuantity -> {
                    screenState.quantity.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is InsertResult.InvalidPrice -> {
                    screenState.price.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()
}