package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ProductVariantRepository
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductVariantModule {

    @Provides
    @Singleton
    fun provideProductVariantEntityDao(appDatabase: AppDatabase): ProductVariantEntityDao {
        return appDatabase.getProductVariantEntityDao()
    }

    @Provides
    @Singleton
    fun provideProductVariantRepository(
        dao: ProductVariantEntityDao
    ): ProductVariantRepositorySource {
        return ProductVariantRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetProductVariantEntityUseCase(
        productVariantRepositorySource: ProductVariantRepositorySource
    ): GetProductVariantEntityUseCase {
        return GetProductVariantEntityUseCase(productVariantRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetProductVariantEntityByProductUseCase(
        productVariantRepositorySource: ProductVariantRepositorySource
    ): GetProductVariantEntityByProductUseCase {
        return GetProductVariantEntityByProductUseCase(productVariantRepositorySource)
    }

    /** DOMAIN */
}
