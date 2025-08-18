package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.domain.usecase.data.GetAveragePriceByShopByVariantByProducerByDayForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetItemsForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductModule {

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetProductEntityUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetProductEntityUseCase {
        return GetProductEntityUseCase(productRepositorySource)
    }

    /** DOMAIN */

    // @Provides
    // @Singleton
    // fun provideGetProductUseCase(
    //     getProductEntityUseCase: GetProductEntityUseCase
    // ): GetProductUseCase {
    //     return GetProductUseCase(getProductEntityUseCase)
    // }

    @Provides
    @Singleton
    fun provideGetTotalSpentForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetTotalSpentForProductUseCase {
        return GetTotalSpentForProductUseCase(productRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetItemsForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetItemsForProductUseCase {
        return GetItemsForProductUseCase(productRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetTotalSpentByDayForProductUseCase {
        return GetTotalSpentByDayForProductUseCase(productRepositorySource)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetTotalSpentByWeekForProductUseCase {
        return GetTotalSpentByWeekForProductUseCase(productRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetTotalSpentByMonthForProductUseCase {
        return GetTotalSpentByMonthForProductUseCase(productRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetTotalSpentByYearForProductUseCase {
        return GetTotalSpentByYearForProductUseCase(productRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetAveragePriceByShopByVariantByProducerByDayForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetAveragePriceByShopByVariantByProducerByDayForProductUseCase {
        return GetAveragePriceByShopByVariantByProducerByDayForProductUseCase(
            productRepositorySource
        )
    }
}
