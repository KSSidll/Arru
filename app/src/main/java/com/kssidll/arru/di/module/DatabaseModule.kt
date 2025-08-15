package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.data.dao.ImportDao
import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.dao.ProductEntityDao
import com.kssidll.arru.data.dao.ProductProducerEntityDao
import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.database.downloadsAppDirectory
import com.kssidll.arru.data.database.downloadsDbFile
import com.kssidll.arru.data.database.externalDbFile
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getDatabaseLocation
import com.kssidll.arru.data.repository.CategoryRepository
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ImportRepository
import com.kssidll.arru.data.repository.ImportRepositorySource
import com.kssidll.arru.data.repository.ItemRepository
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProducerRepository
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepository
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ShopRepository
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepository
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.data.repository.VariantRepository
import com.kssidll.arru.data.repository.VariantRepositorySource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        var databaseLocation: AppPreferences.Database.Location.Values
        runBlocking {
            databaseLocation = AppPreferences.getDatabaseLocation(context).first()
        }

        return when (databaseLocation) {
            AppPreferences.Database.Location.Values.EXTERNAL -> {
                AppDatabase.buildExternal(context, context.externalDbFile().absolutePath)
            }

            AppPreferences.Database.Location.Values.INTERNAL -> {
                AppDatabase.buildInternal(context)
            }

            AppPreferences.Database.Location.Values.DOWNLOADS -> {
                val downloadsAppDirectory = context.downloadsAppDirectory()

                // create in case it doesn't exist
                downloadsAppDirectory.mkdir()

                AppDatabase.buildExternal(context, context.downloadsDbFile().absolutePath)
            }

        }
    }

    @Provides
    fun provideImportDao(appDatabase: AppDatabase): ImportDao {
        return appDatabase.getImportDao()
    }

    @Provides
    fun provideImportRepository(importDao: ImportDao): ImportRepositorySource {
        return ImportRepository(importDao)
    }

    @Provides
    fun provideTransactionBasketDao(appDatabase: AppDatabase): TransactionEntityDao {
        return appDatabase.getTransactionEntityDao()
    }

    @Provides
    fun provideTransactionBasketRepository(dao: TransactionEntityDao): TransactionBasketRepositorySource {
        return TransactionBasketRepository(dao)
    }

    @Provides
    fun provideItemDao(appDatabase: AppDatabase): ItemEntityDao {
        return appDatabase.getItemEntityDao()
    }

    @Provides
    fun provideItemRepository(dao: ItemEntityDao): ItemRepositorySource {
        return ItemRepository(dao)
    }

    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductEntityDao {
        return appDatabase.getProductEntityDao()
    }

    @Provides
    fun provideProductRepository(dao: ProductEntityDao): ProductRepositorySource {
        return ProductRepository(dao)
    }

    @Provides
    fun provideVariantDao(appDatabase: AppDatabase): ProductVariantEntityDao {
        return appDatabase.getProductVariantEntityDao()
    }

    @Provides
    fun provideVariantRepository(dao: ProductVariantEntityDao): VariantRepositorySource {
        return VariantRepository(dao)
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): ProductCategoryEntityDao {
        return appDatabase.getProductCategoryEntityDao()
    }


    @Provides
    fun provideCategoryRepository(dao: ProductCategoryEntityDao): CategoryRepositorySource {
        return CategoryRepository(dao)
    }

    @Provides
    fun provideShopDao(appDatabase: AppDatabase): ShopEntityDao {
        return appDatabase.getShopEntityDao()
    }

    @Provides
    fun provideShopRepository(dao: ShopEntityDao): ShopRepositorySource {
        return ShopRepository(dao)
    }

    @Provides
    fun provideProducerDao(appDatabase: AppDatabase): ProductProducerEntityDao {
        return appDatabase.getProductProducerEntityDao()
    }

    @Provides
    fun provideProducerRepository(dao: ProductProducerEntityDao): ProducerRepositorySource {
        return ProducerRepository(dao)
    }
}
