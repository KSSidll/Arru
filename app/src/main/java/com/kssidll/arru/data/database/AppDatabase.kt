package com.kssidll.arru.data.database

import android.content.*
import androidx.datastore.preferences.core.*
import androidx.room.*
import androidx.room.migration.*
import androidx.sqlite.db.*
import com.kssidll.arru.*
import com.kssidll.arru.data.dao.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.preference.*
import java.io.*
import java.util.*

/**
 * default database name
 */
const val DATABASE_NAME: String = "arru_database.db"

/**
 * default database backup directory name
 */
const val DATABASE_BACKUP_DIRECTORY_NAME: String = "db_backups"

/**
 * default database backup prefix
 */
const val DATABASE_BACKUP_PREFIX: String = "bak_arru_db_"

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
    version = 5,
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
         * @param time time to stamp the database files with, current time by default
         * @return absolute path to created db backup file as [File]
         */
        fun saveDbBackup(
            context: Context,
            preferences: Preferences,
            time: Long = Calendar.getInstance().timeInMillis
        ): File {
            val backupDir = context.currentDbBackupDirectory(preferences)
            val currentDbFile = context.currentDbFile(preferences)
            val backupDbFile = File(backupDir.absolutePath.plus("/$DATABASE_BACKUP_PREFIX$time.db"))

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
            backupDbFile: File
        ) {
            if (backupDbFile.exists()) {
                val currentDbFile = context.currentDbFile(preferences)
                copy(
                    backupDbFile,
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
        fun deleteDbBackup(backupDbFile: File) {
            if (backupDbFile.exists()) {
                delete(backupDbFile)
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
        ): List<File> {
            val directory = context.currentDbBackupDirectory(preferences)

            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles { file ->
                    // return only files that are databases, ignore shm, wal and other files
                    file.name.endsWith(".db")
                }

                if (files != null) {
                    return files.toList()
                        .sortedDescending()
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
