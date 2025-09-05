package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ProductEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepository
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.usecase.data.DeleteProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAveragePriceByShopByVariantByProducerByDayForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetItemsForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.PerformAutomaticBackupIfEnabledUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductEntityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductModule {

    @Provides
    @Singleton
    fun provideProductEntityDao(appDatabase: AppDatabase): ProductEntityDao {
        return appDatabase.getProductEntityDao()
    }

    @Provides
    @Singleton
    fun provideProductRepository(dao: ProductEntityDao): ProductRepositorySource {
        return ProductRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideInsertProductEntityUseCase(
        productRepository: ProductRepositorySource,
        productProducerRepository: ProductProducerRepositorySource,
        productCategoryRepository: ProductCategoryRepositorySource,
    ): InsertProductEntityUseCase {
        return InsertProductEntityUseCase(
            productRepository,
            productProducerRepository,
            productCategoryRepository,
        )
    }

    @Provides
    @Singleton
    fun provideUpdateProductEntityUseCase(
        productRepository: ProductRepositorySource,
        productProducerRepository: ProductProducerRepositorySource,
        productCategoryRepository: ProductCategoryRepositorySource,
    ): UpdateProductEntityUseCase {
        return UpdateProductEntityUseCase(
            productRepository,
            productProducerRepository,
            productCategoryRepository,
        )
    }

    @Provides
    @Singleton
    fun provideMergeProductEntityUseCase(
        productRepository: ProductRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
        itemRepository: ItemRepositorySource,
        performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
    ): MergeProductEntityUseCase {
        return MergeProductEntityUseCase(
            productRepository,
            productVariantRepository,
            itemRepository,
            performAutomaticBackupIfEnabledUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideDeleteProductEntityUseCase(
        productRepository: ProductRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
        itemRepository: ItemRepositorySource,
        performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
    ): DeleteProductEntityUseCase {
        return DeleteProductEntityUseCase(
            productRepository,
            productVariantRepository,
            itemRepository,
            performAutomaticBackupIfEnabledUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideGetProductEntityUseCase(
        productRepository: ProductRepositorySource
    ): GetProductEntityUseCase {
        return GetProductEntityUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllProductEntityUseCase(
        productRepository: ProductRepositorySource
    ): GetAllProductEntityUseCase {
        return GetAllProductEntityUseCase(productRepository)
    }

    /** DOMAIN */
    @Provides
    @Singleton
    fun provideGetTotalSpentForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetTotalSpentForProductUseCase {
        return GetTotalSpentForProductUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetItemsForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetItemsForProductUseCase {
        return GetItemsForProductUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetTotalSpentByDayForProductUseCase {
        return GetTotalSpentByDayForProductUseCase(productRepository)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetTotalSpentByWeekForProductUseCase {
        return GetTotalSpentByWeekForProductUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetTotalSpentByMonthForProductUseCase {
        return GetTotalSpentByMonthForProductUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetTotalSpentByYearForProductUseCase {
        return GetTotalSpentByYearForProductUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetAveragePriceByShopByVariantByProducerByDayForProductUseCase(
        productRepository: ProductRepositorySource
    ): GetAveragePriceByShopByVariantByProducerByDayForProductUseCase {
        return GetAveragePriceByShopByVariantByProducerByDayForProductUseCase(productRepository)
    }
}
