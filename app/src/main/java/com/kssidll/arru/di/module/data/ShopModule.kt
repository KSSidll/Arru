package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ShopRepository
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopByMonthUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForShopUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ShopModule {

    @Provides
    @Singleton
    fun provideShopEntityDao(appDatabase: AppDatabase): ShopEntityDao {
        return appDatabase.getShopEntityDao()
    }

    @Provides
    @Singleton
    fun provideShopRepository(dao: ShopEntityDao): ShopRepositorySource {
        return ShopRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetShopEntityUseCase(shopRepository: ShopRepositorySource): GetShopEntityUseCase {
        return GetShopEntityUseCase(shopRepository)
    }

    /** DOMAIN */
    @Provides
    @Singleton
    fun provideGetTotalSpentForShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentForShopUseCase {
        return GetTotalSpentForShopUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetItemsForShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetItemsForShopUseCase {
        return GetItemsForShopUseCase(shopRepository)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentByDayForShopUseCase {
        return GetTotalSpentByDayForShopUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentByWeekForShopUseCase {
        return GetTotalSpentByWeekForShopUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentByMonthForShopUseCase {
        return GetTotalSpentByMonthForShopUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentByYearForShopUseCase {
        return GetTotalSpentByYearForShopUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByShopUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentByShopUseCase {
        return GetTotalSpentByShopUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByShopByMonthUseCase(
        shopRepository: ShopRepositorySource
    ): GetTotalSpentByShopByMonthUseCase {
        return GetTotalSpentByShopByMonthUseCase(shopRepository)
    }
}
