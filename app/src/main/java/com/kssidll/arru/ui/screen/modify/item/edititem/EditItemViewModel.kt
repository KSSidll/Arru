package com.kssidll.arru.ui.screen.modify.item.edititem

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.UpdateResult
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.item.ModifyItemViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditItemViewModel
@Inject
constructor(
    override val itemRepository: ItemRepositorySource,
    override val productRepository: ProductRepositorySource,
    override val variantsRepository: ProductVariantRepositorySource,
) : ModifyItemViewModel() {
    private var mItem: ItemEntity? = null

    suspend fun checkExists(id: Long) =
        viewModelScope
            .async {
                return@async itemRepository.get(id).first() != null
            }
            .await()

    fun updateState(itemId: Long) =
        viewModelScope.launch {
            // skip state update for repeating itemId
            if (itemId == mItem?.id) return@launch

            screenState.allToLoading()

            mItem = itemRepository.get(itemId).first()

            updateStateForItem(mItem)
        }

    /**
     * Tries to update item with provided [itemId] with current screen state data
     *
     * @return resulting [UpdateResult]
     */
    suspend fun updateItem(itemId: Long) =
        viewModelScope
            .async {
                screenState.attemptedToSubmit.value = true

                val result =
                    itemRepository.update(
                        id = itemId,
                        productId =
                            screenState.selectedProduct.value.data?.id
                                ?: ItemEntity.INVALID_PRODUCT_ID,
                        variantId = screenState.selectedVariant.value.data?.id,
                        quantity =
                            screenState.quantity.value.data?.let {
                                ItemEntity.quantityFromString(it)
                            } ?: ItemEntity.INVALID_QUANTITY,
                        price =
                            screenState.price.value.data?.let { ItemEntity.priceFromString(it) }
                                ?: ItemEntity.INVALID_PRICE,
                    )

                if (result.isError()) {
                    when (result.error!!) {
                        UpdateResult.InvalidId -> {
                            Log.e(
                                "InvalidId",
                                "Tried to update item with invalid item id in EditItemViewModel",
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
     *
     * @return resulting [DeleteResult]
     */
    suspend fun deleteItem(itemId: Long) =
        viewModelScope
            .async {
                val result = itemRepository.delete(itemId)

                if (result.isError()) {
                    when (result.error!!) {
                        DeleteResult.InvalidId -> {
                            Log.e(
                                "InvalidId",
                                "Tried to delete item with invalid item id in EditItemViewModel",
                            )
                            return@async DeleteResult.Success
                        }
                    }
                }

                return@async result
            }
            .await()
}
