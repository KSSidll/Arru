package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ShopRepository
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.usecase.data.DeleteShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetItemsForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopByMonthUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForShopUseCase
import com.kssidll.arru.domain.usecase.data.InsertShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.PerformAutomaticBackupIfEnabledUseCase
import com.kssidll.arru.domain.usecase.data.UpdateShopEntityUseCase
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
    fun provideInsertShopEntityUseCase(
        shopRepository: ShopRepositorySource
    ): InsertShopEntityUseCase {
        return InsertShopEntityUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateShopEntityUseCase(
        shopRepository: ShopRepositorySource
    ): UpdateShopEntityUseCase {
        return UpdateShopEntityUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideMergeShopEntityUseCase(
        transactionRepository: TransactionRepositorySource,
        shopRepository: ShopRepositorySource,
        performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
    ): MergeShopEntityUseCase {
        return MergeShopEntityUseCase(
            transactionRepository,
            shopRepository,
            performAutomaticBackupIfEnabledUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideDeleteShopEntityUseCase(
        transactionRepository: TransactionRepositorySource,
        itemRepository: ItemRepositorySource,
        shopRepository: ShopRepositorySource,
        performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
    ): DeleteShopEntityUseCase {
        return DeleteShopEntityUseCase(
            transactionRepository,
            itemRepository,
            shopRepository,
            performAutomaticBackupIfEnabledUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideGetShopEntityUseCase(shopRepository: ShopRepositorySource): GetShopEntityUseCase {
        return GetShopEntityUseCase(shopRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllShopEntityUseCase(
        shopRepository: ShopRepositorySource
    ): GetAllShopEntityUseCase {
        return GetAllShopEntityUseCase(shopRepository)
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
