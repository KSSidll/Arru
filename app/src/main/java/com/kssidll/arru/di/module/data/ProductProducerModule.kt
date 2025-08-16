package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductProducerUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
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
    fun provideGetItemsForProductProducerUseCase(
        productProducerRepositorySource: ProductProducerRepositorySource
    ): GetItemsForProductProducerUseCase {
        return GetItemsForProductProducerUseCase(productProducerRepositorySource)
    }
}
