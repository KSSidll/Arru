package com.kssidll.arru.data.database

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kssidll.arru.APPLICATION_NAME
import com.kssidll.arru.Arru
import com.kssidll.arru.data.dao.CategoryDao
import com.kssidll.arru.data.dao.ItemDao
import com.kssidll.arru.data.dao.ProducerDao
import com.kssidll.arru.data.dao.ProductDao
import com.kssidll.arru.data.dao.ShopDao
import com.kssidll.arru.data.dao.TransactionBasketDao
import com.kssidll.arru.data.dao.VariantDao
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductAltName
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductCategoryAltName
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket
import com.kssidll.arru.data.database.AppDatabase.Companion.move
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getDatabaseLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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

fun Context.internalDbFile(): File = getDatabasePath(DATABASE_NAME)
fun Context.downloadsAppDirectory(): File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath.plus("/${APPLICATION_NAME}"))
fun Context.downloadsDbFile(): File = File(downloadsAppDirectory().absolutePath.plus("/$DATABASE_NAME"))

/**
 * @return absolute path to currently used database file as [File]
 */
suspend fun Context.currentDbFile(): File {
    return when (AppPreferences.getDatabaseLocation(this).first()) {
        is AppPreferences.Database.Location.Values.DOWNLOADS -> {
            downloadsDbFile()
        }

        is AppPreferences.Database.Location.Values.INTERNAL -> {
            internalDbFile()
        }
    }
}

suspend fun Context.currentDbBackupDirectory(): File {
    val parent = currentDbFile().parentFile!!.absolutePath
    val dbBackup = File(parent.plus("/$DATABASE_BACKUP_DIRECTORY_NAME"))

    // create in case it doesn't exist
    dbBackup.mkdir()

    return dbBackup
}

