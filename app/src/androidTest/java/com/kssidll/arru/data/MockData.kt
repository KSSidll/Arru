package com.kssidll.arru.data

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity

val shopEntityMockData =
    listOf(
        ShopEntity(1, "ShopEntity_1"),
        ShopEntity(2, "ShopEntity_2"),
        ShopEntity(3, "ShopEntity_3"),
        ShopEntity(4, "ShopEntity_4"),
        ShopEntity(5, "ShopEntity_5"),
        ShopEntity(6, "ShopEntity_6"),
        ShopEntity(7, "ShopEntity_7"),
        ShopEntity(8, "ShopEntity_8"),
        ShopEntity(9, "ShopEntity_9"),
        ShopEntity(10, "ShopEntity_10"),
    )

val transactionEntityMockData =
    listOf(
        TransactionEntity(1, 1000, 1, 100, "TransactionEntity_1_NOTE"),
        TransactionEntity(2, 2000, 2, 200, "TransactionEntity_2_NOTE"),
        TransactionEntity(3, 3000, null, 300, null),
        TransactionEntity(4, 4000, 4, 400, "TransactionEntity_4_NOTE"),
        TransactionEntity(5, 5000, 5, 500, "TransactionEntity_5_NOTE"),
        TransactionEntity(6, 6000, 6, 600, null),
        TransactionEntity(7, 7000, null, 700, "TransactionEntity_7_NOTE"),
        TransactionEntity(8, 8000, 8, 800, "TransactionEntity_8_NOTE"),
        TransactionEntity(9, 9000, 9, 900, "TransactionEntity_9_NOTE"),
        TransactionEntity(10, 10000, 10, 1000, null),
    )

val productCategoryEntityMockData =
    listOf(
        ProductCategoryEntity(1, "ProductCategoryEntity_1"),
        ProductCategoryEntity(2, "ProductCategoryEntity_2"),
        ProductCategoryEntity(3, "ProductCategoryEntity_3"),
        ProductCategoryEntity(4, "ProductCategoryEntity_4"),
        ProductCategoryEntity(5, "ProductCategoryEntity_5"),
        ProductCategoryEntity(6, "ProductCategoryEntity_6"),
        ProductCategoryEntity(7, "ProductCategoryEntity_7"),
        ProductCategoryEntity(8, "ProductCategoryEntity_8"),
        ProductCategoryEntity(9, "ProductCategoryEntity_9"),
        ProductCategoryEntity(10, "ProductCategoryEntity_10"),
    )

val productProducerEntityMockData =
    listOf(
        ProductProducerEntity(1, "ProductProducerEntity_1"),
        ProductProducerEntity(2, "ProductProducerEntity_2"),
        ProductProducerEntity(3, "ProductProducerEntity_3"),
        ProductProducerEntity(4, "ProductProducerEntity_4"),
        ProductProducerEntity(5, "ProductProducerEntity_5"),
        ProductProducerEntity(6, "ProductProducerEntity_6"),
        ProductProducerEntity(7, "ProductProducerEntity_7"),
        ProductProducerEntity(8, "ProductProducerEntity_8"),
        ProductProducerEntity(9, "ProductProducerEntity_9"),
        ProductProducerEntity(10, "ProductProducerEntity_10"),
    )

val productEntityMockData =
    listOf(
        ProductEntity(1, 1, 1, "ProductEntity_1"),
        ProductEntity(2, 2, 2, "ProductEntity_2"),
        ProductEntity(3, 3, 3, "ProductEntity_3"),
        ProductEntity(4, 4, 4, "ProductEntity_4"),
        ProductEntity(5, 5, null, "ProductEntity_5"),
        ProductEntity(6, 6, 6, "ProductEntity_6"),
        ProductEntity(7, 7, 7, "ProductEntity_7"),
        ProductEntity(8, 8, null, "ProductEntity_8"),
        ProductEntity(9, 9, 9, "ProductEntity_9"),
        ProductEntity(10, 10, 10, "ProductEntity_10"),
    )

