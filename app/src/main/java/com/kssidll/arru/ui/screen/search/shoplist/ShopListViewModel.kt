package com.kssidll.arru.ui.screen.search.shoplist


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ShopRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = shopRepository.allFlow()
}
