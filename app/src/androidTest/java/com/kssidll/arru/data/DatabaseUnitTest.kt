package com.kssidll.arru.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.data.ShopEntity
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
class DatabaseUnitTest {
    private lateinit var dao: ShopEntityDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.getShopEntityDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertShopEntityAndCheckExistsByName() = runTest {
        val entity = ShopEntity(7, "test")

        dao.insert(entity)

        val byName = dao.byName("test").first()

        assertThat(byName, equalTo(entity))
    }
}