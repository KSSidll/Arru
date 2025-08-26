package com.kssidll.arru.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

// Sanity check tests

@RunWith(AndroidJUnit4::class)
@MediumTest
class DatabaseUnitTest {
    private lateinit var db: AppDatabase
    private lateinit var shopDao: ShopEntityDao
    private lateinit var transactionDao: TransactionEntityDao
    private lateinit var productCategoryDao: ProductCategoryEntityDao
    private lateinit var productProducerDao: ProductProducerEntityDao
    private lateinit var productVariantDao: ProductVariantEntityDao
    private lateinit var productDao: ProductEntityDao
    private lateinit var itemDao: ItemEntityDao

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
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertShopEntityAndCheckExistsById() = runTest {
        shopEntityMockData.forEach { entity ->
            val expectingNone = shopDao.get(entity.id).first()
            shopDao.insert(entity)
            val expectingEntity = shopDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertTransactionEntityWithoutShopExpectingException() = runTest {
        val entity = transactionEntityMockData.first { it.shopEntityId != null }
        val expectingNone = transactionDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no ShopEntity was inserted
        transactionDao.insert(entity)
    }

    @Test
    fun insertTransactionEntityAndCheckExistsById() = runTest {
        shopEntityMockData.forEach { shopDao.insert(it) }

        transactionEntityMockData.forEach { entity ->
            val expectingNone = transactionDao.get(entity.id).first()
            transactionDao.insert(entity)
            val expectingEntity = transactionDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }

    @Test
    fun insertProductCategoryEntityAndCheckExistsById() = runTest {
        productCategoryEntityMockData.forEach { entity ->
            val expectingNone = productCategoryDao.get(entity.id).first()
            productCategoryDao.insert(entity)
            val expectingEntity = productCategoryDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }

    @Test
    fun insertProductProducerEntityAndCheckExistsById() = runTest {
        productProducerEntityMockData.forEach { entity ->
            val expectingNone = productProducerDao.get(entity.id).first()
            productProducerDao.insert(entity)
            val expectingEntity = productProducerDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertProductEntityWithoutCategoryExpectingException() = runTest {
        productProducerEntityMockData.forEach { productProducerDao.insert(it) }

        val entity = productEntityMockData.first()

        val expectingNone = transactionDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no ProductCategoryEntity was inserted
        productDao.insert(entity)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertProductEntityWithoutProducerExpectingException() = runTest {
        productCategoryEntityMockData.forEach { productCategoryDao.insert(it) }

        val entity = productEntityMockData.first { it.productProducerEntityId != null }

        val expectingNone = productDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no ProductProducerEntity was inserted
        productDao.insert(entity)
    }

    @Test
    fun insertProductEntityAndCheckExistsById() = runTest {
        productProducerEntityMockData.forEach { productProducerDao.insert(it) }
        productCategoryEntityMockData.forEach { productCategoryDao.insert(it) }

        productEntityMockData.forEach { entity ->
            val expectingNone = productDao.get(entity.id).first()
            productDao.insert(entity)
            val expectingEntity = productDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertProductVariantEntityWithoutProductExpectingException() = runTest {
        val entity = productVariantEntityMockData.first { it.productEntityId != null }
        val expectingNone = productVariantDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no ProductEntity was inserted
        productVariantDao.insert(entity)
    }

    @Test
    fun insertProductVariantEntityAndCheckExistsById() = runTest {
        productCategoryEntityMockData.forEach { productCategoryDao.insert(it) }
        productProducerEntityMockData.forEach { productProducerDao.insert(it) }
        productEntityMockData.forEach { productDao.insert(it) }

        productVariantEntityMockData.forEach { entity ->
            val expectingNone = productVariantDao.get(entity.id).first()
            productVariantDao.insert(entity)
            val expectingEntity = productVariantDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertItemEntityWithoutTransactionExpectingException() = runTest {
        productCategoryEntityMockData.forEach { productCategoryDao.insert(it) }
        productProducerEntityMockData.forEach { productProducerDao.insert(it) }
        productEntityMockData.forEach { productDao.insert(it) }
        productVariantEntityMockData.forEach { productVariantDao.insert(it) }

        val entity = itemEntityMockData.first()

        val expectingNone = itemDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no TransactionEntity was inserted
        itemDao.insert(entity)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertItemEntityWithoutProductExpectingException() = runTest {
        shopEntityMockData.forEach { shopDao.insert(it) }
        transactionEntityMockData.forEach { transactionDao.insert(it) }
        productVariantEntityMockData.forEach { productVariantDao.insert(it) }

        val entity = itemEntityMockData.first()

        val expectingNone = itemDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no ProductEntity was inserted
        itemDao.insert(entity)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertItemEntityWithoutVariantExpectingException() = runTest {
        shopEntityMockData.forEach { shopDao.insert(it) }
        transactionEntityMockData.forEach { transactionDao.insert(it) }
        productCategoryEntityMockData.forEach { productCategoryDao.insert(it) }
        productProducerEntityMockData.forEach { productProducerDao.insert(it) }
        productEntityMockData.forEach { productDao.insert(it) }

        val entity = itemEntityMockData.first { it.productVariantEntityId != null }

        val expectingNone = itemDao.get(entity.id).first()
        assertThat("Got entity before it was inserted", expectingNone, equalTo(null))

        // expecting SQLiteConstraintException since no ProductVariantEntity was inserted
        itemDao.insert(entity)
    }

    @Test
    fun insertItemEntityAndCheckExistsById() = runTest {
        shopEntityMockData.forEach { shopDao.insert(it) }
        transactionEntityMockData.forEach { transactionDao.insert(it) }
        productCategoryEntityMockData.forEach { productCategoryDao.insert(it) }
        productProducerEntityMockData.forEach { productProducerDao.insert(it) }
        productEntityMockData.forEach { productDao.insert(it) }
        productVariantEntityMockData.forEach { productVariantDao.insert(it) }

        itemEntityMockData.forEach { entity ->
            val expectingNone = itemDao.get(entity.id).first()
            itemDao.insert(entity)
            val expectingEntity = itemDao.get(entity.id).first()

            assertThat("Got entity before it was inserted", expectingNone, equalTo(null))
            assertThat(
                "Returned entity is not equal to the inserted one",
                expectingEntity,
                equalTo(entity),
            )
        }
    }
}
