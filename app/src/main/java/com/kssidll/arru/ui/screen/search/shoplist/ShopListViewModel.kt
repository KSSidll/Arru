package com.kssidll.arru.ui.screen.search.shoplist


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val shopRepository: ShopRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = shopRepository.allFlow()
}
