package com.kssidll.arru.domain.data.data

import androidx.compose.runtime.Immutable
import com.kssidll.arru.data.data.ProductEntity

@Immutable
data class Product(
    val id: Long
) {
    companion object {
        fun fromEntity(entity: ProductEntity): Product {
            return Product(
                id = entity.id
            )
        }
    }
}