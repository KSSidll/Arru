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
        ItemEntity::class,
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

        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                variantId INTEGER,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(variantId) REFERENCES ProductVariant(id) ON UPDATE RESTRICT ON DELETE RESTRICT
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

        db.execSQL("""
            DROP TABLE Item
        """.trimIndent())

        db.execSQL("""
            DROP TABLE TransactionBasketItem
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionId ON tmp_item (transactionId)
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productId ON tmp_item (productId)
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_ItemEntity_variantId ON tmp_item (variantId)
        """.trimIndent())

        /// Stage 2: Rename tables

        db.execSQL("""
            ALTER TABLE tmp_item RENAME TO ItemEntity
        """.trimIndent())

        db.execSQL("""
            ALTER TABLE TransactionBasket RENAME TO TransactionEntity
        """.trimIndent())
    }
}
