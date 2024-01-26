package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface TransactionBasketDao {
    @Query("SELECT * FROM transactionbasket ORDER BY id ASC")
    suspend fun allTransactionBaskets(): List<TransactionBasket>

    @Query("SELECT * FROM transactionbasket ORDER BY id ASC")
    fun allTransactionBasketsFlow(): Flow<List<TransactionBasket>>

    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
    suspend fun shopById(shopId: Long): Shop

    @Query("SELECT item.* FROM transactionbasketitem JOIN item ON item.id = transactionbasketitem.itemId WHERE transactionbasketitem.transactionBasketId = :transactionBasketId")
    suspend fun itemsByTransactionBasketId(transactionBasketId: Long): List<Item>

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product

    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariant

    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategory


    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducer

    @Transaction
    suspend fun fullItemsByTransactionBasketId(transactionBasketId: Long): List<FullItem> {
        val items = itemsByTransactionBasketId(transactionBasketId)

        return items.map {
            val product = productById(it.productId)
            FullItem(
                embeddedItem = EmbeddedItem(
                    item = it,
                    product = product,
                    variant = it.variantId?.let { variantById(it) },
                ),
                embeddedProduct = EmbeddedProduct(
                    product = product,
                    category = categoryById(product.categoryId),
                    producer = product.producerId?.let { producerById(it) },
                )
            )
        }
    }

    fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>> {
        val transactionBaskets = allTransactionBasketsFlow()

        return transactionBaskets.map { baskets ->
            baskets.map { basket ->
                TransactionBasketWithItems(
                    id = basket.id,
                    date = basket.date,
                    shop = basket.shopId?.let { shopById(it) },
                    totalCost = basket.totalCost,
                    items = fullItemsByTransactionBasketId(basket.id),
                )
            }
        }
    }

    @Insert
    suspend fun insert(transactionBasket: TransactionBasket): Long

    @Update
    suspend fun update(transactionBasket: TransactionBasket)

    @Delete
    suspend fun delete(transactionBasket: TransactionBasket)
}