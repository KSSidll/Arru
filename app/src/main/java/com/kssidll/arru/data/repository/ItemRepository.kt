package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.data.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable

class ItemRepository(private val dao: ItemEntityDao) : ItemRepositorySource {
    // Create

    override suspend fun insert(entity: ItemEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: ItemEntity) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: ItemEntity) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<ItemEntity?> = dao.get(id).cancellable()

    override fun newest(): Flow<ItemEntity?> = dao.newest().cancellable()

    override fun newestByProduct(id: Long): Flow<ItemEntity?> =
        dao.newestByProduct(id).cancellable()
}
