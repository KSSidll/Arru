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
    private val itemRepository: ItemRepositorySource,
    private val transactionRepository: TransactionBasketRepositorySource,
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
     * @return Whether the update was successful
     */
    suspend fun updateShop(shopId: Long) = viewModelScope.async {
        //        screenState.attemptedToSubmit.value = true
        //        screenState.validate()
        //
        //        val shop = screenState.extractDataOrNull(shopId) ?: return@async false
        //        val other = shopRepository.byName(shop.name)
        //
        //        if (other != null) {
        //            if (other.id == shopId) return@async true
        //
        //            screenState.name.apply {
        //                value = value.toError(FieldError.DuplicateValueError)
        //            }
        //
        //            chosenMergeCandidate.value = other
        //            showMergeConfirmDialog.value = true
        //
        //            return@async false
        //        } else {
        //            shopRepository.update(shop)
        //            return@async true
        //        }
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete shop with provided [shopId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteShop(shopId: Long) = viewModelScope.async {
        // return true if no such shop exists
        //        val shop = shopRepository.get(shopId) ?: return@async true
        //
        //        val items = itemRepository.byShop(shop)
        //
        //        if (items.isNotEmpty() && !screenState.deleteWarningConfirmed.value) {
        //            screenState.showDeleteWarning.value = true
        //            return@async false
        //        } else {
        //            itemRepository.delete(items)
        //            shopRepository.delete(shop)
        //            return@async true
        //        }
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete merge category into provided [mergeCandidate]
     * @return True if operation succeded, false otherwise
     */
    suspend fun mergeWith(mergeCandidate: Shop) = viewModelScope.async {
        //        val transactions = mShop?.let { transactionRepository.byShop(it) } ?: return@async false
        //
        //        if (transactions.isNotEmpty()) {
        //            transactions.forEach { it.shopId = mergeCandidate.id }
        //            transactionRepository.update(transactions)
        //        }
        //
        //        mShop?.let { shopRepository.delete(it) }
        //
        //        return@async true
        return@async true
        // TODO add use case
    }
        .await()
}
