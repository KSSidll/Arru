package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerUseCase
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

    @Provides
    @Singleton
    fun provideGetProductProducerUseCase(
        getProductProducerEntityUseCase: GetProductProducerEntityUseCase
    ): GetProductProducerUseCase {
        return GetProductProducerUseCase(getProductProducerEntityUseCase)
    }
}
