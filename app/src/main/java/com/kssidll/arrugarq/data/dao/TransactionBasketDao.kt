package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface TransactionBasketDao {
    // Create

    @Insert
    suspend fun insert(transactionBasket: TransactionBasket): Long

    @Insert
    suspend fun insertTransactionBasketItem(transactionBasketItem: TransactionBasketItem): Long

    // Update

    @Update
    suspend fun update(transactionBasket: TransactionBasket)

    @Update
    suspend fun update(transactionBaskets: List<TransactionBasket>)

    // Delete

    @Delete
    suspend fun delete(transactionBasket: TransactionBasket)

    @Delete
    suspend fun delete(transactionBaskets: List<TransactionBasket>)

    @Delete
    suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem)

    @Delete
    suspend fun deleteTransactionBasketItem(transactionBasketItems: List<TransactionBasketItem>)

    // Helper

    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
    suspend fun shopById(shopId: Long): Shop

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product

    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariant

    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategory

    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducer

    @Query("SELECT item.* FROM transactionbasketitem JOIN item ON item.id = transactionbasketitem.itemId WHERE transactionbasketitem.transactionBasketId = :transactionBasketId")
    suspend fun itemsByTransactionBasketId(transactionBasketId: Long): List<Item>

    @Transaction
    suspend fun fullItemsByTransactionBasketId(transactionBasketId: Long): List<FullItem> {
        val transactionBasket = get(transactionBasketId) ?: return emptyList()

        val items = itemsByTransactionBasketId(transactionBasketId)

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val product = productById(item.productId)
            val variant = item.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)
            val producer = product.producerId?.let { producerById(it) }
            val shop = transactionBasket.shopId?.let { shopById(it) }

            FullItem(
                id = item.id,
                quantity = item.quantity,
                price = item.price,
                product = product,
                variant = variant,
                category = category,
                producer = producer,
                date = transactionBasket.date,
                shop = shop,
            )
        }
    }

    // Read

    @Query("SELECT * FROM transactionbasket WHERE transactionbasket.id = :transactionBasketId")
    suspend fun get(transactionBasketId: Long): TransactionBasket?

    @Query("SELECT * FROM transactionbasket ORDER BY id ASC")
    suspend fun all(): List<TransactionBasket>

    @Query("SELECT * FROM transactionbasket ORDER BY id ASC")
    fun allFlow(): Flow<List<TransactionBasket>>

    fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>> {
        val transactionBaskets = allFlow()

        return transactionBaskets.map { baskets ->
            baskets.map { basket ->
                val shop = basket.shopId?.let { shopById(it) }
                val items = fullItemsByTransactionBasketId(basket.id)

                TransactionBasketWithItems(
                    id = basket.id,
                    date = basket.date,
                    shop = shop,
                    totalCost = basket.totalCost,
                    items = items,
                )
            }
        }
    }
}