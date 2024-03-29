package com.kssidll.arru.di.module

import android.content.*
import androidx.datastore.preferences.core.*
import com.kssidll.arru.*
import com.kssidll.arru.data.dao.*
import com.kssidll.arru.data.database.*
import com.kssidll.arru.data.preference.*
import com.kssidll.arru.data.repository.*
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
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        preferences: Preferences
    ): AppDatabase {
        val location = preferences[AppPreferences.Database.Location.key]

        // if it's not internal and doesn't exist, create it so it can be moved
        if (location != AppPreferences.Database.Location.INTERNAL) {
            if (!appContext.internalDbFile()
                    .exists()
            ) {
                // simple query to ensure database creation
                AppDatabase.buildInternal(appContext)
                    .query(
                        "SELECT 1",
                        null
                    )
                    .close()
                Arru.restart(appContext)
            }
        }

        return when (preferences[AppPreferences.Database.Location.key]) {
            AppPreferences.Database.Location.EXTERNAL -> {
                if (!appContext.externalDbFile()
                        .exists()
                ) {
                    AppDatabase.moveInternalToExternal(appContext)
                }

                AppDatabase.buildExternal(appContext)
            }

            AppPreferences.Database.Location.INTERNAL -> {
                if (!appContext.internalDbFile()
                        .exists()
                ) {
                    if (appContext.externalDbFile()
                            .exists()
                    ) {
                        AppDatabase.moveExternalToInternal(appContext)
                    }
                }

                AppDatabase.buildInternal(appContext)
            }

            else -> error("The database location preference key isn't set to a valid value")
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
