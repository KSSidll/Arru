package com.kssidll.arrugarq.ui.additem

import androidx.lifecycle.ViewModel
import com.kssidll.arrugarq.data.repository.IItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    itemRepository: IItemRepository,
) : ViewModel() {
    private val itemRepository: IItemRepository

    init {
        this.itemRepository = itemRepository
    }
}