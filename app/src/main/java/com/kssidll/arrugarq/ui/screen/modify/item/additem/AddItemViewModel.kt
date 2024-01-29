package com.kssidll.arrugarq.ui.screen.modify.item.additem

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    override val itemRepository: ItemRepositorySource,
    override val productRepository: ProductRepositorySource,
    override val variantsRepository: VariantRepositorySource,
    override val shopRepository: ShopRepositorySource,
): ModifyItemViewModel() {

    init {
        loadLastItem()
    }

    /**
     * Tries to add item to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addItem(): Long? = viewModelScope.async {
        //        screenState.attemptedToSubmit.value = true
        //        screenState.validate()
        //
        //        val item = screenState.extractDataOrNull() ?: return@async null
        //
        //        return@async itemRepository.insert(item)
        return@async 1L
        // TODO add use case
    }
        .await()
}