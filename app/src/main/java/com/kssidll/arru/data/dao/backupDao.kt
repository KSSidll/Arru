package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BackupDao {
    @Query("SELECT COUNT(*) FROM TransactionEntity") suspend fun transactionCount(): Long

    @Query("SELECT SUM(totalCost) FROM TransactionEntity") suspend fun transactionTotalSpent(): Long
}
