package com.kssidll.arru.ui.screen.modify.item.edititem


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.UpdateResult
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.VariantRepositorySource
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.item.ModifyItemViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class EditItemViewModel @Inject constructor(
    override val itemRepository: ItemRepositorySource,
    override val productRepository: ProductRepositorySource,
    override val variantsRepository: VariantRepositorySource,
): ModifyItemViewModel() {
    private var mItem: Item? = null

    /**
     * Updates data in the screen state
     * @return true if provided [itemId] was valid, false otherwise
     */
    suspend fun updateState(itemId: Long) = viewModelScope.async {
        // skip state update for repeating itemId
        if (itemId == mItem?.id) return@async true

        screenState.allToLoading()

        mItem = itemRepository.get(itemId)

        updateStateForItem(mItem)

        return@async mItem != null
    }
        .await()

    /**
     * Tries to update item with provided [itemId] with current screen state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateItem(itemId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = itemRepository.update(
            itemId = itemId,
            productId = screenState.selectedProduct.value.data?.id ?: Item.INVALID_PRODUCT_ID,
            variantId = screenState.selectedVariant.value.data?.id,
            quantity = screenState.quantity.value.data?.let { Item.quantityFromString(it) }
                ?: Item.INVALID_QUANTITY,
            price = screenState.price.value.data?.let { Item.priceFromString(it) }
                ?: Item.INVALID_PRICE,
        )

        if (result.isError()) {
            when (result.error!!) {
                UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update item with invalid item id in EditItemViewModel"
                    )
                    return@async UpdateResult.Success
                }

                UpdateResult.InvalidPrice -> {
                    screenState.price.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.InvalidProductId -> {
                    screenState.selectedProduct.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.InvalidQuantity -> {
                    screenState.quantity.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.InvalidVariantId -> {
                    screenState.selectedVariant.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete item with provided [itemId]
     * @return resulting [DeleteResult]
     */
    suspend fun deleteItem(itemId: Long) = viewModelScope.async {
        val result = itemRepository.delete(itemId)

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete item with invalid item id in EditItemViewModel"
                    )
                    return@async DeleteResult.Success
                }
            }
        }

        return@async result
    }
        .await()
}
