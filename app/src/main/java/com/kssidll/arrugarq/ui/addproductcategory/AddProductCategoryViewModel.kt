package com.kssidll.arrugarq.ui.addproductcategory

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductCategoryViewModel @Inject constructor(
    categoryRepository: IProductCategoryRepository,
): ViewModel() {
    private val categoryRepository: IProductCategoryRepository

    init {
        this.categoryRepository = categoryRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addProductCategory(productCategoryData: AddProductCategoryData) = viewModelScope.launch {
        categoryRepository.insert(
            ProductCategory(
                name = productCategoryData.name.trim(),
            )
        )
    }
}