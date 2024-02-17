package com.kssidll.arrugarq.ui.screen.modify.transaction

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class ModifyTransactionViewModel: ViewModel() {
    protected abstract val shopRepository: ShopRepositorySource
    internal val screenState: ModifyTransactionScreenState = ModifyTransactionScreenState()

    private var mShopListener: Job? = null

    /**
     * @return List of all shops
     */
    fun allShops(): Flow<List<Shop>> {
        return shopRepository.allFlow()
    }

    suspend fun setSelectedShopToProvided(providedShopId: Long?) {
        if (providedShopId != null) {
            screenState.selectedShop.apply { value = value.toLoading() }
            onNewShopSelected(shopRepository.get(providedShopId))
        }
    }

    fun onNewShopSelected(shop: Shop?) {
        screenState.selectedShop.value = Field.Loaded(shop)

        mShopListener?.cancel()
        if (shop != null) {
            mShopListener = viewModelScope.launch {
                shopRepository.getFlow(shop.id)
                    .collectLatest {
                        screenState.selectedShop.value = Field.Loaded(it)
                    }
            }
        }
    }
}

data class ModifyTransactionScreenState(
    val date: MutableState<Field<Long>> = mutableStateOf(Field.Loaded()),
    val totalCost: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val selectedShop: MutableState<Field<Shop?>> = mutableStateOf(Field.Loaded()),

    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isShopSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
): ModifyScreenState() {

    /**
     * Sets all fields to Loading status
     */
    fun allToLoading() {
        date.apply { value = value.toLoading() }
        totalCost.apply { value = value.toLoading() }
        selectedShop.apply { value = value.toLoading() }
    }
}