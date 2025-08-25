package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.TransactionEntity

interface BackupRepositorySource {
    /** @return long representing total amount of [TransactionEntity] */
    suspend fun transactionCount(): Long

    /** @return long representing total spending for [TransactionEntity] */
    suspend fun transactionTotalSpent(): Long
}
