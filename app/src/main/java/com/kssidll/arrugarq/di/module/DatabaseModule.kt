package com.kssidll.arrugarq.di.module

import android.content.Context
import androidx.room.Room
import com.kssidll.arrugarq.data.dao.ItemDao
import com.kssidll.arrugarq.data.dao.ProductCategoryDao
import com.kssidll.arrugarq.data.dao.ProductCategoryTypeDao
import com.kssidll.arrugarq.data.dao.ProductDao
import com.kssidll.arrugarq.data.dao.ShopDao
import com.kssidll.arrugarq.data.database.AppDatabase
import com.kssidll.arrugarq.data.repository.IItemRepository
import com.kssidll.arrugarq.data.repository.IProductCategoryRepository
import com.kssidll.arrugarq.data.repository.IProductCategoryTypeRepository
import com.kssidll.arrugarq.data.repository.IProductRepository
import com.kssidll.arrugarq.data.repository.IShopRepository
import com.kssidll.arrugarq.data.repository.ItemRepository
import com.kssidll.arrugarq.data.repository.ProductCategoryRepository
import com.kssidll.arrugarq.data.repository.ProductCategoryTypeRepository
import com.kssidll.arrugarq.data.repository.ProductRepository
import com.kssidll.arrugarq.data.repository.ShopRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
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
    fun provideProductCategoryDao(appDatabase: AppDatabase): ProductCategoryDao {
        return appDatabase.getProductCategoryDao()
    }

    @Provides
    fun provideProductCategoryRepository(productCategoryDao: ProductCategoryDao): IProductCategoryRepository {
        return ProductCategoryRepository(productCategoryDao)
    }


    @Provides
    fun provideProductCategoryTypeDao(appDatabase: AppDatabase): ProductCategoryTypeDao {
        return appDatabase.getProductCategoryTypeDao()
    }

    @Provides
    fun provideProductCategoryTypeRepository(productCategoryTypeDao: ProductCategoryTypeDao): IProductCategoryTypeRepository {
        return ProductCategoryTypeRepository(productCategoryTypeDao)
    }

    @Provides
    fun provideShopDao(appDatabase: AppDatabase): ShopDao {
        return appDatabase.getShopDao()
    }

    @Provides
    fun provideShopRepository(shopDao: ShopDao): IShopRepository {
        return ShopRepository(shopDao)
    }
}
