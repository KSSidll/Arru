package com.kssidll.arru.ui.screen.modify.shop.editshop

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteShopEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.MergeShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeShopEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateShopEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditShopViewModel
@Inject
constructor(
    override val shopRepository: ShopRepositorySource,
    private val updateShopEntityUseCase: UpdateShopEntityUseCase,
    private val mergeShopEntityUseCase: MergeShopEntityUseCase,
    private val deleteShopEntityUseCase: DeleteShopEntityUseCase,
) : ModifyShopViewModel() {
    private var mShop: ShopEntity? = null

    private val mMergeMessageShopName: MutableState<String> = mutableStateOf(String())
    val mergeMessageShopName
        get() = mMergeMessageShopName.value

    val chosenMergeCandidate: MutableState<ShopEntity?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    suspend fun checkExists(id: Long): Boolean {
        return shopRepository.get(id).first() != null
    }

    fun updateState(shopId: Long) =
        viewModelScope.launch {
            // skip state update for repeating shopId
            if (shopId == mShop?.id) return@launch

            screenState.name.apply { value = value.toLoading() }

            mShop = shopRepository.get(shopId).first()
            mMergeMessageShopName.value = mShop?.name.orEmpty()

            screenState.name.apply {
                value = mShop?.name?.let { Field.Loaded(it) } ?: value.toLoaded()
            }
        }

    /** @return list of merge candidates as flow */
    fun allMergeCandidates(shopId: Long): Flow<ImmutableList<ShopEntity>> {
        return shopRepository.all().map {
            it.filter { item -> item.id != shopId }.toImmutableList()
        }
    }

    suspend fun updateShop(shopId: Long): Boolean {
        screenState.attemptedToSubmit.value = true

        val result = updateShopEntityUseCase(id = shopId, name = screenState.name.value.data)

        return when (result) {
            is UpdateShopEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        UpdateShopEntityUseCaseResult.ShopIdInvalid -> {
                            Log.e("ModifyShop", "Insert invalid shop `${shopId}`")
                        }
                        UpdateShopEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        UpdateShopEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                false
            }
            is UpdateShopEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun deleteShop(shopId: Long): Boolean {
        val result = deleteShopEntityUseCase(shopId, screenState.deleteWarningConfirmed.value)

        return when (result) {
            is DeleteShopEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        DeleteShopEntityUseCaseResult.DangerousDelete -> {
                            screenState.showDeleteWarning.value = true
                        }
                        DeleteShopEntityUseCaseResult.ShopIdInvalid -> {
                            Log.e("ModifyShop", "Tried to delete shop with invalid id")
                        }
                    }
                }

                false
            }
            is DeleteShopEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun mergeWith(mergeCandidate: ShopEntity): ShopEntity? {
        if (mShop == null) {
            Log.e("ModifyShop", "Tried to merge shop without being set")
            return null
        }

        val result = mergeShopEntityUseCase(mShop!!.id, mergeCandidate.id)

        return when (result) {
            is MergeShopEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        MergeShopEntityUseCaseResult.MergeIntoIdInvalid -> {
                            Log.e("ModifyShop", "Tried to merge shop but merge id was invalid")
                        }
                        MergeShopEntityUseCaseResult.ShopIdInvalid -> {
                            Log.e("ModifyShop", "Tried to merge shop but id was invalid")
                        }
                    }
                }

                null
            }
            is MergeShopEntityUseCaseResult.Success -> {
                result.mergedEntity
            }
        }
    }
}
