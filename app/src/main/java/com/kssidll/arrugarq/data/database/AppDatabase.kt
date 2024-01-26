package com.kssidll.arrugarq.data.database

import android.content.*
import androidx.room.*
import androidx.room.migration.*
import androidx.sqlite.db.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.di.module.*
import java.io.*

@Database(
    version = 4,
    entities = [
        TransactionBasket::class,
        TransactionBasketItem::class,
        Item::class,
        Product::class,
        ProductAltName::class,
        ProductVariant::class,
        ProductCategory::class,
        ProductCategoryAltName::class,
        Shop::class,
        ProductProducer::class,
    ],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
        AutoMigration(
            from = 2,
            to = 3
        ),
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getTransactionBasketDao(): TransactionBasketDao
    abstract fun getItemDao(): ItemDao
    abstract fun getProductDao(): ProductDao
    abstract fun getVariantDao(): VariantDao
    abstract fun getCategoryDao(): CategoryDao
    abstract fun getShopDao(): ShopDao
    abstract fun getProducerDao(): ProducerDao

    companion object {
        /**
         * @param context app context
         * @param name name of the database or absolute path if not internal location, defaults to internal location with [DATABASE_NAME] name
         * @return [RoomDatabase.Builder] of [AppDatabase] for [name]
         */
        private fun builder(
            context: Context,
            name: String = DATABASE_NAME
        ): Builder<AppDatabase> {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                name
            )
                .addMigrations(MIGRATION_3_4)
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in internal location with [DATABASE_NAME] name, doesn't ensure database file creation
         */
        fun buildInternal(context: Context): AppDatabase {
            return builder(context).createFromAsset("database/arrugarq.db")
                .build()
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in external [Context.externalDbPath] location, doesn't ensure database file creation
         */
        fun buildExternal(context: Context): AppDatabase {
            return builder(
                context,
                context.externalDbPath()
            ).build()
        }

        /**
         * moves database files from [fromDbFile] parent directory to [toDbFile] parent directory
         * @param fromDbFile absolute path to db file from whose parent directory to move database files
         * @param toDbFile absolute path to db file to whose parent directory to move database files
         */
        fun move(
            fromDbFile: File,
            toDbFile: File
        ) {
            val fromDbWalFile = File("${fromDbFile.path}-wal")
            val fromDbShmFile = File("${fromDbFile.path}-shm")

            val toDbWalFile = File("${toDbFile.path}-wal")
            val toDbShmFile = File("${toDbFile.path}-shm")

            if (toDbWalFile.exists()) toDbWalFile.delete()
            if (toDbShmFile.exists()) toDbShmFile.delete()

            fromDbFile.copyTo(
                toDbFile,
                true
            )
            fromDbFile.delete()

            if (fromDbWalFile.exists()) {
                fromDbWalFile.copyTo(
                    toDbWalFile,
                    true
                )
                fromDbWalFile.delete()
            }

            if (fromDbShmFile.exists()) {
                fromDbShmFile.copyTo(
                    toDbShmFile,
                    true
                )
                fromDbShmFile.delete()
            }
        }

        /**
         * moves the database files from external to internal location
         * @param context app context
         */
        fun moveExternalToInternal(context: Context) {
            val externalDbFile = File(context.externalDbPath())
            val internalDbFile = context.internalDbFile()

            move(
                externalDbFile,
                internalDbFile
            )
        }

        /**
         * moves the database files from internal to external location
         * @param context app context
         */
        fun moveInternalToExternal(context: Context) {
            val externalDbFile = File(context.externalDbPath())
            val internalDbFile = context.internalDbFile()

            move(
                internalDbFile,
                externalDbFile
            )
        }
    }
}

val MIGRATION_3_4 = object: Migration(
    3,
    4
) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO transactionbasket(date, shopId, totalCost)
            SELECT date, COALESCE(shopId, 0) as shopId, SUM(price * quantity) / 1000 as totalCost
            FROM item
            GROUP BY shopId, date
            ORDER BY date ASC
        """.trimIndent()
        )

        val basketCount = db.query("SELECT * FROM transactionbasket").count

        for (basketId in 1..basketCount) {
            db.execSQL(
                """
                INSERT INTO transactionbasketitem(transactionbasketid, itemid)
                SELECT transactionbasket.id, item.id
                FROM transactionbasket
                JOIN item ON item.date = transactionbasket.date AND item.shopId = transactionbasket.shopId
                WHERE transactionbasket.id = ?
            """.trimIndent(),
                arrayOf(basketId)
            )
        }
    }
}