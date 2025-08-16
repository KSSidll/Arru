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
import com.kssidll.arru.data.dao.ExportDao
import com.kssidll.arru.data.dao.ImportDao
import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.dao.ProductEntityDao
import com.kssidll.arru.data.dao.ProductProducerEntityDao
import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getDatabaseLocation
import com.kssidll.arru.data.view.Item
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
fun Context.externalDbFile(): File = File(getExternalFilesDir(null)!!.absolutePath.plus("/database/$DATABASE_NAME"))

fun Context.downloadsAppDirectory(): File =
    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath.plus("/${APPLICATION_NAME}"))

fun Context.downloadsBackupDirectory(): File =
    File(downloadsAppDirectory().absolutePath.plus("/$DATABASE_BACKUP_DIRECTORY_NAME"))

fun Context.downloadsDbFile(): File = File(downloadsAppDirectory().absolutePath.plus("/$DATABASE_NAME"))

/**
 * @return absolute path to currently used database file as [File]
 */
suspend fun Context.currentDbFile(): File {
    return when (AppPreferences.getDatabaseLocation(this).first()) {
        AppPreferences.Database.Location.Values.INTERNAL -> {
            internalDbFile()
        }

        AppPreferences.Database.Location.Values.EXTERNAL -> {
            externalDbFile()
        }

        AppPreferences.Database.Location.Values.DOWNLOADS -> {
            downloadsDbFile()
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
    version = 9,
    entities = [
        TransactionEntity::class,
        ItemEntity::class,
        ProductEntity::class,
        ProductVariantEntity::class,
        ProductCategoryEntity::class,
        ShopEntity::class,
        ProductProducerEntity::class,
    ],
    views = [
        Item::class
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
        )
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getTransactionEntityDao(): TransactionEntityDao
    abstract fun getItemEntityDao(): ItemEntityDao
    abstract fun getProductEntityDao(): ProductEntityDao
    abstract fun getProductVariantEntityDao(): ProductVariantEntityDao
    abstract fun getProductCategoryEntityDao(): ProductCategoryEntityDao
    abstract fun getShopEntityDao(): ShopEntityDao
    abstract fun getProductProducerEntityDao(): ProductProducerEntityDao
    abstract fun getExportDao(): ExportDao
    abstract fun getImportDao(): ImportDao

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
                .addMigrations(MIGRATION_7_8)
                .addMigrations(MIGRATION_8_9)
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
         * @return [AppDatabase] created in external location, doesn't ensure database file creation
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
         * @throws Exception if the operation fails (original files are not deleted if that happens)
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

            // COPY

            copy(
                fromDbFile,
                toDbFile
            )

            availableBackups.forEach {
                copy(
                    it.file,
                    File(toDbBackup.absolutePath.plus("/${it.name}.db"))
                )
            }

            // DOUBLE CHECK

            if (!toDbFile.exists()) throw IllegalStateException("Failed to copy the database from [${fromDbFile.absolutePath}] to [${toDbFile.absolutePath}]")

            availableBackups.forEach {
                val fromBackupDbWalFile = File("${it.file.absolutePath}-wal")
                val fromBackupDbShmFile = File("${it.file.absolutePath}-shm")

                val toBackupDbFile = File(toDbBackup.absolutePath.plus("/${it.name}.db"))
                val toBackupDbWalFile = File("${toBackupDbFile.absolutePath}-wal")
                val toBackupDbShmFile = File("${toBackupDbFile.absolutePath}-shm")

                if (!toBackupDbFile.exists()) throw IllegalStateException("Failed to copy the database from [${fromDbFile.absolutePath}] to [${toDbFile.absolutePath}]")
                if (fromBackupDbWalFile.exists() && !toBackupDbWalFile.exists()) throw IllegalStateException("Failed to copy the database from [${fromDbFile.absolutePath}] to [${toDbFile.absolutePath}]")
                if (fromBackupDbShmFile.exists() && !toBackupDbShmFile.exists()) throw IllegalStateException("Failed to copy the database from [${fromDbFile.absolutePath}] to [${toDbFile.absolutePath}]")
            }

            // CLEAN UP

            delete(fromDbFile)

            availableBackups.forEach {
                delete(it.file)
            }
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

                    // TOOD check if this is necessary coz why is this even here
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
        // Attempt to fix wal and shm backup files
        try {
            val externalDbFile =
                File(context.getExternalFilesDir(null)!!.absolutePath.plus("/database/arru_database.db"))

            runBlocking {
                // Fixes names of wal and shm backup files before moving
                val backups =
                    AppDatabase.availableBackups(
                        context,
                        File(externalDbFile.parentFile!!.absolutePath.plus("/db_backups"))
                    )

                backups.forEach { backup ->
                    if (backup.hasLockMarkInName) {
                        val walFile = File(backup.file.absolutePath.replace("true", "false").plus("-wal"))
                        val shmFile = File(backup.file.absolutePath.replace("true", "false").plus("-shm"))

                        if (walFile.exists()) {
                            walFile.renameTo(
                                File(backup.file.absolutePath.plus("-wal"))
                            )
                        }

                        if (shmFile.exists()) {
                            shmFile.renameTo(
                                File(backup.file.absolutePath.plus("-shm"))
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
    }
}

val MIGRATION_7_8 = object: Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Make productId nullable for global variants
        db.execSQL(
            """
            CREATE TABLE tmp_ProductVariant (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                productId INTEGER,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO tmp_ProductVariant (id, name, productId)
            SELECT id, name, productId 
            FROM ProductVariant
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE ProductVariant
        """.trimIndent())

        db.execSQL(
            """
            ALTER TABLE tmp_ProductVariant RENAME TO ProductVariant
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductVariant_productId ON ProductVariant (productId)
        """.trimIndent())

        // Add note field to transactions
        db.execSQL(
            """
            CREATE TABLE tmp_TransactionBasket (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                shopId INTEGER,
                totalCost INTEGER NOT NULL,
                note TEXT,
                FOREIGN KEY(shopId) REFERENCES Shop(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO tmp_TransactionBasket (id, date, shopId, totalCost, note)
            SELECT id, date, shopId, totalCost, NULL
            FROM TransactionBasket
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE TransactionBasket
        """.trimIndent())

        db.execSQL(
            """
            ALTER TABLE tmp_TransactionBasket RENAME TO TransactionBasket
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionBasket_date ON TransactionBasket(date)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionBasket_shopId ON TransactionBasket(shopId)
        """.trimIndent())
    }
}

val MIGRATION_8_9 = object: Migration(
    8,
    9
) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Remove deprecated tables
        db.execSQL(
            """
            DROP TABLE ProductAltName
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE ProductCategoryAltName
        """.trimIndent())

        // Rename Shop
        db.execSQL(
            """
            CREATE TABLE ShopEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO ShopEntity (id, name)
            SELECT id, name
            FROM Shop
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE Shop
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ShopEntity_name ON ShopEntity(name)
        """.trimIndent())

        // Rename TransactionBasket
        db.execSQL(
            """
            CREATE TABLE TransactionEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                shopEntityId INTEGER,
                totalCost INTEGER NOT NULL,
                note TEXT,
                FOREIGN KEY(shopEntityId) REFERENCES ShopEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO TransactionEntity (id, date, shopEntityId, totalCost, note)
            SELECT id, date, shopId, totalCost, note
            FROM TransactionBasket
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE TransactionBasket
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_date ON TransactionEntity(date)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_shopEntityId ON TransactionEntity(shopEntityId)
        """.trimIndent())


        // Rename ProductProducer
        db.execSQL(
            """
            CREATE TABLE ProductProducerEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO ProductProducerEntity (id, name)
            SELECT id, name
            FROM ProductProducer
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE ProductProducer
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductProducerEntity_name ON ProductProducerEntity(name)
        """.trimIndent())

        // Rename ProductCategory
        db.execSQL(
            """
            CREATE TABLE ProductCategoryEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO ProductCategoryEntity (id, name)
            SELECT id, name
            FROM ProductCategory
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE ProductCategory
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductCategoryEntity_name ON ProductCategoryEntity(name)
        """.trimIndent())

        // Rename Product
        db.execSQL(
            """
            CREATE TABLE ProductEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                productCategoryEntityId INTEGER NOT NULL,
                productProducerEntityId INTEGER,
                name TEXT NOT NULL,
                FOREIGN KEY(productCategoryEntityId) REFERENCES ProductCategoryEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(productProducerEntityId) REFERENCES ProductProducerEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO ProductEntity (id, productCategoryEntityId, productProducerEntityId, name)
            SELECT id, categoryId, producerId, name
            FROM Product
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE Product
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductEntity_productCategoryEntityId ON ProductEntity(productCategoryEntityId)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductEntity_productProducerEntityId ON ProductEntity(productProducerEntityId)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductEntity_name ON ProductEntity(name)
        """.trimIndent())

        // Rename ProductVariant
        db.execSQL(
            """
            CREATE TABLE ProductVariantEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                productEntityId INTEGER,
                name TEXT NOT NULL,
                FOREIGN KEY(productEntityId) REFERENCES ProductEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO ProductVariantEntity (id, productEntityId, name)
            SELECT id, productId, name
            FROM ProductVariant
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE ProductVariant
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductVariantEntity_productEntityId ON ProductVariantEntity(productEntityId)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductVariantEntity_name ON ProductVariantEntity(name)
        """.trimIndent())

        // Rename Item
        db.execSQL(
            """
            CREATE TABLE ItemEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionEntityId INTEGER NOT NULL,
                productEntityId INTEGER NOT NULL,
                productVariantEntityId INTEGER,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionEntityId) REFERENCES TransactionEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(productEntityId) REFERENCES ProductEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                FOREIGN KEY(productVariantEntityId) REFERENCES ProductVariantEntity(id) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
        """.trimIndent())

        db.execSQL(
            """
            INSERT INTO ItemEntity (id, transactionEntityId, productEntityId, productVariantEntityId, quantity, price)
            SELECT id, transactionBasketId, productId, variantId, quantity, price
            FROM Item
        """.trimIndent())

        db.execSQL(
            """
            DROP TABLE Item
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionEntityId ON ItemEntity(transactionEntityId)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productEntityId ON ItemEntity(productEntityId)
        """.trimIndent())

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productVariantEntityId ON ItemEntity(productVariantEntityId)
        """.trimIndent())

        db.execSQL(
            """
            CREATE VIEW `ItemView` AS SELECT
                ItemEntity.id               AS id,
                ProductEntity.id            AS productId,
                productCategoryEntity.id    AS productCategoryId,
                productProducerEntity.id    AS productProducerId,
                productVariantEntity.id     AS productVariantId,
                TransactionEntity.id        AS transactionId,
                ShopEntity.id               AS shopId,
                ItemEntity.quantity         AS quantity,
                ItemEntity.price            AS price,
                TransactionEntity.date      AS date,
                ProductEntity.name          AS productName,
                ProductVariantEntity.name   AS productVariantName,
                ProductCategoryEntity.name  AS productCategoryName,
                ProductProducerEntity.name  AS productProducerName,
                ShopEntity.name             AS shopName
            FROM ItemEntity
            LEFT JOIN ProductEntity         ON ProductEntity.id         = ItemEntity.productEntityId
            LEFT JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
            LEFT JOIN ProductProducerEntity ON ProductProducerEntity.id = ProductEntity.productProducerEntityId
            LEFT JOIN ProductVariantEntity  ON ProductVariantEntity.id  = ItemEntity.productVariantEntityId
            LEFT JOIN TransactionEntity     ON TransactionEntity.id     = ItemEntity.transactionEntityId 
            LEFT JOIN ShopEntity            ON ShopEntity.id            = TransactionEntity.shopEntityId
        """.trimIndent())
    }
}
