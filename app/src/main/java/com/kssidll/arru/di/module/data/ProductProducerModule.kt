package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ProductProducerEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ProductProducerRepository
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductProducerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductProducerModule {

    @Provides
    @Singleton
    fun provideProductProducerEntityDao(appDatabase: AppDatabase): ProductProducerEntityDao {
        return appDatabase.getProductProducerEntityDao()
    }

    @Provides
    @Singleton
    fun provideProductProducerRepository(
        dao: ProductProducerEntityDao
    ): ProductProducerRepositorySource {
        return ProductProducerRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetProductProducerEntityUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetProductProducerEntityUseCase {
        return GetProductProducerEntityUseCase(productProducerRepository)
    }

    /** DOMAIN */
    @Provides
    @Singleton
    fun provideGetTotalSpentForProductProducerUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetTotalSpentForProductProducerUseCase {
        return GetTotalSpentForProductProducerUseCase(productProducerRepository)
    }

    @Provides
    @Singleton
    fun provideGetItemsForProductProducerUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetItemsForProductProducerUseCase {
        return GetItemsForProductProducerUseCase(productProducerRepository)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForProductProducerUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetTotalSpentByDayForProductProducerUseCase {
        return GetTotalSpentByDayForProductProducerUseCase(productProducerRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForProductProducerUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetTotalSpentByWeekForProductProducerUseCase {
        return GetTotalSpentByWeekForProductProducerUseCase(productProducerRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForProductProducerUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetTotalSpentByMonthForProductProducerUseCase {
        return GetTotalSpentByMonthForProductProducerUseCase(productProducerRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForProductProducerUseCase(
        productProducerRepository: ProductProducerRepositorySource
    ): GetTotalSpentByYearForProductProducerUseCase {
        return GetTotalSpentByYearForProductProducerUseCase(productProducerRepository)
    }
}
