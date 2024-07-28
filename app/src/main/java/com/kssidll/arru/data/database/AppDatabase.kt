package com.kssidll.arru.data.database

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kssidll.arru.Arru
import com.kssidll.arru.BuildConfig
import com.kssidll.arru.data.dao.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.preference.AppPreferences
import java.io.File
import java.util.Calendar

/**
 * default database name
 */
const val DATABASE_NAME: String = "arru_database.db"

/**
 * default database backup directory name
 */
const val DATABASE_BACKUP_DIRECTORY_NAME: String = "db_backups"

/**
 * @return absolute path to external database file as [File]
 */
fun Context.externalDbFile(): File =
    File(getExternalFilesDir(null)!!.absolutePath.plus("/database/$DATABASE_NAME"))

/**
 * @return absolute path to internal database file as [File]
 */
fun Context.internalDbFile(): File = getDatabasePath(DATABASE_NAME)

/**
 * @return absolute path to currently used database file as [File]
 */
fun Context.currentDbFile(preferences: Preferences): File {
    return when (preferences[AppPreferences.Database.Location.key]) {
        AppPreferences.Database.Location.EXTERNAL -> {
            externalDbFile()
        }

        AppPreferences.Database.Location.INTERNAL -> {
            internalDbFile()
        }

        else -> error("The database location preference key isn't set to a valid value")
    }
}

fun Context.currentDbBackupDirectory(preferences: Preferences): File {
    val parent = currentDbFile(preferences).parentFile!!.absolutePath
    val dbBackup = File(parent.plus("/$DATABASE_BACKUP_DIRECTORY_NAME"))

    // create in case it doesn't exist
    dbBackup.mkdir()

    return dbBackup
}

