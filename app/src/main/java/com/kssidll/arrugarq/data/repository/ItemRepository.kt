package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ItemRepository(private val dao: ItemDao): ItemRepositorySource {
    // Create

    override suspend fun insert(item: Item): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(item: Item) {
        TODO("Not yet implemented")
    }

    override suspend fun update(items: List<Item>) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(item: Item) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(items: List<Item>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(itemId: Long): Item? {
        TODO("Not yet implemented")
    }

    override suspend fun newest(): Item? {
        TODO("Not yet implemented")
    }

    override suspend fun newestFlow(): Flow<Item> {
        TODO("Not yet implemented")
    }
}