package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionEntity

interface ImportRepositorySource {
    suspend fun insertAll(
        shops: List<Shop>,
        producers: List<ProductProducer>,
        categories: List<ProductCategory>,
        transactions: List<TransactionEntity>,
        products: List<Product>,
        variants: List<ProductVariant>,
        entities: List<ItemEntity>
    )
}