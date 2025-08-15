package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ImportDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

class ImportRepository(private val dao: ImportDao): ImportRepositorySource {
    override suspend fun insertAll(
        shopEntities: List<ShopEntity>,
        producers: List<ProductProducerEntity>,
        categories: List<ProductCategoryEntity>,
        transactions: List<TransactionEntity>,
        productEntities: List<ProductEntity>,
        variants: List<ProductVariantEntity>,
        entities: List<ItemEntity>
    ) {
        dao.insertAll(
            shopEntities = shopEntities,
            producers = producers,
            categories = categories,
            transactionEntities = transactions,
            productEntities = productEntities,
            variantEntities = variants,
            itemEntities = entities
        )
    }

}