package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ItemRepository(private val dao: ItemDao): ItemRepositorySource {
    // Create

    override suspend fun insert(item: Item): Long {
        return dao.insert(item)
    }

    // Update

    override suspend fun update(item: Item) {
        dao.update(item)
    }

    override suspend fun update(items: List<Item>) {
        dao.update(items)
    }

    // Delete

    override suspend fun delete(item: Item) {
        dao.delete(item)
    }

    override suspend fun delete(items: List<Item>) {
        dao.delete(items)
    }

    // Read

    override suspend fun get(itemId: Long): Item? {
        return dao.get(itemId)
    }

    override suspend fun newest(): Item? {
        return dao.newest()
    }

    override suspend fun newestFlow(): Flow<Item> {
        return dao.newestFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}