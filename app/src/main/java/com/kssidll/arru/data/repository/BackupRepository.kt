package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.BackupDao

class BackupRepository(private val dao: BackupDao) : BackupRepositorySource {
    override suspend fun transactionCount(): Long = dao.transactionCount()

    override suspend fun transactionTotalSpent(): Long = dao.transactionTotalSpent()
}
