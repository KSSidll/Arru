package com.kssidll.arru.ui.screen.search.productlist


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = productRepository.allWithAltNamesFlow()
}
