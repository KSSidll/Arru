package com.kssidll.arru.domain.data.data

import androidx.compose.runtime.Immutable
import com.kssidll.arru.data.data.ProductCategoryEntity

@Immutable
data class ProductCategory(
    val id: Long
) {
    companion object {
        fun fromEntity(entity: ProductCategoryEntity): ProductCategory {
            return ProductCategory(
                id = entity.id
            )
        }
    }
}