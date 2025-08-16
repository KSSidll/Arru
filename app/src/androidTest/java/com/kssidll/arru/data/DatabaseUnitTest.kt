package com.kssidll.arru.data

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
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
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
        productVariantDao = db.getProductVariantEntityDao()
        productDao = db.getProductEntityDao()
        itemDao = db.getItemEntityDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /** Check by ID */

    @Test
    @Throws(Exception::class)
    fun insertShopEntityAndCheckExistsById() = runTest {
        val id = 7L
        val entity = ShopEntity(id, "")

        shopDao.insert(entity)

        val byId = shopDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }

    @Test
    @Throws(Exception::class)
    fun insertTransactionEntityAndCheckExistsById() = runTest {
        val id = 7L
        val entity = TransactionEntity(id, 0, null, 0, null)

        transactionDao.insert(entity)

        val byId = transactionDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }

    @Test
    @Throws(Exception::class)
    fun insertProductCategoryEntityAndCheckExistsById() = runTest {
        val id = 7L
        val entity = ProductCategoryEntity(id, "")

        productCategoryDao.insert(entity)

        val byId = productCategoryDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }

    @Test
    @Throws(Exception::class)
    fun insertProductProducerEntityAndCheckExistsById() = runTest {
        val id = 7L
        val entity = ProductProducerEntity(id, "")

        productProducerDao.insert(entity)

        val byId = productProducerDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }

    @Test
    @Throws(Exception::class)
    fun insertProductVariantEntityAndCheckExistsById() = runTest {
        val id = 7L
        val entity = ProductVariantEntity(id, null, "")

        productVariantDao.insert(entity)

        val byId = productVariantDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }

    @Test
    @Throws(Exception::class)
    fun insertProductEntityAndCheckExistsById() = runTest {
        val id = 7L
        val categoryEntity = ProductCategoryEntity(1, "")
        val entity = ProductEntity(id, categoryEntity.id, null, "")

        productCategoryDao.insert(categoryEntity)
        productDao.insert(entity)

        val byId = productDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }

    @Test
    @Throws(Exception::class)
    fun insertItemEntityAndCheckExistsById() = runTest {
        val id = 7L
        val transactionEntity = TransactionEntity(1, 0, null, 0, null)
        val categoryEntity = ProductCategoryEntity(1, "")
        val productEntity = ProductEntity(id, categoryEntity.id, null, "")
        val entity = ItemEntity(id, transactionEntity.id, productEntity.id, null, 0, 0)

        transactionDao.insert(transactionEntity)
        productCategoryDao.insert(categoryEntity)
        productDao.insert(productEntity)
        itemDao.insert(entity)

        val byId = itemDao.get(id).first()

        assertThat("Returned entity is not equal to the inserted one", byId, equalTo(entity))
    }
}