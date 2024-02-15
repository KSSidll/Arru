package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import kotlinx.coroutines.flow.*

class ProductRepository(private val dao: ProductDao): ProductRepositorySource {
    // Create

    override suspend fun insert(product: Product): Long {
        return dao.insert(product)
    }

    override suspend fun insertAltName(alternativeName: ProductAltName): Long {
        return insertAltName(alternativeName)
    }

    // Update

    override suspend fun update(product: Product) {
        dao.update(product)
    }

    override suspend fun updateAltName(alternativeName: ProductAltName) {
        dao.updateAltName(alternativeName)
    }

    // Delete

    override suspend fun delete(product: Product) {
        dao.delete(product)
    }

    override suspend fun deleteAltName(alternativeName: ProductAltName) {
        dao.deleteAltName(alternativeName)
    }

    // Read

    override suspend fun get(productId: Long): Product? {
        return dao.get(productId)
    }

    override fun totalSpentFlow(product: Product): Flow<Long> {
        return dao.totalSpentFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByDayFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByWeekFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByMonthFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByYearFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun fullItemsPagedFlow(product: Product): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            product.id,
                            loadSize,
                            start
                        )
                    }
                )
            }
        )
            .flow
    }

    override suspend fun newestItem(product: Product): Item? {
        return dao.newestItem(product.id)
    }

    override fun allWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return dao.allWithAltNamesFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun averagePriceByVariantByShopByMonthFlow(product: Product): Flow<List<ProductPriceByShopByTime>> {
        return dao.averagePriceByVariantByShopByMonthFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun allFlow(): Flow<List<Product>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}