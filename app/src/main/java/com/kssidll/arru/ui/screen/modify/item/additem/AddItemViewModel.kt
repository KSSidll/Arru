package com.kssidll.arru.ui.screen.modify.item.additem

import android.util.*
import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    override val itemRepository: ItemRepositorySource,
    override val productRepository: ProductRepositorySource,
    override val variantsRepository: VariantRepositorySource,
): ModifyItemViewModel() {

    init {
        loadLastItem()
    }

    /**
     * Tries to add an item to the repository
     * @param transactionId id of the [TransactionBasket] to add the item to
     * @return resulting [InsertResult]
     */
    suspend fun addItem(transactionId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = itemRepository.insert(
            transactionId = transactionId,
            productId = screenState.selectedProduct.value.data?.id ?: Item.INVALID_PRODUCT_ID,
            variantId = screenState.selectedVariant.value.data?.id,
            quantity = screenState.quantity.value.data?.let { Item.quantityFromString(it) }
                ?: Item.INVALID_QUANTITY,
            price = screenState.price.value.data?.let { Item.priceFromString(it) }
                ?: Item.INVALID_PRICE,
        )

        if (result.isError()) {
            when (result.error!!) {
                is InsertResult.InvalidTransactionId -> {
                    Log.e(
                        "InvalidId",
                        "Tried inserting an item to a transaction that doesn't exist in AddItemViewModel"
                    )
                    return@async InsertResult.Success(-1)
                }

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