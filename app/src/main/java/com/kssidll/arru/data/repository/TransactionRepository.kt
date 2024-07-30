package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kssidll.arru.data.dao.TransactionDao
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.model.TransactionPreview
import com.kssidll.arru.domain.model.TransactionSpentByTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TransactionRepository(private val dao: TransactionDao): TransactionRepositorySource {
    // Create

    override suspend fun insert(
        date: Long,
        totalCost: Long,
    ): InsertResult {
        TODO()
    }

    // Update

    override suspend fun update(
        transactionId: Long,
        date: Long,
        totalCost: Long,
    ): UpdateResult {
        TODO()
    }

    // Delete

    override suspend fun delete(
        transactionId: Long,
        force: Boolean
    ): DeleteResult {
        TODO()
    }

    // Read

    /**
     * @return sum of all totalCost of [TransactionEntity] objects if any
     */
    override suspend fun totalRawSpent(): Long {
        return dao.totalSpent()
            .firstOrNull() ?: 0
    }

    /**
     * @return count of [TransactionEntity] objects
     */
    override suspend fun count(): Int {
        return dao.count()
            .firstOrNull() ?: 0
    }

    /**
     * @return sum of all totalCost of [TransactionEntity] objects as [Flow] of [Data]
     */
    override fun totalSpentFlow(): Flow<Data<Float?>> {
        //        return dao.totalSpentFlow()
        //            .cancellable()
        //            .distinctUntilChanged()
        //            .map {
        //                Data.Loaded(
        //                    it?.toFloat()
        //                        ?.div(TransactionEntity.COST_DIVISOR)
        //                )
        //            }
        //            .onStart { Data.Loading<Long>() }
        return flowOf()
    }

    override fun totalSpentByDayFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        //        return dao.totalSpentByDayFlow()
        //            .cancellable()
        //            .distinctUntilChanged()
        //            .map { Data.Loaded(it) }
        //            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override fun totalSpentByWeekFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        //        return dao.totalSpentByWeekFlow()
        //            .cancellable()
        //            .distinctUntilChanged()
        //            .map { Data.Loaded(it) }
        //            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override fun totalSpentByMonthFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        //        return dao.totalSpentByMonthFlow()
        //            .cancellable()
        //            .distinctUntilChanged()
        //            .map { Data.Loaded(it) }
        //            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override fun totalSpentByYearFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        //        return dao.totalSpentByYearFlow()
        //            .cancellable()
        //            .distinctUntilChanged()
        //            .map { Data.Loaded(it) }
        //            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    /**
     * @return all [TransactionEntity] objects mapped to [TransactionPreview] as [Flow] of [PagingData]
     */
    override fun allPagedAsPreview(): Flow<PagingData<TransactionPreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                initialLoadSize = 24,
                prefetchDistance = 96,
                enablePlaceholders = true,
                jumpThreshold = 48
            ),
            pagingSourceFactory = {
                dao.allPagingSource()
            }
        ).flow
            .map { pagingData ->
                pagingData.map {
                    TransactionPreview(
                        id = it.id,
                        date = it.date,
                        totalCost = it.totalCost,
                        tags = emptyList()
                    )
                }
            }
    }
}