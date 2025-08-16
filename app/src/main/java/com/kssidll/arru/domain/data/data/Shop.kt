package com.kssidll.arru.domain.data.data

import androidx.compose.runtime.Immutable
import com.kssidll.arru.data.data.ShopEntity

@Immutable
data class Shop(
    val id: Long
) {
    companion object {
        fun fromEntity(entity: ShopEntity): Shop {
            return Shop(
                id = entity.id
            )
        }
    }
}