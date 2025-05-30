package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.kssidll.arru.data.data.Item
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducers(entities: List<ProductProducer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(entities: List<ProductCategory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(entities: List<TransactionBasket>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(entities: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(entities: List<ProductVariant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(entities: List<Item>)

    @Transaction
    suspend fun insertAll(
        shops: List<Shop>,
        producers: List<ProductProducer>,
        categories: List<ProductCategory>,
        transactions: List<TransactionBasket>,
        products: List<Product>,
        variants: List<ProductVariant>,
        items: List<Item>
    ) {
        insertShops(shops)
        insertProducers(producers)
        insertCategories(categories)
        insertTransactions(transactions)
        insertProducts(products)
        insertVariants(variants)
        insertItems(items)
    }
}