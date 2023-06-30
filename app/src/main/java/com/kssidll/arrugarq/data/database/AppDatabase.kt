package com.kssidll.arrugarq.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kssidll.arrugarq.data.dao.ItemDao
import com.kssidll.arrugarq.data.dao.ProductCategoryDao
import com.kssidll.arrugarq.data.dao.ProductCategoryTypeDao
import com.kssidll.arrugarq.data.dao.ProductDao
import com.kssidll.arrugarq.data.dao.ProductProducerDao
import com.kssidll.arrugarq.data.dao.ProductVariantDao
import com.kssidll.arrugarq.data.dao.ShopDao
import com.kssidll.arrugarq.data.data.Item
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.data.ProductAltName
import com.kssidll.arrugarq.data.data.ProductCategory
import com.kssidll.arrugarq.data.data.ProductCategoryAltName
import com.kssidll.arrugarq.data.data.ProductCategoryType
import com.kssidll.arrugarq.data.data.ProductProducer
import com.kssidll.arrugarq.data.data.ProductVariant
import com.kssidll.arrugarq.data.data.Shop

@Database(
    entities = [
        Item::class,
        Product::class,
        ProductVariant::class,
        ProductAltName::class,
        ProductCategory::class,
        ProductCategoryAltName::class,
        ProductCategoryType::class,
        Shop::class,
        ProductProducer::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getItemDao(): ItemDao
    abstract fun getProductDao(): ProductDao
    abstract fun getProductVariantDao(): ProductVariantDao
    abstract fun getProductCategoryDao(): ProductCategoryDao
    abstract fun getProductCategoryTypeDao(): ProductCategoryTypeDao
    abstract fun getShopDao(): ShopDao
    abstract fun getProductProducerDao(): ProductProducerDao
}
