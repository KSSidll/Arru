package com.kssidll.arrugarq.di.module

import android.content.Context
import androidx.room.Room
import com.kssidll.arrugarq.data.dao.ItemDao
import com.kssidll.arrugarq.data.database.AppDatabase
import com.kssidll.arrugarq.data.repository.IItemRepository
import com.kssidll.arrugarq.data.repository.ItemRepository
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
        return ItemRepository(itemDao = itemDao)
    }
}