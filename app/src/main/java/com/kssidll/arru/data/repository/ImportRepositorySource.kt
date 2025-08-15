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
        productProducerEntities: List<ProductProducerEntity>,
        productCategoryEntities: List<ProductCategoryEntity>,
        transactionEntities: List<TransactionEntity>,
        productEntities: List<ProductEntity>,
        productVariantEntities: List<ProductVariantEntity>,
        itemEntities: List<ItemEntity>
    )
}