val productVariantEntityMockData =
    listOf(
        ProductVariantEntity(1, 1, "ProductVariantEntity_1"),
        ProductVariantEntity(2, 2, "ProductVariantEntity_2"),
        ProductVariantEntity(3, 3, "ProductVariantEntity_3"),
        ProductVariantEntity(4, 4, "ProductVariantEntity_4"),
        ProductVariantEntity(5, null, "ProductVariantEntity_5"),
        ProductVariantEntity(6, 6, "ProductVariantEntity_6"),
        ProductVariantEntity(7, 7, "ProductVariantEntity_7"),
        ProductVariantEntity(8, 8, "ProductVariantEntity_8"),
        ProductVariantEntity(9, null, "ProductVariantEntity_9"),
        ProductVariantEntity(10, 10, "ProductVariantEntity_10"),
    )

val itemEntityMockData =
    listOf(
        ItemEntity(1, 1, 1, 1, 1000, 100),
        ItemEntity(2, 1, 2, 2, 2000, 200),
        ItemEntity(3, 1, 3, 3, 3000, 300),
        ItemEntity(4, 1, 4, 4, 4000, 400),
        ItemEntity(5, 1, 5, null, 5000, 500),
        ItemEntity(6, 2, 6, 6, 6000, 600),
        ItemEntity(7, 2, 7, 7, 7000, 700),
        ItemEntity(8, 2, 8, 8, 8000, 800),
        ItemEntity(9, 2, 9, null, 9000, 900),
        ItemEntity(10, 2, 10, 10, 10000, 1000),
        ItemEntity(11, 3, 1, 1, 1000, 100),
        ItemEntity(12, 3, 2, 2, 2000, 200),
        ItemEntity(13, 3, 3, 3, 3000, 300),
        ItemEntity(14, 3, 4, 4, 4000, 400),
        ItemEntity(15, 3, 5, null, 5000, 500),
        ItemEntity(16, 4, 6, 6, 6000, 600),
        ItemEntity(17, 4, 7, 7, 7000, 700),
        ItemEntity(18, 4, 8, 8, 8000, 800),
        ItemEntity(19, 4, 9, null, 9000, 900),
        ItemEntity(20, 4, 10, 10, 10000, 1000),
        ItemEntity(21, 5, 1, 1, 1000, 100),
        ItemEntity(22, 5, 2, 2, 2000, 200),
        ItemEntity(23, 5, 3, 3, 3000, 300),
        ItemEntity(24, 5, 4, 4, 4000, 400),
        ItemEntity(25, 5, 5, null, 5000, 500),
        ItemEntity(26, 6, 6, 6, 6000, 600),
        ItemEntity(27, 6, 7, 7, 7000, 700),
        ItemEntity(28, 6, 8, 8, 8000, 800),
        ItemEntity(29, 6, 9, null, 9000, 900),
        ItemEntity(30, 6, 10, 10, 10000, 1000),
        ItemEntity(31, 7, 1, 1, 1000, 100),
        ItemEntity(32, 7, 2, 2, 2000, 200),
        ItemEntity(33, 7, 3, 3, 3000, 300),
        ItemEntity(34, 7, 4, 4, 4000, 400),
        ItemEntity(35, 7, 5, null, 5000, 500),
        ItemEntity(36, 8, 6, 6, 6000, 600),
        ItemEntity(37, 8, 7, 7, 7000, 700),
        ItemEntity(38, 8, 8, 8, 8000, 800),
        ItemEntity(39, 8, 9, null, 9000, 900),
        ItemEntity(40, 8, 10, 10, 10000, 1000),
        ItemEntity(41, 9, 1, 1, 1000, 100),
        ItemEntity(42, 9, 2, 2, 2000, 200),
        ItemEntity(43, 9, 3, 3, 3000, 300),
        ItemEntity(44, 9, 4, 4, 4000, 400),
        ItemEntity(45, 9, 5, null, 5000, 500),
        ItemEntity(46, 10, 6, 6, 6000, 600),
        ItemEntity(47, 10, 7, 7, 7000, 700),
        ItemEntity(48, 10, 8, 8, 8000, 800),
        ItemEntity(49, 10, 9, null, 9000, 900),
        ItemEntity(50, 10, 10, 10, 10000, 1000),
    )
