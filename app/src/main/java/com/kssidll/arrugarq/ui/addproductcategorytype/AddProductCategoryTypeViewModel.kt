package com.kssidll.arrugarq.ui.addproductcategorytype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.ProductCategoryType
import com.kssidll.arrugarq.data.repository.IProductCategoryTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductCategoryTypeViewModel @Inject constructor(
    productCategoryTypeRepository: IProductCategoryTypeRepository,
) : ViewModel() {
    private val productCategoryTypeRepository: IProductCategoryTypeRepository

    init {
        this.productCategoryTypeRepository = productCategoryTypeRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addType(typeData: AddProductCategoryTypeData) = viewModelScope.launch {
        productCategoryTypeRepository.insert(
            ProductCategoryType(
                name = typeData.name,
            )
        )
    }
}
