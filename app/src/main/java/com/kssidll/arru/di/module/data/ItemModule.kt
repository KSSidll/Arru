package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepository
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemEntityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ItemModule {

    @Provides
    @Singleton
    fun provideItemEntityDao(appDatabase: AppDatabase): ItemEntityDao {
        return appDatabase.getItemEntityDao()
    }

    @Provides
    @Singleton
    fun provideItemRepository(dao: ItemEntityDao): ItemRepositorySource {
        return ItemRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetItemEntityUseCase(
        itemRepositorySource: ItemRepositorySource
    ): GetItemEntityUseCase {
        return GetItemEntityUseCase(itemRepositorySource)
    }

    /** DOMAIN */
}
