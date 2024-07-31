package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kssidll.arru.data.data.TagEntity
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

    /**
     * @param transactionId id of the transaction to get the first shop tag for
     * @return [TagEntity] representing the first shop tag of the [TransactionEntity] matching [transactionId]
     */
    @Query(
        """
        SELECT TagEntity.* FROM TagEntity
        JOIN TagTagEntity ON TagEntity.id = TagTagEntity.subTagId
        JOIN TransactionTagEntity ON TagEntity.id = TransactionTagEntity.tagId
        WHERE TransactionTagEntity.transactionId = :transactionId
        AND TagTagEntity.mainTagId = :shopTagId
        ORDER BY TagTagEntity.id ASC
        LIMIT 1
    """
    )
    suspend fun firstShopTag(
        transactionId: Long,
        shopTagId: Long = TagEntity.System.SHOP.id
    ): TagEntity?


    /**
     * @param transactionId id of the transaction to get the count of shop tags for
     * @return [Int] representing the count of shop tags that a [TransactionEntity] matching [transactionId] is tagged with
     */
    @Query(
        """
        SELECT COUNT(*) FROM TagEntity
        JOIN TagTagEntity ON TagEntity.id = TagTagEntity.subTagId
        JOIN TransactionTagEntity ON TagEntity.id = TransactionTagEntity.tagId
        WHERE TransactionTagEntity.transactionId = :transactionId
        AND TagTagEntity.mainTagId = :shopTagId
        ORDER BY TagTagEntity.id ASC
    """
    )
    suspend fun countShopTag(
        transactionId: Long,
        shopTagId: Long = TagEntity.System.SHOP.id
    ): Int
}