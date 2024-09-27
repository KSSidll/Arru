package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.Arru
import com.kssidll.arru.data.dao.CategoryDao
import com.kssidll.arru.data.dao.ItemDao
import com.kssidll.arru.data.dao.ProducerDao
import com.kssidll.arru.data.dao.ProductDao
import com.kssidll.arru.data.dao.ShopDao
import com.kssidll.arru.data.dao.TransactionBasketDao
import com.kssidll.arru.data.dao.VariantDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.database.externalDbFile
import com.kssidll.arru.data.database.internalDbFile
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getDatabaseLocation
import com.kssidll.arru.data.repository.CategoryRepository
import com.kssidll.arru.data.repository.CategoryRepositorySource
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

        // if it's not internal and doesn't exist, create it so it can be moved
        if (databaseLocation != AppPreferences.Database.Location.Values.INTERNAL) {
            if (!context.internalDbFile()
                    .exists()
            ) {
                // simple query to ensure database creation
                AppDatabase.buildInternal(context)
                    .query(
                        "SELECT 1",
                        null
                    )
                    .close()
                Arru.restart(context)
            }
        }

        return when (databaseLocation) {
            AppPreferences.Database.Location.Values.EXTERNAL -> {
                if (!context.externalDbFile()
                        .exists()
                ) {
                    AppDatabase.moveInternalToExternal(context)
                }

                AppDatabase.buildExternal(context)
            }

            AppPreferences.Database.Location.Values.INTERNAL -> {
                if (!context.internalDbFile()
                        .exists()
                ) {
                    if (context.externalDbFile()
                            .exists()
                    ) {
                        AppDatabase.moveExternalToInternal(context)
                    }
                }

                AppDatabase.buildInternal(context)
            }
        }
    }

    @Provides
    fun provideTransactionBasketDao(appDatabase: AppDatabase): TransactionBasketDao {
        return appDatabase.getTransactionBasketDao()
    }

    @Provides
    fun provideTransactionBasketRepository(transactionBasketDao: TransactionBasketDao): TransactionBasketRepositorySource {
        return TransactionBasketRepository(transactionBasketDao)
    }

    @Provides
    fun provideItemDao(appDatabase: AppDatabase): ItemDao {
        return appDatabase.getItemDao()
    }

    @Provides
    fun provideItemRepository(itemDao: ItemDao): ItemRepositorySource {
        return ItemRepository(itemDao)
    }

    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.getProductDao()
    }

    @Provides
    fun provideProductRepository(productDao: ProductDao): ProductRepositorySource {
        return ProductRepository(productDao)
    }

    @Provides
    fun provideVariantDao(appDatabase: AppDatabase): VariantDao {
        return appDatabase.getVariantDao()
    }

    @Provides
    fun provideVariantRepository(productVariantDao: VariantDao): VariantRepositorySource {
        return VariantRepository(productVariantDao)
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.getCategoryDao()
    }


    @Provides
    fun provideCategoryRepository(productCategoryDao: CategoryDao): CategoryRepositorySource {
        return CategoryRepository(productCategoryDao)
    }

    @Provides
    fun provideShopDao(appDatabase: AppDatabase): ShopDao {
        return appDatabase.getShopDao()
    }

    @Provides
    fun provideShopRepository(shopDao: ShopDao): ShopRepositorySource {
        return ShopRepository(shopDao)
    }

    @Provides
    fun provideProducerDao(appDatabase: AppDatabase): ProducerDao {
        return appDatabase.getProducerDao()
    }

    @Provides
    fun provideProducerRepository(productProducerDao: ProducerDao): ProducerRepositorySource {
        return ProducerRepository(productProducerDao)
    }
}
