package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductCategoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductCategoryModule {

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetProductCategoryEntityUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetProductCategoryEntityUseCase {
        return GetProductCategoryEntityUseCase(productCategoryRepositorySource)
    }

    /** DOMAIN */

    // @Provides
    // @Singleton
    // fun provideGetProductCategoryUseCase(
    //     getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase
    // ): GetProductCategoryUseCase {
    //     return GetProductCategoryUseCase(getProductCategoryEntityUseCase)
    // }

    @Provides
    @Singleton
    fun provideGetTotalSpentForProductCategoryUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetTotalSpentForProductCategoryUseCase {
        return GetTotalSpentForProductCategoryUseCase(productCategoryRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetItemsForProductCategoryUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetItemsForProductCategoryUseCase {
        return GetItemsForProductCategoryUseCase(productCategoryRepositorySource)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForProductCategoryUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetTotalSpentByDayForProductCategoryUseCase {
        return GetTotalSpentByDayForProductCategoryUseCase(productCategoryRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForProductCategoryUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetTotalSpentByWeekForProductCategoryUseCase {
        return GetTotalSpentByWeekForProductCategoryUseCase(productCategoryRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForProductCategoryUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetTotalSpentByMonthForProductCategoryUseCase {
        return GetTotalSpentByMonthForProductCategoryUseCase(productCategoryRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForProductCategoryUseCase(
        productCategoryRepositorySource: ProductCategoryRepositorySource
    ): GetTotalSpentByYearForProductCategoryUseCase {
        return GetTotalSpentByYearForProductCategoryUseCase(productCategoryRepositorySource)
    }
}
