package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ExportDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

class ExportRepository(private val dao: ExportDao) : ExportRepositorySource {
    override suspend fun shopCount(): Int = dao.shopCount()

    override suspend fun transactionCount(): Int = dao.transactionCount()

    override suspend fun productProducerCount(): Int = dao.productProducerCount()

    override suspend fun productCategoryCount(): Int = dao.productCategoryCount()

    override suspend fun productCount(): Int = dao.productCount()

    override suspend fun productVariantCount(): Int = dao.productVariantCount()

    override suspend fun itemCount(): Int = dao.itemCount()

    override suspend fun shopPagedList(limit: Int, offset: Int): List<ShopEntity> =
        dao.shopPagedList(limit, offset)

    override suspend fun transactionPagedList(limit: Int, offset: Int): List<TransactionEntity> =
        dao.transactionPagedList(limit, offset)

    override suspend fun productProducerPagedList(
        limit: Int,
        offset: Int,
    ): List<ProductProducerEntity> = dao.productProducerPagedList(limit, offset)

    override suspend fun productCategoryPagedList(
        limit: Int,
        offset: Int,
    ): List<ProductCategoryEntity> = dao.productCategoryPagedList(limit, offset)

    override suspend fun productPagedList(limit: Int, offset: Int): List<ProductEntity> =
        dao.productPagedList(limit, offset)

    override suspend fun productVariantPagedList(
        limit: Int,
        offset: Int,
    ): List<ProductVariantEntity> = dao.productVariantPagedList(limit, offset)

    override suspend fun itemPagedList(limit: Int, offset: Int): List<ItemEntity> =
        dao.itemPagedList(limit, offset)

    override suspend fun getShop(id: Long): ShopEntity? = dao.getShop(id)

    override suspend fun getProductProducer(id: Long): ProductProducerEntity? =
        dao.getProductProducer(id)

    override suspend fun getProductCategory(id: Long): ProductCategoryEntity? =
        dao.getProductCategory(id)

    override suspend fun getProduct(id: Long): ProductEntity? = dao.getProduct(id)

    override suspend fun getProductVariant(id: Long): ProductVariantEntity? =
        dao.getProductVariant(id)

    override suspend fun getItemsByTransaction(transactionId: Long): List<ItemEntity> =
        dao.getItemsByTransaction(transactionId)
}
