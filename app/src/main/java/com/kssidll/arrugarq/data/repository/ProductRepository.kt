package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ProductRepository(private val dao: ProductDao): ProductRepositorySource {
    // Create

    override suspend fun insert(product: Product): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertAltName(
        product: Product,
        alternativeName: String
    ): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun update(products: List<Product>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAltName(
        id: Long,
        alternativeName: String
    ) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(products: List<Product>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAltName(alternativeName: ProductAltName) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAltName(alternativeNames: List<ProductAltName>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(productId: Long): Product? {
        //        TODO("Not yet implemented")
        return null
    }

    override fun totalSpentFlow(product: Product): Flow<Float> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByDayFlow(product: Product): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByWeekFlow(product: Product): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByMonthFlow(product: Product): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByYearFlow(product: Product): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override suspend fun fullItems(
        product: Product,
        count: Int,
        offset: Int
    ): List<FullItem> {
        //        TODO("Not yet implemented")
        return emptyList()
    }

    override suspend fun newestItem(product: Product): Item? {
        //        TODO("Not yet implemented")
        return null
    }

    override fun allWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun averagePriceByVariantByShopByMonthFlow(product: Product): Flow<List<ProductPriceByShopByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun allFlow(): Flow<List<Product>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }
}