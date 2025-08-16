package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
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
}