@Database(
    version = 7,
    entities = [
        TransactionBasket::class,
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
        AutoMigration(
            from = 4,
            to = 5,
            spec = MIGRATION_4_5_SPEC::class
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
        fun builder(
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
                .addMigrations(MIGRATION_6_7(context))
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in internal location with [DATABASE_NAME] name, doesn't ensure database file creation
         */
        fun buildInternal(context: Context): AppDatabase {
            val builder = builder(context)

            return builder.build()
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in external [Context.externalDbFile] location, doesn't ensure database file creation
         */
        fun buildExternal(context: Context, absolutePath: String): AppDatabase {
            return builder(
                context,
                absolutePath
            ).build()
        }

        /**
         * copies database files from [fromDbFile] parent directory to [toDbFile] parent directory
         * @param fromDbFile absolute path to db file from whose parent directory to move database files
         * @param toDbFile absolute path to db file to whose parent directory to move database files
         */
        fun copy(
            fromDbFile: File,
            toDbFile: File
        ) {
            val fromDbWalFile = File("${fromDbFile.absolutePath}-wal")
            val fromDbShmFile = File("${fromDbFile.absolutePath}-shm")

            val toDbWalFile = File("${toDbFile.absolutePath}-wal")
            val toDbShmFile = File("${toDbFile.absolutePath}-shm")

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
         * moves database files and backups from [fromDbFile] parent directory to [toDbFile] parent directory
         * @param context app context
         * @param fromDbFile absolute path to db file from whose parent directory to move database files
         * @param toDbFile absolute path to db file to whose parent directory to move database files
         */
        fun move(
            context: Context,
            fromDbFile: File,
            toDbFile: File
        ) {
            var availableBackups: List<DatabaseBackup> = emptyList()
            val fromDbBackup = File(fromDbFile.parentFile!!.absolutePath.plus("/$DATABASE_BACKUP_DIRECTORY_NAME"))
            val toDbBackup = File(toDbFile.parentFile!!.absolutePath.plus("/$DATABASE_BACKUP_DIRECTORY_NAME"))

            runBlocking {
                if (fromDbBackup.exists()) {
                    availableBackups = availableBackups(context, fromDbBackup)
                }
            }

            availableBackups.forEach {
                copy(
                    it.file,
                    File(toDbBackup.absolutePath.plus("/${it.name}.db"))
                )

                delete(it.file)
            }

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
        fun delete(dbFile: File) {
            val dbWalFile = File("${dbFile.absolutePath}-wal")
            val dbShmFile = File("${dbFile.absolutePath}-shm")

            dbFile.delete()

            if (dbWalFile.exists()) {
                dbWalFile.delete()
            }

            if (dbShmFile.exists()) {
                dbShmFile.delete()
            }
        }

        fun lockDbBackup(databaseBackup: DatabaseBackup) {
            if (databaseBackup.file.parent == null) {
                Log.d("APP_DATABASE", "lockDbBackup: failed to get file parent, aborting")
                return
            }

            val newName = DatabaseBackup.makeName(
                time = databaseBackup.time,
                totalTransactions = databaseBackup.totalTransactions,
                totalSpending = databaseBackup.totalSpending,
                locked = true
            ).plus(".db")

            val walFile = File(databaseBackup.file.absolutePath.plus("-wal"))
            val shmFile = File(databaseBackup.file.absolutePath.plus("-shm"))

            databaseBackup.file.renameTo(
                File(databaseBackup.file.parent, newName)
            )

            if (walFile.exists()) {
                walFile.renameTo(
                    File(databaseBackup.file.parent, newName.plus("-wal"))
                )
            }

            if (shmFile.exists()) {
                shmFile.renameTo(
                    File(databaseBackup.file.parent, newName.plus("-shm"))
                )
            }
        }

        fun unlockDbBackup(databaseBackup: DatabaseBackup) {
            if (databaseBackup.file.parent == null) {
                Log.d("APP_DATABASE", "unlockDbBackup: failed to get file parent, aborting")
                return
            }

            val newName = DatabaseBackup.makeName(
                time = databaseBackup.time,
                totalTransactions = databaseBackup.totalTransactions,
                totalSpending = databaseBackup.totalSpending,
                locked = false
            ).plus(".db")

            val walFile = File(databaseBackup.file.absolutePath.plus("-wal"))
            val shmFile = File(databaseBackup.file.absolutePath.plus("-shm"))

            databaseBackup.file.renameTo(
                File(databaseBackup.file.parent, newName)
            )

            if (walFile.exists()) {
                walFile.renameTo(
                    File(databaseBackup.file.parent, newName.plus("-wal"))
                )
            }

            if (shmFile.exists()) {
                shmFile.renameTo(
                    File(databaseBackup.file.parent, newName.plus("-shm"))
                )
            }
        }

        /**
         * creates a database backup in current database location at set [time]
         * @param context app context
         * @param totalTransactions total transactions to stamp the database files with
         * @param totalSpending total spending to stamp the database files with
         * @param time time to stamp the database files with, current time by default
         * @return absolute path to created db backup file as [File]
         */
        suspend fun saveDbBackup(
            context: Context,
            totalTransactions: Int,
            totalSpending: Long,
            time: Long = Calendar.getInstance().timeInMillis
        ): File {
            val backupDir = context.currentDbBackupDirectory()
            val currentDbFile = context.currentDbFile()
            val backupDbFile = File(
                backupDir.absolutePath.plus(
                    "/${
                        DatabaseBackup.makeName(
                            time,
                            totalTransactions,
                            totalSpending,
                            false
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
         * @param backupDbFile database backup file to load
         */
        suspend fun loadDbBackup(
            context: Context,
            backupDbFile: DatabaseBackup
        ) {
            // TODO ensure that what we are loading is actually the database file
            val file = backupDbFile.file
            if (file.exists()) {
                val currentDbFile = context.currentDbFile()
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
         * @return all available backups as a list of [File], sorted newest to oldest
         */
        suspend fun availableBackups(
            context: Context,
            directory: File? = null
        ): List<DatabaseBackup> {
            @Suppress("LocalVariableName")
            val _directory = directory ?: context.currentDbBackupDirectory()

            if (_directory.exists() && _directory.isDirectory) {
                val files = _directory.listFiles { file ->
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

                    var changed = false
                    dbFiles.forEach {
                        if (!it.hasLockMarkInName) {
                            unlockDbBackup(it) // unlock backup files that don't have lock status in the name
                            changed = true
                        }
                    }

                    return if (changed) {
                        availableBackups(context)
                    } else {
                        dbFiles.toList().sortedByDescending { it.time }
                    }
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

val MIGRATION_5_6 = object: Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionBasketId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                variantId INTEGER,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionBasketId) REFERENCES TransactionBasket(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(variantId) REFERENCES ProductVariant(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO tmp_item (transactionBasketId, productId, variantId, quantity, price)
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
            ALTER TABLE tmp_item RENAME TO Item
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_Item_transactionBasketId ON Item (transactionBasketId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_Item_productId ON Item (productId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_Item_variantId ON Item (variantId)
        """.trimIndent()
        )
    }
}

@Suppress("ClassName")
class MIGRATION_6_7(private val context: Context): Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Attempt to migrate from external storage to internal storage as previous implementation was external storage hard coded
        try {
            val externalDbFile = File(context.getExternalFilesDir(null)!!.absolutePath.plus("/database/arru_database.db"))
            val internalDbFile = context.getDatabasePath(DATABASE_NAME)

            move(
                context,
                externalDbFile,
                internalDbFile
            )
        } catch (_: Exception) {}
    }
}