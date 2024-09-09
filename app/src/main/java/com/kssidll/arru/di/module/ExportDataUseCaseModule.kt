package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.data.repository.VariantRepositorySource
import com.kssidll.arru.domain.usecase.ExportDataUIBlockingUseCase
import com.kssidll.arru.domain.usecase.ExportDataWithServiceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ExportDataUseCaseModule {
    @Provides
    @Singleton
    fun provideExportDataWithServiceUseCase(@ApplicationContext context: Context): ExportDataWithServiceUseCase {
        return ExportDataWithServiceUseCase(context)
    }

    @Provides
    @Singleton
    fun provideExportDataUIBlockingUseCase(
        @ApplicationContext context: Context,
        categoryRepository: CategoryRepositorySource,
        itemRepository: ItemRepositorySource,
        producerRepository: ProducerRepositorySource,
        productRepository: ProductRepositorySource,
        shopRepository: ShopRepositorySource,
        transactionRepository: TransactionBasketRepositorySource,
        variantRepository: VariantRepositorySource,
    ): ExportDataUIBlockingUseCase {
        return ExportDataUIBlockingUseCase(
            context,
            categoryRepository,
            itemRepository,
            producerRepository,
            productRepository,
            shopRepository,
            transactionRepository,
            variantRepository
        )
    }
}