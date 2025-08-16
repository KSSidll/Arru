package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

interface ExportRepositorySource {
    suspend fun shopCount(): Int
    suspend fun transactionCount(): Int
    suspend fun productProducerCount(): Int
    suspend fun productCategoryCount(): Int
    suspend fun productCount(): Int
    suspend fun productVariantCount(): Int
    suspend fun itemCount(): Int

    suspend fun shopPagedList(limit: Int, offset: Int): List<ShopEntity>
    suspend fun transactionPagedList(limit: Int, offset: Int): List<TransactionEntity>
    suspend fun productProducerPagedList(limit: Int, offset: Int): List<ProductProducerEntity>
    suspend fun productCategoryPagedList(limit: Int, offset: Int): List<ProductCategoryEntity>
    suspend fun productPagedList(limit: Int, offset: Int): List<ProductEntity>
    suspend fun productVariantPagedList(limit: Int, offset: Int): List<ProductVariantEntity>
    suspend fun itemPagedList(limit: Int, offset: Int): List<ItemEntity>

    suspend fun getShop(id: Long): ShopEntity?
    suspend fun getProductProducer(id: Long): ProductProducerEntity?
    suspend fun getProductCategory(id: Long): ProductCategoryEntity?
    suspend fun getProduct(id: Long): ProductEntity?
    suspend fun getProductVariant(id: Long): ProductVariantEntity?

    suspend fun getItemsByTransaction(transactionId: Long): List<ItemEntity>
}