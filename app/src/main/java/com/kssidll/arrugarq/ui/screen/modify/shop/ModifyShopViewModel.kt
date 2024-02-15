package com.kssidll.arrugarq.ui.screen.modify.shop

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Shop modification view models
 * @property screenState A [ModifyShopScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Shop matching provided id, only changes representation data and loading state
 */
abstract class ModifyShopViewModel: ViewModel() {
    protected abstract val shopRepository: ShopRepositorySource
    protected var mShop: Shop? = null
    internal val screenState: ModifyShopScreenState = ModifyShopScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [shopId] was valid, false otherwise
     */
    open suspend fun updateState(shopId: Long) = viewModelScope.async {
        screenState.name.apply { value = value.toLoading() }

        mShop = shopRepository.get(shopId)

        screenState.name.apply {
            value = mShop?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async mShop != null
    }
        .await()

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(shopId: Long): Flow<List<Shop>> {
        return shopRepository.allFlow()
            .onEach { it.filter { item -> item.id != shopId } }
            .distinctUntilChanged()
    }
}

/**
 * Data representing [ModifyShopScreenImpl] screen state
 */
data class ModifyShopScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState()