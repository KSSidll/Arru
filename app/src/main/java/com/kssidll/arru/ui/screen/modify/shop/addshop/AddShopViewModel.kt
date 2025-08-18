package com.kssidll.arru.ui.screen.modify.shop.addshop

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async

@HiltViewModel
class AddShopViewModel @Inject constructor(override val shopRepository: ShopRepositorySource) :
    ModifyShopViewModel() {

    /**
     * Tries to add a shop to the repository
     *
     * @return resulting [InsertResult]
     */
    suspend fun addShop() =
        viewModelScope
            .async {
                screenState.attemptedToSubmit.value = true

                val result = shopRepository.insert(screenState.name.value.data.orEmpty())

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
                    }
                }

                return@async result
            }
            .await()
}
