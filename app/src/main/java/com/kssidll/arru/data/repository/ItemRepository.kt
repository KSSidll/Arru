package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.data.ItemEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class ItemRepository(private val dao: ItemEntityDao) : ItemRepositorySource {
    // Create

    override suspend fun insert(entity: ItemEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: ItemEntity) = dao.update(entity)

    override suspend fun update(entity: List<ItemEntity>) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: ItemEntity) = dao.delete(entity)

    override suspend fun delete(entity: List<ItemEntity>) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<ItemEntity?> = dao.get(id).cancellable()

    override fun byProductCategory(id: Long): Flow<ImmutableList<ItemEntity>> =
        dao.byProductCategory(id).cancellable().map { it.toImmutableList() }

    override fun byProductProducer(id: Long): Flow<ImmutableList<ItemEntity>> =
        dao.byProductProducer(id).cancellable().map { it.toImmutableList() }

    override fun byProduct(id: Long): Flow<ImmutableList<ItemEntity>> =
        dao.byProduct(id).cancellable().map { it.toImmutableList() }

    override fun newest(): Flow<ItemEntity?> = dao.newest().cancellable()

    override fun newestByProduct(id: Long): Flow<ItemEntity?> =
        dao.newestByProduct(id).cancellable()
}
