package com.kssidll.arrugarq.di.module

import android.content.*
import androidx.datastore.preferences.core.*
import androidx.room.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.database.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.preference.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.qualifiers.*
import dagger.hilt.components.*
import javax.inject.*

const val DATABASE_NAME: String = "arrugarq_database"

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        preferences: Preferences
    ): AppDatabase {
        val builder = Room.databaseBuilder(
            appContext.applicationContext,
            AppDatabase::class.java,
            when (preferences[AppPreferences.Database.key]) {
                AppPreferences.Database.Location.EXTERNAL -> {
                    appContext.getExternalFilesDir(null)!!.absolutePath.plus("/$DATABASE_NAME.db")
                }

                AppPreferences.Database.Location.INTERNAL -> {
                    DATABASE_NAME
                }

                else -> error("The database location preference key isn't set to a valid value")
            }
        )


        return builder
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
    fun provideVariantDao(appDatabase: AppDatabase): VariantDao {
        return appDatabase.getVariantDao()
    }

    @Provides
    fun provideVariantRepository(productVariantDao: VariantDao): IVariantRepository {
        return VariantRepository(productVariantDao)
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.getCategoryDao()
    }


    @Provides
    fun provideCategoryRepository(productCategoryDao: CategoryDao): ICategoryRepository {
        return CategoryRepository(productCategoryDao)
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
    fun provideProducerDao(appDatabase: AppDatabase): ProducerDao {
        return appDatabase.getProducerDao()
    }

    @Provides
    fun provideProducerRepository(productProducerDao: ProducerDao): IProducerRepository {
        return ProducerRepository(productProducerDao)
    }
}
