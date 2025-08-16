package com.kssidll.arru.domain.data.data

import androidx.compose.runtime.Immutable
import com.kssidll.arru.data.data.TransactionEntity

@Immutable
data class Transaction(
    val id: Long
) {
    companion object {
        fun fromEntity(entity: TransactionEntity): Transaction {
            return Transaction(
                id = entity.id
            )
        }
    }
}