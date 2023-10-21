package com.kssidll.arrugarq.data.database

import androidx.room.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*

@Database(
    version = 2,
    entities = [
        Item::class,
        Product::class,
        ProductAltName::class,
        ProductVariant::class,
        ProductCategory::class,
        ProductCategoryAltName::class,
        Shop::class,
        ProductProducer::class,
    ],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getItemDao(): ItemDao
    abstract fun getProductDao(): ProductDao
    abstract fun getVariantDao(): VariantDao
    abstract fun getCategoryDao(): CategoryDao
    abstract fun getShopDao(): ShopDao
    abstract fun getProducerDao(): ProducerDao
}
