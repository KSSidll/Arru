package com.kssidll.arrugarq.ui.screen.modify.shop.editshop


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
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
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete shop with provided [shopId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteShop(shopId: Long) = viewModelScope.async {
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete merge shop into provided [mergeCandidate]
     * @return resulting [MergeResult]
     */
    suspend fun mergeWith(mergeCandidate: Shop) = viewModelScope.async {
        return@async true
        // TODO add use case
    }
        .await()
}
