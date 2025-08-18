package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

@Dao
interface ImportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopEntities(entities: List<ShopEntity>)

    @Query("DELETE FROM ShopEntity") suspend fun deleteShopEntities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducerEntities(entities: List<ProductProducerEntity>)

    @Query("DELETE FROM ProductProducerEntity") suspend fun deleteProducerEntities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryEntities(entities: List<ProductCategoryEntity>)

    @Query("DELETE FROM ProductCategoryEntity") suspend fun deleteCategoryEntities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionEntities(entities: List<TransactionEntity>)

    @Query("DELETE FROM TransactionEntity") suspend fun deleteTransactionEntities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductEntities(entities: List<ProductEntity>)

    @Query("DELETE FROM ProductEntity") suspend fun deleteProductEntities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariantEntities(entities: List<ProductVariantEntity>)

    @Query("DELETE FROM ProductVariantEntity") suspend fun deleteVariantEntities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntities(entities: List<ItemEntity>)

    @Query("DELETE FROM ItemEntity") suspend fun deleteItemEntities()

    @Transaction
    suspend fun deleteAll() {
        deleteItemEntities()
        deleteVariantEntities()
        deleteProductEntities()
        deleteTransactionEntities()
        deleteCategoryEntities()
        deleteProducerEntities()
        deleteShopEntities()
    }

    @Transaction
    suspend fun insertAll(
        shopEntities: List<ShopEntity>,
        producers: List<ProductProducerEntity>,
        categories: List<ProductCategoryEntity>,
        transactionEntities: List<TransactionEntity>,
        productEntities: List<ProductEntity>,
        variantEntities: List<ProductVariantEntity>,
        itemEntities: List<ItemEntity>,
    ) {
        deleteAll()

        insertShopEntities(shopEntities)
        insertProducerEntities(producers)
        insertCategoryEntities(categories)
        insertTransactionEntities(transactionEntities)
        insertProductEntities(productEntities)
        insertVariantEntities(variantEntities)
        insertItemEntities(itemEntities)
    }
}
