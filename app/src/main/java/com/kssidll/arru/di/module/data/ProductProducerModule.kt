package com.kssidll.arru.di.module.data

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

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetProductProducerEntityUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetProductProducerEntityUseCase {
        return GetProductProducerEntityUseCase(productProducerRepositorySource)
    }

    /** DOMAIN */

    // @Provides
    // @Singleton
    // fun provideGetProductProducerUseCase(
    //     getProductProducerEntityUseCase: GetProductProducerEntityUseCase
    // ): GetProductProducerUseCase {
    //     return GetProductProducerUseCase(getProductProducerEntityUseCase)
    // }

    @Provides
    @Singleton
    fun provideGetTotalSpentForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetTotalSpentForProductProducerUseCase {
        return GetTotalSpentForProductProducerUseCase(productProducerRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetItemsForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetItemsForProductProducerUseCase {
        return GetItemsForProductProducerUseCase(productProducerRepositorySource)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetTotalSpentByDayForProductProducerUseCase {
        return GetTotalSpentByDayForProductProducerUseCase(productProducerRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetTotalSpentByWeekForProductProducerUseCase {
        return GetTotalSpentByWeekForProductProducerUseCase(productProducerRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetTotalSpentByMonthForProductProducerUseCase {
        return GetTotalSpentByMonthForProductProducerUseCase(productProducerRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetTotalSpentByYearForProductProducerUseCase {
        return GetTotalSpentByYearForProductProducerUseCase(productProducerRepositorySource)
    }
}
