package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket

@Dao
interface ImportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShops(entities: List<Shop>)

    @Query("DELETE FROM Shop")
    suspend fun deleteShops()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducers(entities: List<ProductProducer>)

    @Query("DELETE FROM ProductProducer")
    suspend fun deleteProducers()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(entities: List<ProductCategory>)

    @Query("DELETE FROM ProductCategory")
    suspend fun deleteCategories()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(entities: List<TransactionBasket>)

    @Query("DELETE FROM TransactionBasket")
    suspend fun deleteTransactions()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(entities: List<Product>)

    @Query("DELETE FROM Product")
    suspend fun deleteProducts()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(entities: List<ProductVariant>)

    @Query("DELETE FROM ProductVariant")
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
        shops: List<Shop>,
        producers: List<ProductProducer>,
        categories: List<ProductCategory>,
        transactions: List<TransactionBasket>,
        products: List<Product>,
        variants: List<ProductVariant>,
        entities: List<ItemEntity>
    ) {
        deleteAll()

        insertShops(shops)
        insertProducers(producers)
        insertCategories(categories)
        insertTransactions(transactions)
        insertProducts(products)
        insertVariants(variants)
        insertItems(entities)
    }
}