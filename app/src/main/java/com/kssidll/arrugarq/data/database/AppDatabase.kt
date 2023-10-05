package com.kssidll.arrugarq.data.database

import androidx.room.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*

@Database(
    version = 2,
    entities = [
        Item::class,
        Product::class,
        ProductVariant::class,
        ProductAltName::class,
        ProductCategory::class,
        ProductCategoryAltName::class,
        Shop::class,
        ProductProducer::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getItemDao(): ItemDao
    abstract fun getProductDao(): ProductDao
    abstract fun getProductVariantDao(): ProductVariantDao
    abstract fun getProductCategoryDao(): ProductCategoryDao
    abstract fun getShopDao(): ShopDao
    abstract fun getProductProducerDao(): ProductProducerDao
}
