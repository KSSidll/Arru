package com.kssidll.arrugarq.di.module

import android.content.*
import androidx.room.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.database.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.qualifiers.*
import dagger.hilt.components.*
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            AppDatabase::class.java,
            "arrugarq_database"
        )
            .createFromAsset("database/arrugarq.db")
            .build()
    }

    @Provides
    fun provideItemDao(appDatabase: AppDatabase): ItemDao {
        return appDatabase.getItemDao()
    }

    @Provides
    fun provideItemRepository(itemDao: ItemDao): IItemRepository {
        return ItemRepository(itemDao)
    }

    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.getProductDao()
    }

    @Provides
    fun provideProductRepository(productDao: ProductDao): IProductRepository {
        return ProductRepository(productDao)
    }

    @Provides
    fun provideProductVariantDao(appDatabase: AppDatabase): ProductVariantDao {
        return appDatabase.getProductVariantDao()
    }

    @Provides
    fun provideProductVariantRepository(productVariantDao: ProductVariantDao): IProductVariantRepository {
        return ProductVariantRepository(productVariantDao)
    }

    @Provides
    fun provideProductCategoryDao(appDatabase: AppDatabase): ProductCategoryDao {
        return appDatabase.getProductCategoryDao()
    }


    @Provides
    fun provideProductCategoryRepository(productCategoryDao: ProductCategoryDao): IProductCategoryRepository {
        return ProductCategoryRepository(productCategoryDao)
    }

    @Provides
    fun provideShopDao(appDatabase: AppDatabase): ShopDao {
        return appDatabase.getShopDao()
    }

    @Provides
    fun provideShopRepository(shopDao: ShopDao): IShopRepository {
        return ShopRepository(shopDao)
    }

    @Provides
    fun provideProductProducerDao(appDatabase: AppDatabase): ProductProducerDao {
        return appDatabase.getProductProducerDao()
    }

    @Provides
    fun provideProductProducerRepository(productProducerDao: ProductProducerDao): IProductProducerRepository {
        return ProductProducerRepository(productProducerDao)
    }
}
