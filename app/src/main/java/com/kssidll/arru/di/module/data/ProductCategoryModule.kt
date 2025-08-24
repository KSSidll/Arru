package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductCategoryRepository
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.usecase.data.DeleteProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetItemsForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByProductCategoryByMonthUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForProductCategoryUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductCategoryEntityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductCategoryModule {

    @Provides
    @Singleton
    fun provideProductCategoryEntityDao(appDatabase: AppDatabase): ProductCategoryEntityDao {
        return appDatabase.getProductCategoryEntityDao()
    }

    @Provides
    @Singleton
    fun provideProductCategoryRepository(
        dao: ProductCategoryEntityDao
    ): ProductCategoryRepositorySource {
        return ProductCategoryRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideInsertProductCategoryEntityUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): InsertProductCategoryEntityUseCase {
        return InsertProductCategoryEntityUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateProductCategoryEntityUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): UpdateProductCategoryEntityUseCase {
        return UpdateProductCategoryEntityUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideMergeProductCategoryEntityUseCase(
        productRepository: ProductRepositorySource,
        productCategoryRepository: ProductCategoryRepositorySource,
    ): MergeProductCategoryEntityUseCase {
        return MergeProductCategoryEntityUseCase(productRepository, productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteProductCategoryEntityUseCase(
        productRepository: ProductRepositorySource,
        productVariantRepository: ProductVariantRepositorySource,
        itemRepository: ItemRepositorySource,
        productCategoryRepository: ProductCategoryRepositorySource,
    ): DeleteProductCategoryEntityUseCase {
        return DeleteProductCategoryEntityUseCase(
            productRepository,
            productVariantRepository,
            itemRepository,
            productCategoryRepository,
        )
    }

    @Provides
    @Singleton
    fun provideGetProductCategoryEntityUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetProductCategoryEntityUseCase {
        return GetProductCategoryEntityUseCase(productCategoryRepository)
    }

    /** DOMAIN */
    @Provides
    @Singleton
    fun provideGetTotalSpentForProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentForProductCategoryUseCase {
        return GetTotalSpentForProductCategoryUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetItemsForProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetItemsForProductCategoryUseCase {
        return GetItemsForProductCategoryUseCase(productCategoryRepository)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentByDayForProductCategoryUseCase {
        return GetTotalSpentByDayForProductCategoryUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentByWeekForProductCategoryUseCase {
        return GetTotalSpentByWeekForProductCategoryUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentByMonthForProductCategoryUseCase {
        return GetTotalSpentByMonthForProductCategoryUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentByYearForProductCategoryUseCase {
        return GetTotalSpentByYearForProductCategoryUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByProductCategoryUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentByProductCategoryUseCase {
        return GetTotalSpentByProductCategoryUseCase(productCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByProductCategoryByMonthUseCase(
        productCategoryRepository: ProductCategoryRepositorySource
    ): GetTotalSpentByProductCategoryByMonthUseCase {
        return GetTotalSpentByProductCategoryByMonthUseCase(productCategoryRepository)
    }
}
