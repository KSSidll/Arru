package com.kssidll.arrugarq.ui.addproductcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.ProductCategory
import com.kssidll.arrugarq.data.data.ProductCategoryType
import com.kssidll.arrugarq.data.repository.IProductCategoryRepository
import com.kssidll.arrugarq.data.repository.IProductCategoryTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductCategoryViewModel @Inject constructor(
    categoryRepository: IProductCategoryRepository,
    categoryTypeRepository: IProductCategoryTypeRepository
) : ViewModel() {
    private val categoryRepository : IProductCategoryRepository
    private val categoryTypeRepository: IProductCategoryTypeRepository

    var addProductCategoryState: AddProductCategoryState = AddProductCategoryState()
    init {
        this.categoryRepository = categoryRepository
        this.categoryTypeRepository = categoryTypeRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addProductCategory(productCategoryData: AddProductCategoryData) = viewModelScope.launch {
        categoryRepository.insert(
            ProductCategory(
                typeId = productCategoryData.typeId,
                name = productCategoryData.name.trim(),
            )
        )
    }

    fun getProductCategoryTypesFlow(): Flow<List<ProductCategoryType>> {
        return categoryTypeRepository.getAllFlow()
    }
}