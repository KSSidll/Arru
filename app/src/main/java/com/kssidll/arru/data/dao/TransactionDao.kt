package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Create

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity): Long

    // Update

    @Update
    suspend fun update(transactionEntity: TransactionEntity)

    // Delete

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    // Read

    /**
     * @return sum of [TransactionEntity] totalCost
     */
    @Query("SELECT SUM(totalCost) FROM TransactionEntity")
    fun totalSpent(): Flow<Long?>

    /**
     * @return count of [TransactionEntity] objects
     */
    @Query("SELECT COUNT(*) FROM TransactionEntity")
    fun count(): Flow<Int>

    /**
     * @return all [TransactionEntity] objects as [PagingSource]
     */
    @Query("SELECT * FROM TransactionEntity")
    fun allPagingSource(): PagingSource<Int, TransactionEntity>
}