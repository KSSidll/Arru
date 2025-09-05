package com.kssidll.arru.usecase

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.dao.ProductEntityDao
import com.kssidll.arru.data.dao.ProductProducerEntityDao
import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.ShopRepository
import com.kssidll.arru.data.repository.TransactionRepository
import com.kssidll.arru.data.shopEntityMockData
import com.kssidll.arru.domain.usecase.data.InsertTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertTransactionEntityUseCaseResult
import java.io.IOException
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class TransactionUseCaseUnitTest {
    private lateinit var db: AppDatabase
    private lateinit var shopDao: ShopEntityDao
    private lateinit var transactionDao: TransactionEntityDao
    private lateinit var productCategoryDao: ProductCategoryEntityDao
    private lateinit var productProducerDao: ProductProducerEntityDao
    private lateinit var productVariantDao: ProductVariantEntityDao
    private lateinit var productDao: ProductEntityDao
    private lateinit var itemDao: ItemEntityDao

    private lateinit var insertTransactionEntityUseCase: InsertTransactionEntityUseCase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        shopDao = db.getShopEntityDao()
        transactionDao = db.getTransactionEntityDao()
        productCategoryDao = db.getProductCategoryEntityDao()
        productProducerDao = db.getProductProducerEntityDao()
        productDao = db.getProductEntityDao()
        productVariantDao = db.getProductVariantEntityDao()
        itemDao = db.getItemEntityDao()

        insertTransactionEntityUseCase =
            InsertTransactionEntityUseCase(
                ShopRepository(shopDao),
                TransactionRepository(transactionDao),
            )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertTransactionEntityUseCaseNullDataReturnsErrors() = runTest {
        val result = insertTransactionEntityUseCase(null, null, null, null)

        val expectedErrors =
            listOf(
                    InsertTransactionEntityUseCaseResult.DateNoValue,
                    InsertTransactionEntityUseCaseResult.TotalCostNoValue,
                )
                .toImmutableList()

        assertThat(
            "Did not get error result",
            result is InsertTransactionEntityUseCaseResult.Error,
            equalTo(true),
        )
        if (result is InsertTransactionEntityUseCaseResult.Error) {
            assertThat("Didn't get expected errors", result.errors, equalTo(expectedErrors))
        }
    }

    @Test
    fun insertTransactionEntityUseCaseBadDataReturnsErrors() = runTest {
        val result = insertTransactionEntityUseCase(1, "13.e7", null, 7)

        val expectedErrors =
            listOf(
                    InsertTransactionEntityUseCaseResult.TotalCostInvalid,
                    InsertTransactionEntityUseCaseResult.ShopIdInvalid,
                )
                .toImmutableList()

        assertThat(
            "Did not get error result",
            result is InsertTransactionEntityUseCaseResult.Error,
            equalTo(true),
        )
        if (result is InsertTransactionEntityUseCaseResult.Error) {
            assertThat("Didn't get expected errors", result.errors, equalTo(expectedErrors))
        }
    }

    @Test
    fun insertTransactionEntityUseCaseGoodDataReturnsSuccess() = runTest {
        val shopEntity = shopEntityMockData.first()
        shopDao.insert(shopEntity)

        val result = insertTransactionEntityUseCase(100, "13.7", "test", shopEntity.id)

        val expectedId = 1L

        assertThat(
            "Did not get success result",
            result is InsertTransactionEntityUseCaseResult.Success,
            equalTo(true),
        )
        if (result is InsertTransactionEntityUseCaseResult.Success) {
            assertThat("Didn't get expected id", result.id, equalTo(expectedId))
        }
    }
}
