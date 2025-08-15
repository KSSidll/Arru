package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

@Dao
interface ImportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShops(entities: List<ShopEntity>)

    @Query("DELETE FROM ShopEntity")
    suspend fun deleteShops()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducers(entities: List<ProductProducerEntity>)

    @Query("DELETE FROM ProductProducerEntity")
    suspend fun deleteProducers()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(entities: List<ProductCategory>)

    @Query("DELETE FROM ProductCategory")
    suspend fun deleteCategories()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(entities: List<TransactionEntity>)

    @Query("DELETE FROM TransactionEntity")
    suspend fun deleteTransactions()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(entities: List<Product>)

    @Query("DELETE FROM Product")
    suspend fun deleteProducts()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(entities: List<ProductVariantEntity>)

    @Query("DELETE FROM ProductVariantEntity")
    suspend fun deleteVariants()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(entities: List<ItemEntity>)

    @Query("DELETE FROM ItemEntity")
    suspend fun deleteItems()


    @Transaction
    suspend fun deleteAll() {
        deleteItems()
        deleteVariants()
        deleteProducts()
        deleteTransactions()
        deleteCategories()
        deleteProducers()
        deleteShops()
    }

    @Transaction
    suspend fun insertAll(
        shopEntities: List<ShopEntity>,
        producers: List<ProductProducerEntity>,
        categories: List<ProductCategory>,
        transactionEntities: List<TransactionEntity>,
        products: List<Product>,
        variantEntities: List<ProductVariantEntity>,
        itemEntities: List<ItemEntity>
    ) {
        deleteAll()

        insertShops(shopEntities)
        insertProducers(producers)
        insertCategories(categories)
        insertTransactions(transactionEntities)
        insertProducts(products)
        insertVariants(variantEntities)
        insertItems(itemEntities)
    }
}