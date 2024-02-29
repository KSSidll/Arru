package com.kssidll.arru.ui.screen.search.productlist


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = productRepository.allWithAltNamesFlow()
}
