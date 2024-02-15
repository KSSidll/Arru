package com.kssidll.arrugarq.ui.screen.modify.shop.editshop


import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.MergeResult
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.UpdateResult
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditShopViewModel @Inject constructor(
    override val shopRepository: ShopRepositorySource,
): ModifyShopViewModel() {
    private val mMergeMessageShopName: MutableState<String> = mutableStateOf(String())
    val mergeMessageShopName get() = mMergeMessageShopName.value

    val chosenMergeCandidate: MutableState<Shop?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun updateState(shopId: Long): Boolean {
        return super.updateState(shopId)
            .also {
                mMergeMessageShopName.value = mShop?.name.orEmpty()
            }
    }

    /**
     * Tries to update shop with provided [shopId] with current screen state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateShop(shopId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = shopRepository.update(
            shopId = shopId,
            name = screenState.name.value.data.orEmpty()
        )

        if (result.isError()) {
            when (result.error!!) {
                UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update shop with invalid shop id in EditShopViewModel"
                    )
                    return@async UpdateResult.Success
                }

                UpdateResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete shop with provided [shopId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteShop(shopId: Long) = viewModelScope.async {
        val result = shopRepository.delete(
            shopId,
            screenState.deleteWarningConfirmed.value
        )

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete shop with invalid shop id in EditShopViewModel"
                    )
                    return@async DeleteResult.Success
                }

                DeleteResult.DangerousDelete -> {
                    screenState.showDeleteWarning.value = true
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete merge shop into provided [mergeCandidate]
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: Shop) = viewModelScope.async {
        if (mShop == null) {
            Log.e(
                "InvalidId",
                "Tried to merge shop without the shop being set in EditShopViewModel"
            )
            return@async MergeResult.Success
        }

        val result = shopRepository.merge(
            mShop!!,
            mergeCandidate
        )

        if (result.isError()) {
            when (result.error!!) {
                MergeResult.InvalidShop -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge shop without the shop being set in EditShopViewModel"
                    )
                    return@async MergeResult.Success
                }

                MergeResult.InvalidMergingInto -> {
                    Log.e(
                        "InvalidId",
                        "Tried to merge shop without the shop being set in EditShopViewModel"
                    )
                    return@async MergeResult.Success
                }
            }
        }

        return@async result
    }
        .await()
}
