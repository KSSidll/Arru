package com.kssidll.arru.ui.screen.modify.transaction

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

abstract class ModifyTransactionViewModel : ViewModel() {
    protected abstract val shopRepository: ShopRepositorySource
    internal val screenState: ModifyTransactionScreenState = ModifyTransactionScreenState()

    private var mShopListener: Job? = null

    /** @return List of all shops */
    fun allShops(): Flow<ImmutableList<ShopEntity>> {
        return shopRepository.all()
    }

    suspend fun setSelectedShopToProvided(providedShopId: Long?) {
        if (providedShopId != null) {
            screenState.selectedShop.apply { value = value.toLoading() }
            onNewShopSelected(shopRepository.get(providedShopId).first())
        }
    }

    fun onNewShopSelected(shop: ShopEntity?) {
        // Don't do anything if the shop is the same as already selected
        if (screenState.selectedShop.value.data == shop) {
            screenState.selectedShop.apply { value = value.toLoaded() }
            return
        }

        screenState.selectedShop.value = Field.Loaded(shop)

        mShopListener?.cancel()
        if (shop != null) {
            mShopListener =
                viewModelScope.launch {
                    shopRepository.get(shop.id).collectLatest {
                        screenState.selectedShop.value = Field.Loaded(it)
                    }
                }
        }
    }
}

data class ModifyTransactionScreenState(
    val date: MutableState<Field<Long>> = mutableStateOf(Field.Loaded()),
    val totalCost: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val selectedShop: MutableState<Field<ShopEntity?>> = mutableStateOf(Field.Loaded()),
    val note: MutableState<Field<String?>> = mutableStateOf(Field.Loaded()),
    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isShopSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
) : ModifyScreenState() {

    /** Sets all fields to Loading status */
    fun allToLoading() {
        date.apply { value = value.toLoading() }
        totalCost.apply { value = value.toLoading() }
        selectedShop.apply { value = value.toLoading() }
        note.apply { value = value.toLoading() }
    }
}
