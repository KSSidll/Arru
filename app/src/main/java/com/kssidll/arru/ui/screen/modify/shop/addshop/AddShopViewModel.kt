package com.kssidll.arru.ui.screen.modify.shop.addshop

import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.InsertShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertShopEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddShopViewModel
@Inject
constructor(
    override val shopRepository: ShopRepositorySource,
    private val insertShopEntityUseCase: InsertShopEntityUseCase,
) : ModifyShopViewModel() {

    suspend fun addShop(): Long? {
        screenState.attemptedToSubmit.value = true

        val result = insertShopEntityUseCase(name = screenState.name.value.data)

        return when (result) {
            is InsertShopEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        InsertShopEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        InsertShopEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                null
            }
            is InsertShopEntityUseCaseResult.Success -> {
                result.id
            }
        }
    }
}
