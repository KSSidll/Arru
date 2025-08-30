package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepository
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.usecase.data.DeleteProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductVariantEntityUseCase
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
    fun provideInsertProductVariantEntityUseCase(
        itemRepository: ItemRepositorySource,
        productRepository: ProductRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
    ): InsertProductVariantEntityUseCase {
        return InsertProductVariantEntityUseCase(
            itemRepository,
            productRepository,
            productVariantRepository,
        )
    }

    @Provides
    @Singleton
    fun provideUpdateProductVariantEntityUseCase(
        itemRepository: ItemRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
    ): UpdateProductVariantEntityUseCase {
        return UpdateProductVariantEntityUseCase(itemRepository, productVariantRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteProductVariantEntityUseCase(
        itemRepository: ItemRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
    ): DeleteProductVariantEntityUseCase {
        return DeleteProductVariantEntityUseCase(itemRepository, productVariantRepository)
    }

    @Provides
    @Singleton
    fun provideGetProductVariantEntityUseCase(
        productVariantRepository: ProductVariantRepositorySource
    ): GetProductVariantEntityUseCase {
        return GetProductVariantEntityUseCase(productVariantRepository)
    }

    @Provides
    @Singleton
    fun provideGetProductVariantEntityByProductUseCase(
        productVariantRepository: ProductVariantRepositorySource
    ): GetProductVariantEntityByProductUseCase {
        return GetProductVariantEntityByProductUseCase(productVariantRepository)
    }

    /** DOMAIN */
}
