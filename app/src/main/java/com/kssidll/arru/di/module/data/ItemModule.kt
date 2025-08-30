package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepository
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
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
        transactionRepository: TransactionRepositorySource,
        productRepository: ProductRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
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
        transactionRepository: TransactionRepositorySource,
        productRepository: ProductRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
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
    fun provideGetItemEntityUseCase(itemRepository: ItemRepositorySource): GetItemEntityUseCase {
        return GetItemEntityUseCase(itemRepository)
    }

    @Provides
    @Singleton
    fun provideGetNewestItemEntityUseCase(
        itemRepository: ItemRepositorySource
    ): GetNewestItemEntityUseCase {
        return GetNewestItemEntityUseCase(itemRepository)
    }

    @Provides
    @Singleton
    fun provideGetNewestItemEntityByProductUseCase(
        itemRepository: ItemRepositorySource
    ): GetNewestItemEntityByProductUseCase {
        return GetNewestItemEntityByProductUseCase(itemRepository)
    }

    /** DOMAIN */
}
