package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ItemDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ItemRepository(private val dao: ItemDao): ItemRepositorySource {

    // Create

    override suspend fun insert(
        transactionId: Long,
        quantity: Long,
        price: Long
    ): InsertResult {
        TODO()
    }

    // Update

    override suspend fun update(
        itemId: Long,
        quantity: Long,
        price: Long
    ): UpdateResult {
        TODO()
    }

    // Delete

    override suspend fun delete(itemId: Long): DeleteResult {
        TODO()
    }

    // Read

    override suspend fun newestFlow(): Flow<Data<ItemEntity?>> {
        //        return dao.newestFlow()
        //            .cancellable()
        //            .distinctUntilChanged()
        //            .map { Data.Loaded(it) }
        //            .onStart { Data.Loading<ItemEntity>() }
        return flowOf()
    }
}