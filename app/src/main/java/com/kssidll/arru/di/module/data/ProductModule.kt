package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
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
    fun provideGetItemsForProductUseCase(
        productRepositorySource: ProductRepositorySource
    ): GetItemsForProductUseCase {
        return GetItemsForProductUseCase(productRepositorySource)
    }
}
