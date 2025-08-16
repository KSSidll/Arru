package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductVariantModule {

    /** ENTITY */

    @Provides
    @Singleton
    fun provideGetProductVariantEntityUseCase(
        productVariantRepositorySource: ProductVariantRepositorySource
    ): GetProductVariantEntityUseCase {
        return GetProductVariantEntityUseCase(productVariantRepositorySource)
    }


    /** DOMAIN */

    @Provides
    @Singleton
    fun provideGetProductVariantUseCase(
        getProductVariantEntityUseCase: GetProductVariantEntityUseCase
    ): GetProductVariantUseCase {
        return GetProductVariantUseCase(getProductVariantEntityUseCase)
    }
}
