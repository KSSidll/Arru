package com.kssidll.arrugarq.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kssidll.arrugarq.data.dao.ItemDao
import com.kssidll.arrugarq.data.dao.ProductCategoryDao
import com.kssidll.arrugarq.data.dao.ProductCategoryTypeDao
import com.kssidll.arrugarq.data.dao.ProductDao
import com.kssidll.arrugarq.data.dao.ProductProducerDao
import com.kssidll.arrugarq.data.dao.ShopDao
import com.kssidll.arrugarq.data.database.AppDatabase
import com.kssidll.arrugarq.data.repository.IItemRepository
import com.kssidll.arrugarq.data.repository.IProductCategoryRepository
import com.kssidll.arrugarq.data.repository.IProductCategoryTypeRepository
import com.kssidll.arrugarq.data.repository.IProductProducerRepository
import com.kssidll.arrugarq.data.repository.IProductRepository
import com.kssidll.arrugarq.data.repository.IShopRepository
import com.kssidll.arrugarq.data.repository.ItemRepository
import com.kssidll.arrugarq.data.repository.ProductCategoryRepository
import com.kssidll.arrugarq.data.repository.ProductCategoryTypeRepository
import com.kssidll.arrugarq.data.repository.ProductProducerRepository
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
            "arrugarq_database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                // prepopulate the database
                // we do that with a function but it is preffered and recommended
                // to prepopulate via prepackaged database file
                // TODO change this to prepackaged database file
                for (data in prepopulateProductCategoryTypeData()) {
                    db.execSQL("INSERT INTO productcategorytype (id, name) VALUES('${data.id}', '${data.name}');")
                }

                for (data in prepopulateProductCategoryData()) {
                    db.execSQL("INSERT INTO productcategory (typeId, name) VALUES('${data.typeId}', '${data.name}');")
                }
            }
        }).build()
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

    @Provides
    fun provideProductProducerDao(appDatabase: AppDatabase): ProductProducerDao {
        return appDatabase.getProductProducerDao()
    }

    @Provides
    fun provideProductProducerRepository(productProducerDao: ProductProducerDao): IProductProducerRepository {
        return ProductProducerRepository(productProducerDao)
    }
}
