package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ImportDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

class ImportRepository(private val dao: ImportDao): ImportRepositorySource {
    override suspend fun insertAll(
        shopEntities: List<ShopEntity>,
        producers: List<ProductProducer>,
        categories: List<ProductCategory>,
        transactions: List<TransactionEntity>,
        products: List<Product>,
        variants: List<ProductVariant>,
        entities: List<ItemEntity>
    ) {
        dao.insertAll(
            shopEntities = shopEntities,
            producers = producers,
            categories = categories,
            transactionEntities = transactions,
            products = products,
            variants = variants,
            itemEntities = entities
        )
    }

}