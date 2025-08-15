package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

interface ImportRepositorySource {
    suspend fun insertAll(
        shopEntities: List<ShopEntity>,
        producers: List<ProductProducerEntity>,
        categories: List<ProductCategoryEntity>,
        transactions: List<TransactionEntity>,
        productEntities: List<ProductEntity>,
        variants: List<ProductVariantEntity>,
        entities: List<ItemEntity>
    )
}