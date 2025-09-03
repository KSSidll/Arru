package com.kssidll.arru.data.view

import androidx.compose.runtime.Immutable
import androidx.room.DatabaseView
import androidx.room.Ignore
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.helper.generateRandomDate
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue

@DatabaseView(
    """
SELECT
    ItemEntity.id               AS id,
    ProductEntity.id            AS productId,
    productCategoryEntity.id    AS productCategoryId,
    productProducerEntity.id    AS productProducerId,
    productVariantEntity.id     AS productVariantId,
    TransactionEntity.id        AS transactionId,
    ShopEntity.id               AS shopId,
    ItemEntity.quantity         AS quantity,
    ItemEntity.price            AS price,
    TransactionEntity.date      AS date,
    ProductEntity.name          AS productName,
    ProductVariantEntity.name   AS productVariantName,
    ProductCategoryEntity.name  AS productCategoryName,
    ProductProducerEntity.name  AS productProducerName,
    ShopEntity.name             AS shopName
FROM ItemEntity
LEFT JOIN ProductEntity         ON ProductEntity.id         = ItemEntity.productEntityId
LEFT JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
LEFT JOIN ProductProducerEntity ON ProductProducerEntity.id = ProductEntity.productProducerEntityId
LEFT JOIN ProductVariantEntity  ON ProductVariantEntity.id  = ItemEntity.productVariantEntityId
LEFT JOIN TransactionEntity     ON TransactionEntity.id     = ItemEntity.transactionEntityId 
LEFT JOIN ShopEntity            ON ShopEntity.id            = TransactionEntity.shopEntityId
ORDER BY id DESC
""",
    viewName = "ItemView",
)
@Immutable
data class Item(
    val id: Long,
    val productId: Long,
    val productCategoryId: Long,
    val productProducerId: Long?,
    val productVariantId: Long?,
    val transactionId: Long,
    val shopId: Long?,
    val quantity: Long,
    val price: Long,
    val date: Long,
    val productName: String,
    val productVariantName: String?,
    val productCategoryName: String,
    val productProducerName: String?,
    val shopName: String?,
) {
    @Ignore
    fun actualQuantity(): Float {
        return ItemEntity.actualQuantity(quantity)
    }

    @Ignore
    fun actualPrice(): Float {
        return ItemEntity.actualPrice(price)
    }

    companion object {
        @Ignore
        fun generate(itemId: Long = 0): Item {
            return Item(
                id = itemId,
                productId = generateRandomLongValue(),
                productCategoryId = generateRandomLongValue(),
                productProducerId = generateRandomLongValue(),
                productVariantId = generateRandomLongValue(),
                transactionId = generateRandomLongValue(),
                shopId = generateRandomLongValue(),
                quantity = generateRandomLongValue(),
                price = generateRandomLongValue(),
                date = generateRandomDate().time,
                productName = generateRandomStringValue(),
                productVariantName = generateRandomStringValue(),
                productCategoryName = generateRandomStringValue(),
                productProducerName = generateRandomStringValue(),
                shopName = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<Item> {
            return List(amount) { generate(it.toLong()) }
        }
    }
}
