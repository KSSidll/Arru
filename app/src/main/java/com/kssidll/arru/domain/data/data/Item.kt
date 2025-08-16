package com.kssidll.arru.domain.data.data

import androidx.compose.runtime.Immutable
import com.kssidll.arru.data.data.ItemEntity

@Immutable
data class Item(
    val id: Long
) {
    companion object {
        fun fromEntity(entity: ItemEntity): Item {
            return Item(
                id = entity.id
            )
        }
    }
}