@Database(
    version = 6,
    entities = [
        TransactionEntity::class,
        TransactionTagEntity::class,
        ItemEntity::class,
        ItemTagEntity::class,
        TagEntity::class,
        TagTagEntity::class,

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
        AutoMigration(
            from = 4,
            to = 5,
            spec = MIGRATION_4_5_SPEC::class
        ),
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getTransactionDao(): TransactionDao
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
                .addMigrations(MIGRATION_5_6)
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in internal location with [DATABASE_NAME] name, doesn't ensure database file creation
         */
        fun buildInternal(context: Context): AppDatabase {
            val builder = builder(context)

            // use version 1 database file to check if migrations work properly
            if (BuildConfig.DEBUG) {
                builder.createFromAsset("database/arru.db")
            }

            return builder.build()
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in external [Context.externalDbFile] location, doesn't ensure database file creation
         */
        fun buildExternal(context: Context): AppDatabase {
            return builder(
                context,
                context.externalDbFile().absolutePath
            ).build()
        }

        /**
         * copies database files from [fromDbFile] parent directory to [toDbFile] parent directory
         * @param fromDbFile absolute path to db file from whose parent directory to move database files
         * @param toDbFile absolute path to db file to whose parent directory to move database files
         */
        private fun copy(
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

            if (fromDbWalFile.exists()) {
                fromDbWalFile.copyTo(
                    toDbWalFile,
                    true
                )
            }

            if (fromDbShmFile.exists()) {
                fromDbShmFile.copyTo(
                    toDbShmFile,
                    true
                )
            }
        }

        /**
         * moves database files from [fromDbFile] parent directory to [toDbFile] parent directory
         * @param fromDbFile absolute path to db file from whose parent directory to move database files
         * @param toDbFile absolute path to db file to whose parent directory to move database files
         */
        private fun move(
            fromDbFile: File,
            toDbFile: File
        ) {
            // TODO add moving backups too when localization change is implemented
            copy(
                fromDbFile,
                toDbFile
            )

            delete(fromDbFile)
        }

        /**
         * delete [dbFile] database files
         * @param dbFile absolute path to the main db file to be deleted
         */
        private fun delete(dbFile: File) {
            val dbWalFile = File("${dbFile.path}-wal")
            val dbShmFile = File("${dbFile.path}-shm")

            dbFile.delete()

            if (dbWalFile.exists()) {
                dbWalFile.delete()
            }

            if (dbShmFile.exists()) {
                dbShmFile.delete()
            }
        }

        /**
         * moves the database files from external to internal location
         * @param context app context
         */
        fun moveExternalToInternal(context: Context) {
            val externalDbFile = context.externalDbFile()
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
            val externalDbFile = context.externalDbFile()
            val internalDbFile = context.internalDbFile()

            move(
                internalDbFile,
                externalDbFile
            )
        }

        /**
         * creates a database backup in current database location at set [time]
         * @param context app context
         * @param preferences app preferences
         * @param totalTransactions total transactions to stamp the database files with
         * @param totalSpending total spending to stamp the database files with
         * @param time time to stamp the database files with, current time by default
         * @return absolute path to created db backup file as [File]
         */
        fun saveDbBackup(
            context: Context,
            preferences: Preferences,
            totalTransactions: Int,
            totalSpending: Long,
            time: Long = Calendar.getInstance().timeInMillis
        ): File {
            val backupDir = context.currentDbBackupDirectory(preferences)
            val currentDbFile = context.currentDbFile(preferences)
            val backupDbFile = File(
                backupDir.absolutePath.plus(
                    "/${
                        DatabaseBackup.makeName(
                            time,
                            totalTransactions,
                            totalSpending
                        )
                    }.db"
                )
            )

            copy(
                currentDbFile,
                backupDbFile
            )

            return backupDbFile
        }

        /**
         * loads a database backup to current database location
         * @param context app context
         * @param preferences app preferences
         * @param backupDbFile database backup file to load
         */
        fun loadDbBackup(
            context: Context,
            preferences: Preferences,
            backupDbFile: DatabaseBackup
        ) {
            // TODO ensure that what we are loading is actually the database file
            val file = backupDbFile.file
            if (file.exists()) {
                val currentDbFile = context.currentDbFile(preferences)
                copy(
                    file,
                    currentDbFile
                )
            }

            // Required, if we don't restart, we can't add or edit the db, only read seems to work
            Arru.restart(context)
        }

        /**
         * delete [backupDbFile] database files
         * @param backupDbFile absolute path to the main db file to be deleted
         */
        fun deleteDbBackup(backupDbFile: DatabaseBackup) {
            // TODO ensure that what we are deleting is actually the database file
            val file = backupDbFile.file
            if (file.exists()) {
                delete(file)
            }
        }

        /**
         * @param context app context
         * @param preferences app preferences
         * @return all available backups as a list of [File], sorted newest to oldest
         */
        fun availableBackups(
            context: Context,
            preferences: Preferences
        ): List<DatabaseBackup> {
            val directory = context.currentDbBackupDirectory(preferences)

            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles { file ->
                    // return only files that are databases, ignore shm, wal and other files
                    file.extension == "db"
                }

                if (files != null) {
                    // TODO ensure that the mapped file is actually the database file
                    val dbFiles = files.mapNotNull {
                        try {
                            DatabaseBackup.fromFile(it)
                        } catch (_: IllegalStateException) {
                            null
                        }
                    }

                    return dbFiles.toList()
                        .sortedByDescending { it.time }
                }
            }

            return emptyList()
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

@Suppress("ClassName")
@DeleteColumn(
    "Item",
    "shopId"
)
@DeleteColumn(
    "Item",
    "date"
)
class MIGRATION_4_5_SPEC: AutoMigrationSpec

val MIGRATION_5_6 = object: Migration(
    5,
    6
) {
    override fun migrate(db: SupportSQLiteDatabase) {

        /// Stage 1: Remove TransactionBasketItem
        /// Move transactionId to Item

        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                variantId INTEGER,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(variantId) REFERENCES ProductVariant(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO tmp_item (transactionId, productId, variantId, quantity, price)
                SELECT TransactionBasketItem.transactionBasketId, productId, variantId, quantity, price
                FROM Item
                JOIN TransactionBasketItem ON Item.id = TransactionBasketItem.itemId
            """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE Item
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE TransactionBasketItem
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionId ON tmp_item (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productId ON tmp_item (productId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_variantId ON tmp_item (variantId)
        """.trimIndent()
        )

        /// Stage 1.5: Rename Item to ItemEntity

        db.execSQL(
            """
            ALTER TABLE tmp_item RENAME TO ItemEntity
        """.trimIndent()
        )

        /// Stage 2: Change constraints
        /// Using Restrict was bug prone and Cascade should work better with a tag system

        db.execSQL(
            """
            CREATE TABLE tmp_transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                shopId INTEGER,
                totalCost INTEGER NOT NULL,
                FOREIGN KEY(shopId) REFERENCES Shop(id) ON UPDATE CASCADE ON DELETE SET NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO tmp_transactions (id, date, shopId, totalCost)
                SELECT id, date, shopId, totalCost
                FROM TransactionBasket
            """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE TransactionBasket
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_date ON tmp_transactions (date)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_shopId ON tmp_transactions (shopId)
        """.trimIndent()
        )

        /// Stage 2.5: Rename TransactionBasket to TransactionEntity

        db.execSQL(
            """
            ALTER TABLE tmp_transactions RENAME TO TransactionEntity
        """.trimIndent()
        )

        /// Stage 3: Add tag tables
        /// TagEntity, ItemTagEntity, TransactionTagEntity and TagTagEntity

        db.execSQL(
            """
            CREATE TABLE TagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                colorOrdinal INTEGER NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ItemTagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                itemId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                FOREIGN KEY(itemId) REFERENCES ItemEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(tagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemTagEntity_itemId ON ItemTagEntity (itemId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemTagEntity_tagId ON ItemTagEntity (tagId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE TransactionTagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(tagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionTagEntity_transactionId ON TransactionTagEntity (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionTagEntity_tagId ON TransactionTagEntity (tagId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE TagTagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mainTagId INTEGER NOT NULL,
                subTagId INTEGER NOT NULL,
                FOREIGN KEY(mainTagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(subTagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TagTagEntity_mainTagId ON TagTagEntity (mainTagId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TagTagEntity_subTagId ON TagTagEntity (subTagId)
        """.trimIndent()
        )

        /// Stage 4: Migrate Shop data

        // Add special/system shop tag

        db.execSQL("""
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.SHOP.id}, '${TagEntity.System.SHOP.name}', ${TagEntity.System.SHOP.color.ordinal}
        """.trimIndent())

        // Add shop tags

        db.execSQL("""
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT name, ${TagEntity.System.SHOP.color.ordinal}
            FROM Shop
        """.trimIndent())

        // Set shop tags as subtags of system shop tag

        db.execSQL("""
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.SHOP.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM Shop)
        """.trimIndent())

        // Add shop tags to the associated transactions

        val shopTagsCursor = db.query("""
            SELECT Shop.id, TagEntity.id
            FROM TagEntity
            JOIN Shop ON Shop.name = TagEntity.name
        """.trimIndent())

        if (shopTagsCursor.moveToFirst()) {
            do {
                val shopId = shopTagsCursor.getLong(0)
                val tagId = shopTagsCursor.getLong(1)

                db.execSQL("""
                    INSERT INTO TransactionTagEntity (transactionId, tagId)
                    SELECT id, $tagId FROM TransactionEntity WHERE shopId = $shopId
                """.trimIndent())
            } while (shopTagsCursor.moveToNext())
        }

        shopTagsCursor.close()

        /// Remove unused tags
        db.execSQL("""
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN TransactionTagEntity ON TransactionTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.SHOP.id}
                AND TransactionTagEntity.tagId IS NULL
            )
        """.trimIndent())

        /// Stage 5: Migrate ProductVariant data

        // Add special/system variant tag

        db.execSQL("""
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.VARIANT.id}, '${TagEntity.System.VARIANT.name}', ${TagEntity.System.VARIANT.color.ordinal}
        """.trimIndent())

        // Add variant tags

        db.execSQL("""
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagEntity.System.VARIANT.color.ordinal}
            FROM ProductVariant
        """.trimIndent())

        // Set variant tags as subtags of system variant tag

        db.execSQL("""
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.VARIANT.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM ProductVariant)
                AND TagEntity.id NOT IN (
                    SELECT TagEntity.id
                    FROM TagEntity
                    JOIN TagTagEntity
                    ON TagTagEntity.subTagId = TagEntity.id
                        OR TagTagEntity.mainTagId = TagEntity.id
                )
        """.trimIndent())

        // Add variant tags to the associated items

        val variantTagsCursor = db.query("""
            SELECT ProductVariant.id, TagEntity.id
            FROM ProductVariant
            JOIN TagEntity ON TagEntity.name = ProductVariant.name
            AND TagEntity.id IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.VARIANT.id}
            )
        """.trimIndent())

        if (variantTagsCursor.moveToFirst()) {
            do {
                val variantId = variantTagsCursor.getLong(0)
                val tagId = variantTagsCursor.getLong(1)

                db.execSQL("""
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT id, $tagId FROM ItemEntity WHERE variantId = $variantId
                """.trimIndent())
            } while (variantTagsCursor.moveToNext())
        }

        variantTagsCursor.close()

        /// Remove unused tags
        db.execSQL("""
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.VARIANT.id}
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent())

        // Remove redundant variantId column from ItemEntity

        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO tmp_item (transactionId, productId, quantity, price)
                SELECT transactionId, productId, quantity, price
                FROM ItemEntity
            """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ItemEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionId ON tmp_item (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productId ON tmp_item (productId)
        """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE tmp_item RENAME TO ItemEntity
        """.trimIndent()
        )

        /// Stage 6: Migrate ProductProducer data

        // Add special/system producer tag

        db.execSQL("""
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.PRODUCER.id}, '${TagEntity.System.PRODUCER.name}', ${TagEntity.System.PRODUCER.color.ordinal}
        """.trimIndent())

        // Add producer tags

        db.execSQL("""
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagEntity.System.PRODUCER.color.ordinal}
            FROM ProductProducer
        """.trimIndent())

        // Set producer tags as subtags of system producer tag

        db.execSQL("""
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.PRODUCER.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM ProductProducer)
                AND TagEntity.id NOT IN (
                    SELECT TagEntity.id
                    FROM TagEntity
                    JOIN TagTagEntity
                    ON TagTagEntity.subTagId = TagEntity.id
                        OR TagTagEntity.mainTagId = TagEntity.id
                )
        """.trimIndent())

        // Add producer tags to the associated items

        val producerTagsCursor = db.query("""
            SELECT ProductProducer.id, TagEntity.id
            FROM ProductProducer
            JOIN TagEntity ON TagEntity.name = ProductProducer.name
            AND TagEntity.id IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCER.id}
            )
        """.trimIndent())

        if (producerTagsCursor.moveToFirst()) {
            do {
                val producerId = producerTagsCursor.getLong(0)
                val tagId = producerTagsCursor.getLong(1)

                db.execSQL("""
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT ItemEntity.id, $tagId
                    FROM ItemEntity
                    JOIN Product ON Product.id = ItemEntity.productId
                    JOIN ProductProducer ON ProductProducer.id = Product.producerId
                    WHERE ProductProducer.id = $producerId
                """.trimIndent())
            } while (producerTagsCursor.moveToNext())
        }

        producerTagsCursor.close()

        /// Remove unused tags
        db.execSQL("""
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCER.id}
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent())

        /// Stage 7: Migrate Product data

        // Add special/system product tag

        db.execSQL("""
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.PRODUCT.id}, '${TagEntity.System.PRODUCT.name}', ${TagEntity.System.PRODUCT.color.ordinal}
        """.trimIndent())

        // Add product tags

        db.execSQL("""
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagEntity.System.PRODUCT.color.ordinal}
            FROM Product
        """.trimIndent())

        // Set product tags as subtags of system product tag

        db.execSQL("""
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.PRODUCER.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM Product)
                AND TagEntity.id NOT IN (
                    SELECT TagEntity.id
                    FROM TagEntity
                    JOIN TagTagEntity
                    ON TagTagEntity.subTagId = TagEntity.id
                        OR TagTagEntity.mainTagId = TagEntity.id
                )
        """.trimIndent())

        // Add product tags to the associated items

        val productTagsCursor = db.query("""
            SELECT Product.id, TagEntity.id
            FROM Product
            JOIN TagEntity ON TagEntity.name = Product.name
            AND TagEntity.id IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCER.id}
            )
        """.trimIndent())

        if (productTagsCursor.moveToFirst()) {
            do {
                val productId = productTagsCursor.getLong(0)
                val tagId = productTagsCursor.getLong(1)

                db.execSQL("""
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT ItemEntity.id, $tagId
                    FROM ItemEntity
                    JOIN Product ON Product.id = ItemEntity.productId
                    WHERE Product.id = $productId
                """.trimIndent())
            } while (productTagsCursor.moveToNext())
        }

        productTagsCursor.close()

        /// Remove unused tags
        db.execSQL("""
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCT.id}
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent())

        /// Stage 8: Migrate ProductCategory data
        // TODO (not system tags?)

        /// Stage 9: Delete redundant tables
        // TODO
    }
}
