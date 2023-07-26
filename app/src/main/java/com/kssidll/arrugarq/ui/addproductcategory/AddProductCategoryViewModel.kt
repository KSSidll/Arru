package com.kssidll.arrugarq.ui.addproductcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.ProductCategory
import com.kssidll.arrugarq.data.repository.IProductCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductCategoryViewModel @Inject constructor(
    categoryRepository: IProductCategoryRepository,
) : ViewModel() {
    private val categoryRepository : IProductCategoryRepository

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