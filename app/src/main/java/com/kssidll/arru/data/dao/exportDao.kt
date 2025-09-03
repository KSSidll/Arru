package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

@Dao
interface ExportDao {

    @Query("SELECT COUNT(*) FROM ShopEntity") suspend fun shopCount(): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity") suspend fun transactionCount(): Int

    @Query("SELECT COUNT(*) FROM ProductProducerEntity") suspend fun productProducerCount(): Int

    @Query("SELECT COUNT(*) FROM ProductCategoryEntity") suspend fun productCategoryCount(): Int

    @Query("SELECT COUNT(*) FROM ProductEntity") suspend fun productCount(): Int

    @Query("SELECT COUNT(*) FROM ProductVariantEntity") suspend fun productVariantCount(): Int

    @Query("SELECT COUNT(*) FROM ItemEntity") suspend fun itemCount(): Int

    @Query("SELECT ShopEntity.* FROM ShopEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun shopPagedList(limit: Int, offset: Int): List<ShopEntity>

    @Query(
        "SELECT TransactionEntity.* FROM TransactionEntity ORDER BY id LIMIT :limit OFFSET :offset"
    )
    suspend fun transactionPagedList(limit: Int, offset: Int): List<TransactionEntity>

    @Query(
        "SELECT ProductProducerEntity.* FROM ProductProducerEntity ORDER BY id LIMIT :limit OFFSET :offset"
    )
    suspend fun productProducerPagedList(limit: Int, offset: Int): List<ProductProducerEntity>

    @Query(
        "SELECT ProductCategoryEntity.* FROM ProductCategoryEntity ORDER BY id LIMIT :limit OFFSET :offset"
    )
    suspend fun productCategoryPagedList(limit: Int, offset: Int): List<ProductCategoryEntity>

    @Query("SELECT ProductEntity.* FROM ProductEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun productPagedList(limit: Int, offset: Int): List<ProductEntity>

    @Query(
        "SELECT ProductVariantEntity.* FROM ProductVariantEntity ORDER BY id LIMIT :limit OFFSET :offset"
    )
    suspend fun productVariantPagedList(limit: Int, offset: Int): List<ProductVariantEntity>

    @Query("SELECT ItemEntity.* FROM ItemEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun itemPagedList(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE id = :id")
    suspend fun getShop(id: Long): ShopEntity?

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE id = :id")
    suspend fun getProductProducer(id: Long): ProductProducerEntity?

    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity WHERE id = :id")
    suspend fun getProductCategory(id: Long): ProductCategoryEntity?

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE id = :id")
    suspend fun getProduct(id: Long): ProductEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE id = :id")
    suspend fun getProductVariant(id: Long): ProductVariantEntity?

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE transactionEntityId = :transactionId")
    suspend fun getItemsByTransaction(transactionId: Long): List<ItemEntity>
}
