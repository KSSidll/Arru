package com.kssidll.arrugarq.di.module

import android.content.*
import androidx.datastore.preferences.core.*
import com.kssidll.arrugarq.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.database.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.preference.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.qualifiers.*
import dagger.hilt.components.*
import java.io.*
import javax.inject.*

/**
 * default database name
 */
const val DATABASE_NAME: String = "arrugarq_database"

/**
 * @return absolute path to external database file
 */
fun Context.externalDbPath(): String =
    getExternalFilesDir(null)!!.absolutePath.plus("/database/$DATABASE_NAME.db")

/**
 * @return absolute path to internal database file as [File]
 */
fun Context.internalDbFile(): File = getDatabasePath(DATABASE_NAME)

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

        if (location != AppPreferences.Database.Location.INTERNAL) {
            if (appContext.internalDbFile()
                    .exists()
                    .not()
            ) {
                // simple query to ensure database creation
                AppDatabase.buildInternal(appContext)
                    .query(
                        "SELECT 1",
                        null
                    )
                    .close()
                Arrugarq.restart(appContext)
            }
        }

        return when (preferences[AppPreferences.Database.Location.key]) {
            AppPreferences.Database.Location.EXTERNAL -> {
                if (File(appContext.externalDbPath()).exists()
                        .not()
                ) {
                    AppDatabase.moveInternalToExternal(appContext)
                }

                AppDatabase.buildExternal(appContext)
            }

            AppPreferences.Database.Location.INTERNAL -> {
                if (appContext.internalDbFile()
                        .exists()
                        .not()
                ) {
                    AppDatabase.moveExternalToInternal(appContext)
                }

                AppDatabase.buildInternal(appContext)
            }

            else -> error("The database location preference key isn't set to a valid value")
        }
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
