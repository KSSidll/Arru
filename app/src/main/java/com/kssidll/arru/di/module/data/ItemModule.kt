package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepository
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.domain.usecase.data.DeleteItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateItemEntityUseCase
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
    fun provideInsertItemEntityUseCase(
        transactionRepository: ItemRepositorySource,
        productRepository: ItemRepositorySource,
        productVariantRepository: ItemRepositorySource,
        itemRepository: ItemRepositorySource,
    ): InsertItemEntityUseCase {
        return InsertItemEntityUseCase(
            transactionRepository,
            productRepository,
            productVariantRepository,
            itemRepository,
        )
    }

    @Provides
    @Singleton
    fun provideUpdateItemEntityUseCase(
        transactionRepository: ItemRepositorySource,
        productRepository: ItemRepositorySource,
        productVariantRepository: ItemRepositorySource,
        itemRepository: ItemRepositorySource,
    ): UpdateItemEntityUseCase {
        return UpdateItemEntityUseCase(
            transactionRepository,
            productRepository,
            productVariantRepository,
            itemRepository,
        )
    }

    @Provides
    @Singleton
    fun provideDeleteItemEntityUseCase(
        itemRepository: ItemRepositorySource
    ): DeleteItemEntityUseCase {
        return DeleteItemEntityUseCase(itemRepository)
    }

    @Provides
    @Singleton
    fun provideGetItemEntityUseCase(
        itemRepositorySource: ItemRepositorySource
    ): GetItemEntityUseCase {
        return GetItemEntityUseCase(itemRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetNewestItemEntityUseCase(
        itemRepositorySource: ItemRepositorySource
    ): GetNewestItemEntityUseCase {
        return GetNewestItemEntityUseCase(itemRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetNewestItemEntityByProductUseCase(
        itemRepositorySource: ItemRepositorySource
    ): GetNewestItemEntityByProductUseCase {
        return GetNewestItemEntityByProductUseCase(itemRepositorySource)
    }

    /** DOMAIN */
}
